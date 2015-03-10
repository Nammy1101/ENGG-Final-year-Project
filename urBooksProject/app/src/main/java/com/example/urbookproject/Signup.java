package com.example.urbookproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Signup extends Activity implements IAsyncHttpHandler {
    private EditText username, password, email, firstName, lastName;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        url = getString(R.string.server_url) + "Signup.php";

        username = (EditText) findViewById(R.id.signup_username);
        password = (EditText) findViewById(R.id.signup_password);
        email = (EditText) findViewById(R.id.signup_email);
        firstName = (EditText) findViewById(R.id.signup_firstname);
        lastName = (EditText) findViewById(R.id.signup_lastname);

        Button signUp = (Button) findViewById(R.id.signup_button);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //accessWebService();
                HttpPostAsyncTask task = new HttpPostAsyncTask(Signup.this);
                task.execute(url,
                        "username", username.getText().toString(),
                        "password", password.getText().toString(),
                        "email", email.getText().toString(),
                        "first_name", firstName.getText().toString(),
                        "last_name", lastName.getText().toString());
            }
        });
    }

    @Override
    public void onPostExec(String json) {
        if (json.equals("1")) {
            Toast.makeText(getApplicationContext(), "Account created!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Unable to create account.", Toast.LENGTH_SHORT).show();
        }
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
