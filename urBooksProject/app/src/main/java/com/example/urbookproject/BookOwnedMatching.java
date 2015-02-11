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
    private SearchResultsBaseAdapter adapter;
    private String url;
    private ArrayList<BookDataMatch> bookDataMatchArray = new ArrayList<>();
   // private ArrayList<BookDataWanted> bookDataMatchArray = new ArrayList<>();
    private ListView resultsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_owned_matching);

        url = getString(R.string.server_url) + "MatchOwnedBooks.php";

        UserData userData = ((MyAppUserID) this.getApplication()).getUserData();

        HttpPostAsyncTask getListTask = new HttpPostAsyncTask(BookOwnedMatching.this);
        getListTask.execute(url, "user_id", userData.getUserID());

        resultsList = (ListView) findViewById(R.id.matching_owned_results);
    }


    @Override
    public void onPostExec(String json) {
        String test = "";
        int test1 = 0;
        try {

            BookDataMatch bookDataMatch;
         //   BookDataWanted bookDataMatch;
            JSONObject jsonResponse = new JSONObject(json);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");

            bookDataMatchArray = new ArrayList<>();

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                test = jsonChildNode.getString("isbn10");
                bookDataMatch = new BookDataMatch(jsonChildNode.getString("author"),
                        jsonChildNode.getString("book_id"),
                        jsonChildNode.getString("has_cover"),
                        jsonChildNode.getString("isbn10"),
                        jsonChildNode.getString("isbn13"),
                        jsonChildNode.getString("title"),
                        jsonChildNode.getString("year"),
                        jsonChildNode.getString("user_id"));
                bookDataMatchArray.add(bookDataMatch);
            }

          test1 =  bookDataMatchArray.size();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "JSON Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        adapter = new SearchResultsBaseAdapter(BookOwnedMatching.this, R.layout.layout_match_results,
               bookDataMatchArray);
        resultsList.setAdapter(adapter);

      /*  StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(test1);

        Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();*/
      //  finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu., menu);
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
