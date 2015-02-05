package com.example.urbookproject;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rstancu on 2/3/2015.
 */
public class HttpPostAsyncTask extends AsyncTask<String, Void, String> {
    IAsyncHttpHandler handler = null;

    public HttpPostAsyncTask(IAsyncHttpHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... params) {
        int length = params.length;
        String url = params[0];  // url to the execute the PHP
        String result = "";
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(length - 1);

        for (int i = 1; i + 1 < length; i += 2) {
            nameValuePairs.add(new BasicNameValuePair(params[i].trim(), params[i + 1].trim()));
        }

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        handler.onPostExec(result);  // override onPostExec() in Activity
    }
}
