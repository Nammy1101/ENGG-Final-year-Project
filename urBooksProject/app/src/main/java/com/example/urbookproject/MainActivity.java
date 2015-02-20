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

public class MainActivity extends ActionBarActivity implements IAsyncHttpHandler {
    private UserData userData = new UserData();
    private EditText username, password;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = getString(R.string.server_url) + "getUserData.php";
        username = (EditText) findViewById(R.id.main_username);
        password = (EditText) findViewById(R.id.main_password);

        Button loginButton = (Button) findViewById(R.id.main_button_login);
        Button signUpButton = (Button) findViewById(R.id.main_button_signup);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpPostAsyncTask task = new HttpPostAsyncTask(MainActivity.this);
                task.execute(url,
                        "username", username.getText().toString(),
                        "password", password.getText().toString());
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Signup.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPostExec(String json) {
        String phpResponse = "No response...";

        try {
            JSONObject jsonResponse = new JSONObject(json);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                phpResponse = jsonChildNode.optString("response").trim();
                userData.setUserID(jsonChildNode.optString("user_id"));
                userData.setUserName(jsonChildNode.optString("username"));
                userData.setPassword(jsonChildNode.optString("password"));
                userData.setEmail(jsonChildNode.optString("email"));
                userData.setFirstName(jsonChildNode.optString("first_name"));
                userData.setLastName(jsonChildNode.optString("last_name"));
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        if (phpResponse.contains("true")) {
            ((MyAppUserData) this.getApplication()).setUserData(userData);
            Toast.makeText(getApplicationContext(), "Login success!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, HomeScreen.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), phpResponse, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
