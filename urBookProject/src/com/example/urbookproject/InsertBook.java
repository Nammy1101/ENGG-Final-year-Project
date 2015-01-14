
package com.example.urbookproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class InsertBook extends ActionBarActivity {

    String title, author, year, bookid;
    int ID;
    String selectedOption;
    private String url, jsonResult;
    List<NameValuePair> nameValuePairs;
    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_book);
        Intent intent = getIntent();
        title = intent.getStringExtra("BOOK_TITLE");
        author = intent.getStringExtra("BOOK_AUTHOR");
        year = intent.getStringExtra("BOOK_YEAR");
        bookid = intent.getStringExtra("BOOK_ID");
        ID = ((MyAppUserID) this.getApplication()).getUserID();

        url = getString(R.string.server_url) + "insertBook.php";

        selectedOption = null;

        TextView bookTitle = (TextView) findViewById(R.id.insert_book_title);
        TextView bookAuthor = (TextView) findViewById(R.id.insert_book_author);
        TextView bookYear = (TextView) findViewById(R.id.insert_book_year);
        ImageView bookCover = (ImageView) findViewById(R.id.insert_book_cover);

        bookTitle.setText(title);
        bookAuthor.setText(author);
        bookYear.setText(year);

        new DownloadImageTask(bookCover).execute(getString(R.string.server_url) + "covers/"
                + bookid + ".jpg");

        Button select = (Button) findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                insertBook();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.insert_book, menu);
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

    public void insertBook() {
        insertBookTask task = new insertBookTask();
        // passes values for the urls string array
        task.execute(new String[] {
                url
        });
    }

    private class insertBookTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("bookTitle", title));
            nameValuePairs.add(new BasicNameValuePair("bookAuthor", author));
            nameValuePairs.add(new BasicNameValuePair("bookYear", year));
            nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(ID)));
            nameValuePairs.add(new BasicNameValuePair("option_selected", selectedOption));

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

        @Override
        protected void onPostExecute(String result) {
            ReadHttpResponse();

            // if successfully logged in
            // goToHomeScreen();
        }
    }

    public void ReadHttpResponse() {
        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                response = jsonChildNode.optString("response").trim();
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getApplicationContext(), response,
                Toast.LENGTH_SHORT).show();

    }

    public void onOptionSelectedClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.wantedBook:
                if (checked) {
                    selectedOption = "want";
                }
                break;
            case R.id.ownedBook:
                if (checked) {
                    selectedOption = "owned";
                }
                break;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
