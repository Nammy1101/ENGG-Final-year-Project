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

public class MyAccount extends ActionBarActivity {

    TextView UserName, FirstName, LastName, Email;
    String response;
    List<NameValuePair> nameValuePairs;
    int ID;
    private String url;
    private String jsonResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        url = getString(R.string.server_url) + "GetUserInformation.php";

        Button changePassword = (Button) findViewById(R.id.ChangePasswordButton);
        Button changeInformation = (Button) findViewById(R.id.ChangeInformationButton);

        UserName = (TextView) findViewById(R.id.accountUserName);
        FirstName = (TextView) findViewById(R.id.accountFirstName);
        LastName = (TextView) findViewById(R.id.accountLastName);
        Email = (TextView) findViewById(R.id.accountEmail);

        ID = ((MyAppUserID) this.getApplication()).getUserID();

        getInfoFromServer task = new getInfoFromServer();
        task.execute();

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

    void fillInTextFields() {
        String test = null;
        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                UserName.setText(jsonChildNode.optString("username").trim());
                FirstName.setText(jsonChildNode.optString("firstName").trim());
                LastName.setText(jsonChildNode.optString("lastName").trim());
                Email.setText(jsonChildNode.optString("email").trim());
                test = jsonChildNode.optString("username").trim();

            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private class getInfoFromServer extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {
            nameValuePairs = new ArrayList<NameValuePair>(1);
            StringBuilder sb = new StringBuilder();
            sb.append(ID);
            nameValuePairs.add(new BasicNameValuePair("ID", sb.toString()));

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO Auto-generated method stub
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                // e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {

            fillInTextFields();

        }
    }

}
