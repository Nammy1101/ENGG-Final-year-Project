package com.example.urbookproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ManualSearch extends ActionBarActivity implements IAsyncHttpHandler {
    private ArrayList<BookData> bookDataArray = new ArrayList<>();
    private EditText bookAuthor, bookTitle, bookYear, bookISBN;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_search);

        url = getString(R.string.server_url) + "ManualSearch.php";

        bookAuthor = (EditText) findViewById(R.id.book_author);
        bookTitle = (EditText) findViewById(R.id.book_title);
        bookYear = (EditText) findViewById(R.id.book_year);
        bookISBN = (EditText) findViewById(R.id.book_isbn);

        bookAuthor.clearFocus();
        bookTitle.clearFocus();
        bookYear.clearFocus();
        bookISBN.clearFocus();

        Button search = (Button) findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validInput()) {
                    HttpPostAsyncTask task = new HttpPostAsyncTask(ManualSearch.this);
                    task.execute(url,
                            "title", bookTitle.getText().toString(),
                            "author", bookAuthor.getText().toString(),
                            "year", bookYear.getText().toString(),
                            "isbn", bookISBN.getText().toString());
                }
            }
        });
    }

    public boolean validInput() {
        String title = bookTitle.getText().toString();
        String author = bookAuthor.getText().toString();
        String isbn = bookISBN.getText().toString();

        if ( (title == null || title.equals("")) && (author == null || author.equals(""))
                && (isbn == null || isbn.equals(""))) {
            Toast.makeText(getApplicationContext(), "Please enter a Title, Author, or ISBN.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (isbn != null && !isbn.equals("") && isbn.length() != 10 && isbn.length() != 13) {
            Toast.makeText(getApplicationContext(), "ISBN must be 10 or 13 characters long.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onPostExec(String json) {
        if (json.equals("")) {
            Toast.makeText(getApplicationContext(), "No results...",
                    Toast.LENGTH_LONG).show();
            return;
        }

        try {
            BookData bookData;
            JSONObject jsonResponse = new JSONObject(json);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");

            bookDataArray = new ArrayList<>();

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                bookData = new BookData(jsonChildNode.getString("author"),
                        jsonChildNode.getString("book_id"),
                        jsonChildNode.getString("has_cover"),
                        jsonChildNode.getString("isbn10"),
                        jsonChildNode.getString("isbn13"),
                        jsonChildNode.getString("title"),
                        jsonChildNode.getString("year"));

                bookDataArray.add(bookData);
            }

            /* Send bookDataArray to SearchResults and switch to it */
            Intent intent = new Intent(this, SearchResults.class);
            intent.putParcelableArrayListExtra("bookData", bookDataArray);
            startActivity(intent);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error communicating with the server.",
                    Toast.LENGTH_LONG).show();
        }
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
}
