
package com.example.urbookproject;

import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.os.AsyncTask;
import android.os.Bundle;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WantedList extends ActionBarActivity {
    List<NameValuePair> nameValuePairs;
    int ID;

    ListView resultsList;
    ArrayList<String> titleArray = new ArrayList<String>();
    ArrayList<String> authorArray = new ArrayList<String>();
    ArrayList<String> yearArray = new ArrayList<String>();
    ArrayList<String> bookID = new ArrayList<String>();
    SearchResultsBaseAdapter adapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wanted_list);

        ID = ((MyAppUserID) this.getApplication()).getUserID();
        resultsList = (ListView) findViewById(R.id.wanted_list);

        GetWantedListAsyncTask task = new GetWantedListAsyncTask();
        task.execute();
    }

    private class GetWantedListAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String result = "";
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("UserID", Integer.toString(ID)));

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.server_url) + "getWantedList.php");
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                result = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");
                result = jsonMainNode.toString();

                jsonMainNode = new JSONArray(result);
                for (int i = 0; i < jsonMainNode.length(); i++) {
                    try {
                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                        titleArray.add(jsonChildNode.getString("Book_Title").toString());
                        authorArray.add(jsonChildNode.getString("Book_Author").toString());
                        yearArray.add(jsonChildNode.getString("Book_Year").toString());
                        bookID.add(jsonChildNode.getString("Book_ID").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter = new SearchResultsBaseAdapter(WantedList.this,
                        R.layout.layout_search_results, titleArray, authorArray, yearArray, bookID,
                        ID);
                resultsList.setAdapter(adapter);
                resultsList.setOnItemClickListener(new OnResultsListItemClickListener());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
