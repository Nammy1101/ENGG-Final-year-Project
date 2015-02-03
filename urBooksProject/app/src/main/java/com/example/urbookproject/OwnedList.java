package com.example.urbookproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

public class OwnedList extends ActionBarActivity {
    List<NameValuePair> nameValuePairs;
    int ID;

    ListView resultsList;
    BookList list = new BookList();
    ArrayList<String> titleArray = new ArrayList<String>();
    ArrayList<String> authorArray = new ArrayList<String>();
    ArrayList<String> yearArray = new ArrayList<String>();
    ArrayList<String> bookID = new ArrayList<String>();
    ArrayList<String> ownedID = new ArrayList<String>();
    SearchResultsBaseAdapter adapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owned_list);

        ID = ((MyAppUserID) this.getApplication()).getUserID();
        resultsList = (ListView) findViewById(R.id.owned_list);

        GetOwnedListAsyncTask task = new GetOwnedListAsyncTask();
        task.execute();

        resultsList.setOnItemClickListener(new OnResultsListItemClickListener("OwnedList"));
        //resultsList.setOnItemLongClickListener(new OnResultsListItemClickListener("OwnedList", list));
        resultsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OwnedList.this);
                builder.setCancelable(true);
                builder.setTitle("Delete \'" + list.title.get(position) + "\'?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        //adapter.remove(adapter.getItem(index));
                        DeleteFromOwnedListAsyncTask deltask = new DeleteFromOwnedListAsyncTask(
                                list.owned_id.get(position), position);
                        deltask.execute();
                        dialog.dismiss();
                    }
                });
                builder.show();

                return true;
            }
        });
    }

    private class GetOwnedListAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String result = "";
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("UserID", Integer.toString(ID)));

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.server_url) + "getOwnedList.php");
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
                        //titleArray.add(jsonChildNode.getString("Book_Title").toString());
                        //authorArray.add(jsonChildNode.getString("Book_Author").toString());
                        //yearArray.add(jsonChildNode.getString("Book_Year").toString());
                        //bookID.add(jsonChildNode.getString("Book_ID").toString());
                        //ownedID.add(jsonChildNode.getString("Owned_ID").toString());

                        list.title.add(jsonChildNode.getString("Book_Title").toString());
                        list.author.add(jsonChildNode.getString("Book_Author").toString());
                        list.year.add(jsonChildNode.getString("Book_Year").toString());
                        list.book_id.add(jsonChildNode.getString("Book_ID").toString());
                        list.owned_id.add(jsonChildNode.getString("Owned_ID").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //adapter = new SearchResultsBaseAdapter(OwnedList.this, R.layout.layout_owned_results, titleArray, authorArray, yearArray, bookID, ID);
                adapter = new SearchResultsBaseAdapter(OwnedList.this, R.layout.layout_owned_results, ID, list);
                resultsList.setAdapter(adapter);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class DeleteFromOwnedListAsyncTask extends AsyncTask<String, Void, String> {
        private String owned_id;
        private int position;

        public DeleteFromOwnedListAsyncTask(String owned_id, int position) {
            this.owned_id = owned_id;
            this.position = position;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String result = "";
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("Owned_ID", owned_id));

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.server_url) + "deleteFromOwnedList.php");
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

            if (result.equals("Successfully removed book!")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                list.remove(position);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
