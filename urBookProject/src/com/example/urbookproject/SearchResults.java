
package com.example.urbookproject;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResults extends ActionBarActivity {

    int ID;
    String jsonArray;

    ListView resultsList;
    ArrayList<String> titleArray = new ArrayList<String>();
    ArrayList<String> authorArray = new ArrayList<String>();
    ArrayList<String> yearArray = new ArrayList<String>();
    ArrayList<String> bookID = new ArrayList<String>();
    SearchResultsBaseAdapter adapter;

    // TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        
        Intent intent = getIntent();
        ID = intent.getIntExtra("USER_ID", 0);

        resultsList = (ListView) findViewById(R.id.search_results);
        // tv = (TextView) findViewById(R.id.textView1);

        jsonArray = intent.getStringExtra("SEARCH_RESULTS");
        // tv.setText(jsonArray);

        try {
            JSONArray array = new JSONArray(jsonArray);
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject jsonChildNode = array.getJSONObject(i);
                    // responseTitle = jsonChildNode.optString("Book_Title").trim();
                    titleArray.add(jsonChildNode.getString("Book_Title").toString());
                    authorArray.add(jsonChildNode.getString("Book_Author").toString());
                    yearArray.add(jsonChildNode.getString("Book_Year").toString());
                    bookID.add(jsonChildNode.getString("Book_ID").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter = new SearchResultsBaseAdapter(SearchResults.this, titleArray, authorArray, yearArray, bookID, ID);
            resultsList.setAdapter(adapter);
            resultsList.setOnItemClickListener(new OnResultsListItemClickListener());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

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
