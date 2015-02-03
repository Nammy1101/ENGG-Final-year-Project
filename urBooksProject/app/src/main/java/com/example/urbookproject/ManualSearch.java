package com.example.urbookproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ManualSearch extends ActionBarActivity {

    EditText bookAuthor, bookTitle, bookYear;
    List<NameValuePair> nameValuePairs;
    String responseTitle, responseISBN10, responseISBN13, responseYear, responseAuthor,
            responseBookID;
    int ID;
    private String url;
    // private String response;
    private String jsonResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_search);

        Intent intent = getIntent();
        ID = intent.getIntExtra("USER_ID", 0);

        url = getString(R.string.server_url) + "manualUpload.php";

        bookAuthor = (EditText) findViewById(R.id.book_author);
        bookTitle = (EditText) findViewById(R.id.book_title);
        bookYear = (EditText) findViewById(R.id.book_year);

        Button search = (Button) findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (bookAuthor.getText().toString() == null
                        && bookTitle.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "Must have Title or Author",
                            Toast.LENGTH_SHORT).show();
                } else {
                    SearchForBook();
                }
            }
        });
        /*
        if(((MyAppUserID) this.getApplication()).getUserID() == 1){
        	 Toast.makeText(getApplicationContext(), "it works!",
                     Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manual_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void SearchForBook() {
        SearchInServer task = new SearchInServer();
        task.execute(new String[]{
                url
        });
    }

    public void ReadHttpResponse() {
        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");

            Intent intent = new Intent(this, SearchResults.class);
            intent.putExtra("SEARCH_RESULTS", jsonMainNode.toString());
            intent.putExtra("USER_ID", ID);

            startActivity(intent);

            /*
             * for (int i = 0; i < jsonMainNode.length(); i++) { JSONObject jsonChildNode =
             * jsonMainNode.getJSONObject(i); responseTitle =
             * jsonChildNode.optString("Book_Title").trim(); }
             */
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        // Toast.makeText(getApplicationContext(),
        // responseTitle , Toast.LENGTH_LONG).show();

        // Need to put code here for the response back from the server about the book data
    }

    private class SearchInServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            nameValuePairs = new ArrayList<NameValuePair>(3);

            nameValuePairs.add(new BasicNameValuePair("bookTitle", bookTitle.getText().toString()
                    .trim()));
            nameValuePairs.add(new BasicNameValuePair("bookAuthor", bookAuthor.getText().toString()
                    .trim()));
            nameValuePairs.add(new BasicNameValuePair("bookYear", bookYear.getText().toString()
                    .trim()));

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            ReadHttpResponse();
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                // e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }
    }
}
