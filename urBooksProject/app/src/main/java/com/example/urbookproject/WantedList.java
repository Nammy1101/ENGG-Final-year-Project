package com.example.urbookproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WantedList extends ActionBarActivity implements IAsyncHttpHandler {
    private SearchResultsBaseAdapter adapter;
    private ArrayList<BookDataWanted> bookDataWantedArray = new ArrayList<>();
    private ListView resultsList;
    private String deleteFromWantedListURL;
    private int deleteIndex;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wanted_list);

        UserData userData = ((MyAppUserID) this.getApplication()).getUserData();
        String getWantedListURL = getString(R.string.server_url) + "getWantedList.php";

        HttpPostAsyncTask getListTask = new HttpPostAsyncTask(WantedList.this);
        getListTask.execute(getWantedListURL, "user_id", userData.getUserID());

        deleteFromWantedListURL = getString(R.string.server_url) + "deleteFromWantedList.php";

        resultsList = (ListView) findViewById(R.id.wanted_list);
        resultsList.setOnItemClickListener(new OnResultsListItemClickListener("WantedList"));
        resultsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
                                           long id) {
                deleteIndex = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(WantedList.this);
                builder.setCancelable(true);
                builder.setTitle("Delete \'" +
                        bookDataWantedArray.get(position).getTitle() + "\'?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        HttpPostAsyncTask deleteTask = new HttpPostAsyncTask(WantedList.this);
                        deleteTask.execute(deleteFromWantedListURL,
                                "wanted_id", bookDataWantedArray.get(position).getWantedID());
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
            // deleteFromWantedList.php was called

            if (json.equals("Successfully removed book!")) {
                Toast.makeText(getApplicationContext(), json, Toast.LENGTH_SHORT).show();
                bookDataWantedArray.remove(deleteIndex);
                adapter.notifyDataSetChanged();
            }
        } else {
            // getWantedList.php was called
            try {
                BookDataWanted bookDataWanted;
                JSONObject jsonResponse = new JSONObject(json);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");

                bookDataWantedArray = new ArrayList<>();

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    bookDataWanted = new BookDataWanted(jsonChildNode.getString("author"),
                            jsonChildNode.getString("book_id"),
                            jsonChildNode.getString("has_cover"),
                            jsonChildNode.getString("isbn10"),
                            jsonChildNode.getString("isbn13"),
                            jsonChildNode.getString("title"),
                            jsonChildNode.getString("year"),
                            jsonChildNode.getString("wanted_id"),
                            jsonChildNode.getString("trade"),
                            jsonChildNode.getString("purchase"));
                    bookDataWantedArray.add(bookDataWanted);
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }

            adapter = new SearchResultsBaseAdapter(WantedList.this, R.layout.layout_wanted_results,
                    bookDataWantedArray);
            resultsList.setAdapter(adapter);
        }
    }
}
