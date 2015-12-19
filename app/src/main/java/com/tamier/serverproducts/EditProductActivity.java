package com.tamier.serverproducts;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class EditProductActivity extends Activity {

    EditText et_name;
    EditText et_price;
    EditText et_description;
    TextView tv_pid;
    Button btn_update;
    Button btn_cancel;
    MyJSONParser mMyJSONParser;
    String pid;
    String name,price,description;
    Intent minEditIntent;

    private final String TAG_SUCESS="success";
    private final String TAG_PRODUCTS="product";
    private final String TAG_PRICE="price";
    private final String TAG_DESCRIPTION="description";
    private final String TAG_PID="pid";
    private final String TAG_NAME="name";
    private final String url="http://192.168.43.245/product/get_product_details.php";
    private final String url_update="http://192.168.43.245/product/update_product.php";

    private void init(){
        et_name=(EditText)findViewById(R.id.et_name);
        et_price=(EditText)findViewById(R.id.et_price);
        et_description=(EditText)findViewById(R.id.et_description);
        tv_pid=(TextView)findViewById(R.id.tv_pid);
        btn_update=(Button)findViewById(R.id.btn_update);
        btn_cancel=(Button)findViewById(R.id.btn_cancel);
        mMyJSONParser=new MyJSONParser();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        init();

        minEditIntent=getIntent();
        //要取得Extra里面的字符串，要用getStringExtra(TAG_PID),而不是getStringExtras(TAG_PID)（本身即是错误用法）
        pid= minEditIntent.getStringExtra(TAG_PID);
        getandshow();
        Log.i("tamier log", pid);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pid = tv_pid.getText().toString();
                name = et_name.getText().toString();
                price = et_price.getText().toString();
                description = et_description.getText().toString();
                try{
                    if (name.matches("") || price.matches("")) {
                        throw new Exception();
                    }
                    update();
                }catch(Exception e){
                    Toast.makeText(EditProductActivity.this,"name or price cannot be void!",Toast.LENGTH_SHORT).show();

                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(101,minEditIntent);
                finish();
            }
        });

    }
    protected void getandshow(){
        new GetSpecificProduct().execute();
    }
    protected void update(){
        new UpdateSpecificProduct().execute();
    }
    protected class GetSpecificProduct extends AsyncTask<String,String,String>{

        String hiddenpid,name,price,description;
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> param=new ArrayList<NameValuePair>();
            BasicNameValuePair mBasicNameValuePair=new BasicNameValuePair(TAG_PID,pid);
            param.add(mBasicNameValuePair);
            try {
                JSONObject mJSONObject = mMyJSONParser.HttpPost(url, param);
                if(mJSONObject!=null) {
                    int success=mJSONObject.getInt(TAG_SUCESS);
                    Log.d("tamier log","success?"+String.valueOf(success));
                    if(success==1) {
                        JSONArray mJSONArray = mJSONObject.getJSONArray(TAG_PRODUCTS);
                        Log.i("tamier log","JSONArray fetching succeeds");
                        JSONObject mJSONSpecificObject=mJSONArray.getJSONObject(0);
                        name=mJSONSpecificObject.getString(TAG_NAME);
                        price=mJSONSpecificObject.getString(TAG_PRICE);
                        description=mJSONSpecificObject.getString(TAG_DESCRIPTION);
                        hiddenpid=mJSONSpecificObject.getString(TAG_PID);
                        Log.d("tamier log",name);
                        Log.d("tamier log",price);
                        Log.d("tamier log",description);
                        Log.d("tamier log",hiddenpid);

                    }

                }
                else{
                    throw new Exception();
                }
            }catch (JSONException e){
                Log.e("tamier log","JSONException occured!");
            }catch (Exception e){
                Runnable mRunnable=new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EditProductActivity.this,"Failed to fetch this item!",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                };
                runOnUiThread(mRunnable);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Runnable mRunnable2=new Runnable() {
                @Override
                public void run() {
                    et_name.setText(name);
                    et_price.setText(price);
                    et_description.setText(description);
                    tv_pid.setText(hiddenpid);
                }
            };
            runOnUiThread(mRunnable2);
        }
    }

    protected class UpdateSpecificProduct extends AsyncTask<String,String,String>{
        JSONObject mJSONObject;
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> param=new ArrayList<NameValuePair>();
            BasicNameValuePair mBasicNameValuePair_pid=new BasicNameValuePair(TAG_PID,pid);
            BasicNameValuePair mBasicNameValuePair_name=new BasicNameValuePair(TAG_NAME,name);
            BasicNameValuePair mBasicNameValuePair_price=new BasicNameValuePair(TAG_PRICE,price);
            BasicNameValuePair mBasicNameValuePair_description=new BasicNameValuePair(TAG_DESCRIPTION,description);
            param.add(mBasicNameValuePair_pid);
            param.add(mBasicNameValuePair_name);
            param.add(mBasicNameValuePair_price);
            param.add(mBasicNameValuePair_description);

            try{
                mJSONObject=mMyJSONParser.HttpPost(url_update,param);
                if (mJSONObject==null){
                    throw new Exception();
                }

            }catch (JSONException e){
                Log.e("tamier log","JSONException occured!");
            }
            catch (Exception e ){
                Log.e("tamier log","Error fetching JSONObject");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Runnable mRunnable=new Runnable() {
                @Override
                public void run() {
                    try {
                        int success = mJSONObject.getInt(TAG_SUCESS);
                        if (success == 1) {
                            Toast.makeText(EditProductActivity.this,"Update succeeds!",Toast.LENGTH_SHORT).show();
                            setResult(100, minEditIntent);
                            finish();
                        }
                        else{
                            Toast.makeText(EditProductActivity.this,"Update failed!",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException E){
                        Log.e("tamier log","JSONException occured!");
                    }
                }
            };
            runOnUiThread(mRunnable);


        }
    }
}
