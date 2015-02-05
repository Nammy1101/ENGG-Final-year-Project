package com.example.urbookproject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchResults extends ActionBarActivity {
    private ArrayList<BookData> bookDataArray = new ArrayList<>();
    private SearchResultsBaseAdapter adapter;
    private ListView resultsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        resultsList = (ListView) findViewById(R.id.search_results);

        /* Grab data sent from ManualSearch activity */
        bookDataArray = new ArrayList<>();
        bookDataArray = getIntent().getParcelableArrayListExtra("bookData");

        adapter = new SearchResultsBaseAdapter(SearchResults.this, R.layout.layout_search_results,
                bookDataArray);
        resultsList.setAdapter(adapter);
        resultsList.setOnItemClickListener(new OnResultsListItemClickListener("SearchResults",
                bookDataArray));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_results, menu);
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
