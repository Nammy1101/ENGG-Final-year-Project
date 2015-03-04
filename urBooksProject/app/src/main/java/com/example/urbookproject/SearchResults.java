package com.example.urbookproject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class SearchResults extends ActionBarActivity {
    private ArrayList<BookData> bookDataArray = new ArrayList<>();
    private SearchResultsBaseAdapter adapter;
    private EditText filterEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        ListView resultsList = (ListView) findViewById(R.id.search_results);

        /* Grab data sent from ManualSearch activity */
        bookDataArray = new ArrayList<>();
        bookDataArray = getIntent().getParcelableArrayListExtra("bookData");

        adapter = new SearchResultsBaseAdapter(SearchResults.this, R.layout.layout_search_results,
                bookDataArray);
        resultsList.setAdapter(adapter);
        resultsList.setOnItemClickListener(new OnResultsListItemClickListener("SearchResults",
                bookDataArray));

        filterEditText = (EditText) findViewById(R.id.book_search_filter);
        filterEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //System.out.println("Filter text: [" + s + "]");
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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
        String sortType;

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.search_results_title_asc:
                sortType = "titleasc";
                break;
            case R.id.search_results_title_dsc:
                sortType = "titledesc";
                break;
            case R.id.search_results_author_asc:
                sortType = "authorasc";
                break;
            case R.id.search_results_author_dsc:
                sortType = "authordesc";
                break;
            case R.id.search_results_year_asc:
                sortType = "yearasc";
                break;
            case R.id.search_results_year_dsc:
                sortType = "yeardesc";
                break;
            default:
                return true;
        }

        Collections.sort(bookDataArray,
                new SearchResultsBaseAdapter.SearchResultsComparator(sortType));
        adapter.notifyDataSetChanged();
        adapter.getFilter().filter(filterEditText.getText().toString());

        return super.onOptionsItemSelected(item);
    }
}
