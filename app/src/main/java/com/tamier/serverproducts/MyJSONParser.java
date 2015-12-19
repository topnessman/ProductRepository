package com.tamier.serverproducts;

/**
 * Created by tamier on 13/11/15.
 */

        import java.io.InputStream;
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.UnsupportedEncodingException;
        import java.util.List;

        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.client.utils.URLEncodedUtils;
        import org.apache.http.conn.ClientConnectionManager;
        import org.apache.http.conn.ConnectTimeoutException;
        import org.apache.http.conn.scheme.PlainSocketFactory;
        import org.apache.http.conn.scheme.Scheme;
        import org.apache.http.conn.scheme.SchemeRegistry;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
        import org.apache.http.params.BasicHttpParams;
        import org.apache.http.params.HttpConnectionParams;
        import org.apache.http.params.HttpParams;
        import org.apache.http.params.HttpProtocolParams;
        import org.apache.http.protocol.HTTP;
        import org.json.JSONException;
        import org.json.JSONObject;

        import android.util.Log;
        import android.widget.Toast;

public class MyJSONParser {

    static InputStream mInputStream ;
    static InputStreamReader mInputStreamReader;
    static BufferedReader mBufferedReader;
    static StringBuilder mStringBuilder;
    static String line = null;
    static String preJSON = null;
    static JSONObject mJSONObject;
    public MyJSONParser() {

    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject HttpPost(String url,List<NameValuePair> params){



        // Making HTTP request
        try {
            Log.i("tamier log","at least here is ok.");

            HttpParams mHttpParams=new BasicHttpParams();
            HttpProtocolParams.setContentCharset(mHttpParams, HTTP.UTF_8);
            HttpConnectionParams.setConnectionTimeout(mHttpParams, 3000);
            HttpConnectionParams.setSoTimeout(mHttpParams, 3000);

            SchemeRegistry mSchemeRegistry=new SchemeRegistry();
            mSchemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(),80));
            mSchemeRegistry.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 433));

            ClientConnectionManager mClientConnectionManage=new ThreadSafeClientConnManager(mHttpParams,mSchemeRegistry);
                HttpClient httpClient = new DefaultHttpClient(mClientConnectionManage,mHttpParams);
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                Log.i("tamier log", "at least here is ok2.");
            //problem occurs here!
                HttpResponse httpResponse = httpClient.execute(httpPost);
                Log.i("tamier log","httpResponse is ok!");
                HttpEntity httpEntity = httpResponse.getEntity();
                mInputStream = httpEntity.getContent();
                Log.i("tamier log","at least here is ok3.");
                //minputStream is byte non-Unicode stream
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //preparation
            mInputStreamReader=new InputStreamReader(mInputStream,"iso-8859-1");
            mBufferedReader = new BufferedReader(mInputStreamReader,8);
            mStringBuilder= new StringBuilder();
            //read
            while ((line = mBufferedReader.readLine()) != null) {
                mStringBuilder.append(line + "\n");
            }
            //finishes reading,close InputStream
            mInputStream.close();
            //StringBuilder calls toString() gets to String
            preJSON = mStringBuilder.toString();
        } catch (Exception e) {
            Log.e("tamier log", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            Log.i("tamier log","reached here");
            Log.i("tamier log",preJSON);
            mJSONObject = new JSONObject(preJSON);


        } catch (Exception e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // return JSON String
        return mJSONObject;


    }
}
