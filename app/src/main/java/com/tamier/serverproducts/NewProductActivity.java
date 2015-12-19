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
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewProductActivity extends Activity {

    EditText et_name;
    EditText et_price;
    EditText et_description;
    Button btn_add;
    Button btn_cancel;
    MyJSONParser mMyJSONParser;
    String name,price,description;

    private final String TAG_SUCESS="success";
    private final String TAG_PRODUCTS="product";
    private final String TAG_PRICE="price";
    private final String TAG_DESCRIPTION="description";
    private final String TAG_PID="pid";
    private final String TAG_NAME="name";
    private final String url="http://192.168.43.245/product/create_product.php";


    private void init(){
        et_name=(EditText)findViewById(R.id.et_name);
        et_price=(EditText)findViewById(R.id.et_price);
        et_description=(EditText)findViewById(R.id.et_description);
        btn_add=(Button)findViewById(R.id.btn_add);
        btn_cancel=(Button)findViewById(R.id.btn_cancel);
        mMyJSONParser=new MyJSONParser();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        init();
        name=null;
        price=null;
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = et_name.getText().toString();
                price = et_price.getText().toString();
                description = et_description.getText().toString();
                try {
                    if (name.matches("") || price.matches("")) {
                        throw new Exception();
                    }
                    add();
                }catch(Exception e){
                    Toast.makeText(NewProductActivity.this,"name or price cannot be void!",Toast.LENGTH_SHORT).show();

                }

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    protected void add(){
        new AddNewProduct().execute();
    }

    protected class AddNewProduct extends AsyncTask<String,String,String>{
        int success;
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> param=new ArrayList<NameValuePair>();

            BasicNameValuePair mBasicNameValuePair_name=new BasicNameValuePair(TAG_NAME,name);
            BasicNameValuePair mBasicNameValuePair_price=new BasicNameValuePair(TAG_PRICE,price);
            BasicNameValuePair mBasicNameValuePair_description=new BasicNameValuePair(TAG_DESCRIPTION,description);

            param.add(mBasicNameValuePair_name);
            param.add(mBasicNameValuePair_price);
            param.add(mBasicNameValuePair_description);
            try {
                JSONObject mJSONObject=mMyJSONParser.HttpPost(url,param);
                if (mJSONObject!=null){
                    success=mJSONObject.getInt(TAG_SUCESS);
                }
                else{
                    throw new Exception();
                }
            }catch (JSONException e){
                Log.e("tamier log","JSONException occrued!");
            }catch (Exception e){
                Log.e("tamier log","Exception occured!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Runnable mRunnable=new Runnable() {
                @Override
                public void run() {
                    if (success==1){
                        Toast.makeText(NewProductActivity.this,"Add succeeds!",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Toast.makeText(NewProductActivity.this,"Add failed!",Toast.LENGTH_SHORT).show();
                    }
                }
            };
            runOnUiThread(mRunnable);

        }
    }
}
