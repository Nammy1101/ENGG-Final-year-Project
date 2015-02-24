package com.example.urbookproject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MatchedUserInformation extends ActionBarActivity implements IAsyncHttpHandler {
    String matchedUserID;
    TextView matchedUserName, matchedFirstName, matchedLastName, matchedEmail;
    String url;
    BookData bookData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_user_information);

        url = getString(R.string.server_url) + "MatchedUserInformation.php";
        matchedUserID = getIntent().getStringExtra("matchedUserID");

        matchedUserName = (TextView) findViewById(R.id.matched_username);
        matchedFirstName = (TextView) findViewById(R.id.matched_first_name);
        matchedLastName = (TextView) findViewById(R.id.matched_last_name);
        matchedEmail = (TextView) findViewById(R.id.matched_email);

        HttpPostAsyncTask task = new HttpPostAsyncTask(MatchedUserInformation.this);
        task.execute(url,
                "matched_user_id", matchedUserID);
        /*
        HttpPostAsyncTask task = new HttpPostAsyncTask(MatchedUserInformation.this);
        task.execute(url,
                "matched_user_id", matchedUserID);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_matched_user_information, menu);
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

    @Override
    public void onPostExec(String json) {
        try {
            JSONObject jsonResponse = new JSONObject(json);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                matchedUserName.setText(jsonChildNode.optString("matched_username").trim());
                matchedFirstName.setText(jsonChildNode.optString("matched_first_name").trim());
                matchedLastName.setText(jsonChildNode.optString("matched_last_name").trim());
                matchedEmail.setText(jsonChildNode.optString("matched_email").trim());
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getApplicationContext(), matchedUserName.getText().toString().trim() + "'s page.",
                Toast.LENGTH_SHORT).show();
    }
}
