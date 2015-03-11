package com.example.urbookproject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookMatching extends ActionBarActivity implements IAsyncHttpHandler {
    private MatchResultsBaseAdapter adapter;
    private ArrayList<BookDataMatch> bookDataMatchArray = new ArrayList<>();
    private ListView resultsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_matching);

        UserData userData = ((MyAppUserData) this.getApplication()).getUserData();
        String getOwnedListURL = getString(R.string.server_url) + "MatchBooks.php";

        HttpPostAsyncTask getListTask = new HttpPostAsyncTask(BookMatching.this);
        getListTask.execute(getOwnedListURL, "user_id", userData.getUserID());

        resultsList = (ListView) findViewById(R.id.book_matching_list);

        EditText filterEditText = (EditText) findViewById(R.id.book_matching_filter);
        filterEditText.clearFocus();
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
    public void onPostExec(String json) {
        if (json.equals("")) {
            Toast.makeText(getApplicationContext(), "No matches...",
                    Toast.LENGTH_LONG).show();
            return;
        }

        BookData incomingBook, outgoingBook;
        BookDataMatch bookDataMatch;
        JSONObject jsonResponse;
        JSONArray jsonMainNodeTrade, jsonMainNodeSell, jsonMainNodeBuy;
        JSONObject jsonChildNodeTrade, jsonChildNodeSell, jsonChildNodeBuy;
        bookDataMatchArray = new ArrayList<>();

        try {
            jsonResponse = new JSONObject(json);
            jsonMainNodeTrade = jsonResponse.optJSONArray("trade_data");
            jsonMainNodeSell = jsonResponse.optJSONArray("sell_data");
            jsonMainNodeBuy = jsonResponse.optJSONArray("purchase_data");

            /* Try to output trade matches */
            try {
                for (int i = 0; i < jsonMainNodeTrade.length(); i++) {
                    jsonChildNodeTrade = jsonMainNodeTrade.getJSONObject(i);
                    incomingBook = new BookData(jsonChildNodeTrade.getString("incoming_author"),
                            jsonChildNodeTrade.getString("incoming_book_id"), "", "", "",
                            jsonChildNodeTrade.getString("incoming_title"),
                            jsonChildNodeTrade.getString("incoming_year"));

                    outgoingBook = new BookData(jsonChildNodeTrade.getString("outgoing_author"),
                            jsonChildNodeTrade.getString("outgoing_book_id"), "", "", "",
                            jsonChildNodeTrade.getString("outgoing_title"),
                            jsonChildNodeTrade.getString("outgoing_year"));

                    bookDataMatch = new BookDataMatch(jsonChildNodeTrade.getString("incoming_user_id"),
                            jsonChildNodeTrade.getString("incoming_username"), "", "trade", incomingBook,
                            outgoingBook);

                    bookDataMatchArray.add(bookDataMatch);
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "No trade data matches",
                        Toast.LENGTH_SHORT).show();
            }

            /* Try to output sell matches */
            try {
                for (int i = 0; i < jsonMainNodeSell.length(); i++) {
                    jsonChildNodeSell = jsonMainNodeSell.getJSONObject(i);
                    incomingBook = new BookData();
                    outgoingBook = new BookData(jsonChildNodeSell.getString("outgoing_author"),
                            jsonChildNodeSell.getString("outgoing_book_id"), "", "", "",
                            jsonChildNodeSell.getString("outgoing_title"),
                            jsonChildNodeSell.getString("outgoing_year"));

                    bookDataMatch = new BookDataMatch(jsonChildNodeSell.getString("incoming_user_id"),
                            jsonChildNodeSell.getString("incoming_username"),
                            jsonChildNodeSell.getString("incoming_price"), "sell", incomingBook,
                            outgoingBook);

                    bookDataMatchArray.add(bookDataMatch);
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "No sell data matches",
                        Toast.LENGTH_SHORT).show();
            }

            /* Try to output purchase matches */
            try {
                for (int i = 0; i < jsonMainNodeBuy.length(); i++) {
                    jsonChildNodeBuy = jsonMainNodeBuy.getJSONObject(i);
                    outgoingBook = new BookData();
                    incomingBook = new BookData(jsonChildNodeBuy.getString("incoming_author"),
                            jsonChildNodeBuy.getString("incoming_book_id"), "", "", "",
                            jsonChildNodeBuy.getString("incoming_title"),
                            jsonChildNodeBuy.getString("incoming_year"));

                    bookDataMatch = new BookDataMatch(jsonChildNodeBuy.getString("incoming_user_id"),
                            jsonChildNodeBuy.getString("incoming_username"),
                            jsonChildNodeBuy.getString("outgoing_price"), "buy", incomingBook,
                            outgoingBook);

                    bookDataMatchArray.add(bookDataMatch);
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "No buy data matches",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "No matches",
                    Toast.LENGTH_SHORT).show();
        }

        adapter = new MatchResultsBaseAdapter(BookMatching.this, R.layout.layout_match_results,
                bookDataMatchArray);
        resultsList.setAdapter(adapter);
        resultsList.setOnItemClickListener(new OnMatchResultsListItemClickListener(adapter));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_book_matching, menu);
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
