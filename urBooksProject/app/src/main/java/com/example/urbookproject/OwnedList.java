package com.example.urbookproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class OwnedList extends ActionBarActivity implements IAsyncHttpHandler {
    private SearchResultsBaseAdapter adapter;
    private ArrayList<BookDataOwned> bookDataOwnedArray = new ArrayList<>();
    private ListView resultsList;
    private String deleteFromOwnedListURL;
    private int deleteIndex;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owned_list);

        UserData userData = ((MyAppUserData) this.getApplication()).getUserData();
        String getOwnedListURL = getString(R.string.server_url) + "getOwnedList.php";

        HttpPostAsyncTask getListTask = new HttpPostAsyncTask(OwnedList.this);
        getListTask.execute(getOwnedListURL, "user_id", userData.getUserID());

        deleteFromOwnedListURL = getString(R.string.server_url) + "deleteFromOwnedList.php";

        resultsList = (ListView) findViewById(R.id.owned_list);
        resultsList.setOnItemClickListener(new OnResultsListItemClickListener("OwnedList"));
        resultsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
                                           long id) {
                deleteIndex = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(OwnedList.this);
                builder.setCancelable(true);
                builder.setTitle("Delete \'" +
                        bookDataOwnedArray.get(position).getTitle() + "\'?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        HttpPostAsyncTask deleteTask = new HttpPostAsyncTask(OwnedList.this);
                        deleteTask.execute(deleteFromOwnedListURL,
                                "owned_id", bookDataOwnedArray.get(position).getOwnedID());
                        dialog.dismiss();
                    }
                });
                builder.show();

                return true;
            }
        });
    }

    @Override
    public void onPostExec(String json) {
        if (json.equals("Successfully removed book!") || json.equals("Could not delete.")) {
            // deleteFromOwnedList.php was called

            if (json.equals("Successfully removed book!")) {
                Toast.makeText(getApplicationContext(), json, Toast.LENGTH_SHORT).show();
                bookDataOwnedArray.remove(deleteIndex);
                adapter.notifyDataSetChanged();
            }
        } else {
            // getOwnedList.php was called
            try {
                BookDataOwned bookDataOwned;
                JSONObject jsonResponse = new JSONObject(json);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");

                bookDataOwnedArray = new ArrayList<>();

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    bookDataOwned = new BookDataOwned(jsonChildNode.getString("author"),
                            jsonChildNode.getString("book_id"),
                            jsonChildNode.getString("has_cover"),
                            jsonChildNode.getString("isbn10"),
                            jsonChildNode.getString("isbn13"),
                            jsonChildNode.getString("title"),
                            jsonChildNode.getString("year"),
                            jsonChildNode.getString("owned_id"),
                            jsonChildNode.getString("keep"),
                            jsonChildNode.getString("trade"),
                            jsonChildNode.getString("sell"));
                    bookDataOwnedArray.add(bookDataOwned);
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }

            adapter = new SearchResultsBaseAdapter(OwnedList.this, R.layout.layout_owned_results,
                    bookDataOwnedArray);
            resultsList.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.owned_list, menu);
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
            case R.id.owned_list_title_asc:
                sortType = "titleasc";
                break;
            case R.id.owned_list_title_dsc:
                sortType = "titledesc";
                break;
            case R.id.owned_list_author_asc:
                sortType = "authorasc";
                break;
            case R.id.owned_list_author_dsc:
                sortType = "authordesc";
                break;
            case R.id.owned_list_year_asc:
                sortType = "yearasc";
                break;
            case R.id.owned_list_year_dsc:
                sortType = "yeardesc";
                break;
            default:
                return true;
        }

        Collections.sort(bookDataOwnedArray,
                new SearchResultsBaseAdapter.SearchResultsComparator(sortType));
        adapter.notifyDataSetChanged();

        return super.onOptionsItemSelected(item);
    }
}
