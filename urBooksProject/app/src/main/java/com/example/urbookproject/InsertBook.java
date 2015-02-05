package com.example.urbookproject;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class InsertBook extends ActionBarActivity implements IAsyncHttpHandler {
    private UserData userData = new UserData();
    private BookData bookData = new BookData();
    private String url, selectedOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_book);

        bookData = new BookData();
        bookData = getIntent().getParcelableExtra("bookData");
        userData = ((MyAppUserID) this.getApplication()).getUserData();

        url = getString(R.string.server_url) + "insertBook.php";

        selectedOption = null;

        TextView bookTitle = (TextView) findViewById(R.id.insert_book_title);
        TextView bookAuthor = (TextView) findViewById(R.id.insert_book_author);
        TextView bookYear = (TextView) findViewById(R.id.insert_book_year);
        ImageView bookCover = (ImageView) findViewById(R.id.insert_book_cover);

        bookTitle.setText(bookData.getTitle());
        bookAuthor.setText(bookData.getAuthor());
        bookYear.setText(bookData.getYear());
        new DownloadImageTask(bookCover).execute(getString(R.string.server_url) + "covers/"
                + bookData.getBookID() + ".jpg");

        Button select = (Button) findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpPostAsyncTask task = new HttpPostAsyncTask(InsertBook.this);
                task.execute(url,
                        "book_id", bookData.getBookID(),
                        "user_id", userData.getUserID(),
                        "option_selected", selectedOption);
            }
        });
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

    @Override
    public void onPostExec(String json) {
        String phpResponse = "No response...";

        try {
            JSONObject jsonResponse = new JSONObject(json);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                phpResponse = jsonChildNode.optString("response").trim();
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "JSON Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getApplicationContext(), phpResponse, Toast.LENGTH_SHORT).show();
        finish();
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
