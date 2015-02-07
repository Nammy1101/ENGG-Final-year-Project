package com.example.urbookproject;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class BookOwnedMatching extends ActionBarActivity implements IAsyncHttpHandler {

    private String url;
    private UserData userData = new UserData();
    private BookData bookData = new BookData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_owned_matching);

        url = getString(R.string.server_url) + "MatchOwnedBooks.php";

        userData = ((MyAppUserID) this.getApplication()).getUserData();

        HttpPostAsyncTask task = new HttpPostAsyncTask(BookOwnedMatching.this);
        task.execute(url,
                "user_id", userData.getUserID());
    }


    @Override
    public void onPostExec(String json) {
        String userID = "";
        String bookID = "";

        try {
            JSONObject jsonResponse = new JSONObject(json);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                userID = jsonChildNode.optString("user_id").trim();
                bookID = jsonChildNode.optString("book_id").trim();
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "JSON Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getApplicationContext(), userID, Toast.LENGTH_SHORT).show();
       // finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_owned_matching, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
