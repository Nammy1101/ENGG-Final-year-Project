package com.example.urbookproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MyAccount extends ActionBarActivity implements IAsyncHttpHandler {

    TextView UserName, FirstName, LastName, Email;
    private String url;
    UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        userData = ((MyAppUserData) this.getApplication()).getUserData();

        url = getString(R.string.server_url) + "DeleteAccount.php";

        Button changePassword = (Button) findViewById(R.id.ChangePasswordButton);
        Button changeInformation = (Button) findViewById(R.id.ChangeInformationButton);
        Button accountDelete = (Button) findViewById(R.id.AccountDeleteButton);

        UserName = (TextView) findViewById(R.id.accountUserName);
        FirstName = (TextView) findViewById(R.id.accountFirstName);
        LastName = (TextView) findViewById(R.id.accountLastName);
        Email = (TextView) findViewById(R.id.accountEmail);

        UserName.setText(userData.getUserName());
        FirstName.setText(userData.getFirstName());
        LastName.setText(userData.getLastName());
        Email.setText(userData.getEmail());

        changeInformation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccount.this, ChangeMyAccount.class);
                startActivity(intent);
            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccount.this, ChangeMyAccountPassword.class);
                startActivity(intent);
            }

        });

        accountDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HttpPostAsyncTask task = new HttpPostAsyncTask(MyAccount.this);
                task.execute(url,"user_id", userData.getUserID());
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_account, menu);
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

    @Override
    public void onPostExec(String json) {
        String phpResponse = "No response...";

        try {
            JSONObject jsonResponse = new JSONObject(json);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                phpResponse = jsonChildNode.optString("response").trim();
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "JSON Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }


            Toast.makeText(getApplicationContext(), phpResponse, Toast.LENGTH_SHORT).show();
          //  finish();

        Intent intent = new Intent(MyAccount.this, MainActivity.class);
        startActivity(intent);

    }

}
