package com.tamier.serverproducts;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

public class AllProductsActivity extends ListActivity {

    protected ListView mListView;
    protected List<HashMap<String,String>> mListProducts;
    protected Dialog mDialog;
    protected MyJSONParser mMyJSONParser;
    protected View mSelectedChildView;
    protected String deletepid;


    protected final String url="http://192.168.43.245/product/get_all_products.php";
    protected final String url_delete="http://192.168.43.245/product/delete_product.php";
    private final String TAG_SUCESS="success";
    private final String TAG_PRODUCTS="products";
    private final String TAG_PID="pid";
    private final String TAG_NAME="name";

    protected void init(){
        mListView=getListView();
        mListProducts=new ArrayList<HashMap<String,String>>();
        mDialog=new Dialog(this);
        mMyJSONParser=new MyJSONParser();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);
        init();

        //execute asynctask to get all items from server
        render();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pid = ((TextView) view.findViewById(R.id.tv_pid)).getText().toString();
                Intent mEditIntent = new Intent(AllProductsActivity.this, EditProductActivity.class);
                mEditIntent.putExtra(TAG_PID, pid);
                startActivityForResult(mEditIntent, 100);
            }
        });
        registerForContextMenu(mListView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId()==android.R.id.list){
            getMenuInflater().inflate(R.menu.contextmenu,menu);
        }
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)menuInfo;
        mSelectedChildView=info.targetView;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_delete){
            //delete item whose pid is pid from remote server
            /*
            *
            *
            * */
            TextView tv_deletepid=(TextView)mSelectedChildView.findViewById(R.id.tv_pid);
            deletepid=tv_deletepid.getText().toString();
            delete();
            mListProducts.clear();
            render();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==100){
            mListProducts.clear();
            render();
        }
        if(resultCode==101){

        }
    }

    protected void render(){
        new LoadAllProducts().execute();
    }
    protected void delete(){
        new DeleteSpecificProduct().execute();
    }
    protected class DeleteSpecificProduct extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> param=new ArrayList<NameValuePair>();
            BasicNameValuePair mBasicNameValuePair_pid=new BasicNameValuePair(TAG_PID,deletepid);
            param.add(mBasicNameValuePair_pid);
            try{
                JSONObject mJSONObject=mMyJSONParser.HttpPost(url_delete,param);
                if(mJSONObject!=null){
                    int success=mJSONObject.getInt(TAG_SUCESS);
                    if(success==1){
                        Runnable mSuccessRunnable=new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AllProductsActivity.this,"Delete succeeds",Toast.LENGTH_SHORT).show();
                            }
                        };
                        runOnUiThread(mSuccessRunnable);

                    }
                    else{
                        Runnable mFailRunnable=new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AllProductsActivity.this,"Connect to server but Delete failed",Toast.LENGTH_SHORT).show();
                            }
                        };
                        runOnUiThread(mFailRunnable);
                    }
                }
                else{
                    throw new Exception();
                }
            }catch (ConcurrentModificationException e){
                Toast.makeText(AllProductsActivity.this,"Connect to server failed and delete failed",Toast.LENGTH_SHORT).show();
            }
            catch (JSONException e){
                Log.e("tamier log","JSONException occured!");
            }catch (Exception e){
                Log.e("tamier log","Couldn't access this pid and delete failed");
            }
            return null;
        }
    }
    protected class LoadAllProducts extends AsyncTask<String,String,String>{
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           mDialog.setTitle("Getting all products...");
           mDialog.setCancelable(false);
           mDialog.show();
       }

       @Override
       protected String doInBackground(String... params) {
           List<NameValuePair> param=new ArrayList<NameValuePair>();


           try{

               JSONObject mJSONObject=mMyJSONParser.HttpPost(url,param);
               //这句mJSONObject!=null太重要了！不加的话，万一没连上服务器，没得到mJSONObject，即mJSONObject为空的话， 从空的对象里提取
               //sucess会导致NullPointerException，并造成程序闪退。这是一定要避免的基本的情况，也是一种素养。
               if(mJSONObject!=null) {
                   int success = mJSONObject.getInt(TAG_SUCESS);
                   if (success == 1) {
                       JSONArray mJSONArray = mJSONObject.getJSONArray(TAG_PRODUCTS);
                       Integer i;

                       for (i = 0; i < mJSONArray.length(); i++) {
                           JSONObject mJOSONEachObject = mJSONArray.getJSONObject(i);
                           String mpid;
                           mpid = mJOSONEachObject.getString(TAG_PID);
                           String mname;
                           mname = mJOSONEachObject.getString(TAG_NAME);
                           HashMap<String, String> mHashMap = new HashMap<String, String>();
                           mHashMap.put(TAG_PID, mpid);
                           mHashMap.put(TAG_NAME, mname);
                           mListProducts.add(mHashMap);
                       }
                   }
               }
               else{
                   throw new ConnectTimeoutException();

               }
           }
           catch (JSONException e){
               Log.e("tamier log","JSONException throwd! Please check where is wrong.");
               //ir really throws connecttimeoutexception when unable to connect to server. but we handle this exception at the
               //topest layer,i.e. where call them
           } catch (ConnectTimeoutException e) {
               e.printStackTrace();
               Log.i("tamier log", "mJSONObject is empty!");
               //AsyncTask中，UI更新一定要在runOnUiThread中创建一个Runnable才行。否则得到runtimeerrer,并闪退。
               Runnable mRunnable2=new Runnable() {
                   @Override
                   public void run() {
                       mDialog.dismiss();
                       Toast.makeText(AllProductsActivity.this,"Error:Connect to server timeout",Toast.LENGTH_SHORT).show();
                       finish();
                   }
               };
               runOnUiThread(mRunnable2);

           }

           return null;
       }

       @Override
       protected void onPostExecute(String s) {
           super.onPostExecute(s);
           mDialog.dismiss();
           Runnable mRunnable=new Runnable() {
               @Override
               public void run() {
                   String[] from=new String[]{TAG_PID,TAG_NAME};
                   int[] to=new int[]{R.id.tv_pid,R.id.tv_name};
                   ListAdapter mListAdapter=new SimpleAdapter(AllProductsActivity.this,mListProducts,R.layout.list_item,from,to);
                   setListAdapter(mListAdapter);
               }
           };
           runOnUiThread(mRunnable);

       }
   }
}
