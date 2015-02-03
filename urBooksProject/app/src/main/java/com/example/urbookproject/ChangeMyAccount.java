package com.example.urbookproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ChangeMyAccount extends ActionBarActivity {

    EditText Email, FirstName, LastName;
    boolean changeFirstName, changeLastName, changeEmail, changeAll;
    String response, selection;
    List<NameValuePair> nameValuePairs;
    int ID;
    private String url;
    private String jsonResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_my_account);

        url = getString(R.string.server_url) + "ChangeUserInformation.php";

        changeFirstName = false;
        changeLastName = false;
        changeEmail = false;
        changeAll = false;

        ID = ((MyAppUserID) this.getApplication()).getUserID();

        Email = (EditText) findViewById(R.id.changeEmail);
        FirstName = (EditText) findViewById(R.id.changeFirstName);
        LastName = (EditText) findViewById(R.id.changeLastName);

        Button submitChangeFirstName = (Button) findViewById(R.id.submitChangeFirstName);
        Button submitChangeLastName = (Button) findViewById(R.id.submitChangeLastName);
        Button submitChangeEmail = (Button) findViewById(R.id.submitChangeEmail);
        Button submitChangeAll = (Button) findViewById(R.id.submitAllChange);

        submitChangeFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirstName.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "First Name is blank",
                            Toast.LENGTH_SHORT).show();
                } else {
                    changeFirstName = true;
                    changeLastName = false;
                    changeEmail = false;
                    changeAll = false;

                    changeInformation task = new changeInformation();
                    task.execute();
                }
            }
        });
        submitChangeLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LastName.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Last Name is blank",
                            Toast.LENGTH_SHORT).show();
                } else {
                    changeFirstName = false;
                    changeLastName = true;
                    changeEmail = false;
                    changeAll = false;

                    changeInformation task = new changeInformation();
                    task.execute();
                }
            }
        });
        submitChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Email.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Email is blank",
                            Toast.LENGTH_SHORT).show();
                } else {

                    changeFirstName = false;
                    changeLastName = false;
                    changeEmail = true;
                    changeAll = false;

                    changeInformation task = new changeInformation();
                    task.execute();
                }
            }
        });

        submitChangeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FirstName.getText().toString().matches("") ||
                        LastName.getText().toString().matches("") || Email.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "One or more fields are blank",
                            Toast.LENGTH_SHORT).show();
                } else {


                    changeFirstName = false;
                    changeLastName = false;
                    changeEmail = false;
                    changeAll = true;

                    changeInformation task = new changeInformation();
                    task.execute();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.change_my_account, menu);
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

    private class changeInformation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            selection = "";
            if (changeAll) {
                nameValuePairs = new ArrayList<NameValuePair>(5);
                selection = "changeall";
                nameValuePairs.add(new BasicNameValuePair("FirstName", FirstName.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("LastName", LastName.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("Email", Email.getText().toString()));
            } else if (changeFirstName) {
                nameValuePairs = new ArrayList<NameValuePair>(3);
                selection = "changefirstname";
                nameValuePairs.add(new BasicNameValuePair("Value", FirstName.getText().toString()));
            } else if (changeLastName) {
                nameValuePairs = new ArrayList<NameValuePair>(3);
                selection = "changelastname";
                nameValuePairs.add(new BasicNameValuePair("Value", LastName.getText().toString()));
            } else if (changeEmail) {
                nameValuePairs = new ArrayList<NameValuePair>(3);
                selection = "changeemail";
                nameValuePairs.add(new BasicNameValuePair("Value", Email.getText().toString()));
            }

            StringBuilder sb = new StringBuilder();
            sb.append(ID);
            nameValuePairs.add(new BasicNameValuePair("ID", sb.toString()));
            nameValuePairs.add(new BasicNameValuePair("selection", selection));

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
            String response = null;
            try {
                JSONObject jsonResponse = new JSONObject(jsonResult);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    response = jsonChildNode.optString("result").trim();

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(getApplicationContext(), response,
                    Toast.LENGTH_SHORT).show();

        }
    }

}
