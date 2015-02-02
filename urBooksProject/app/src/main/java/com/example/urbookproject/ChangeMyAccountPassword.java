package com.example.urbookproject;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

public class ChangeMyAccountPassword extends ActionBarActivity {
    private String url;
    String response;
    private String jsonResult;
    List<NameValuePair> nameValuePairs;
    int ID;
    EditText oldPassword, newPassword, verifyPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_my_account_password);

        url = getString(R.string.server_url) + "ChangeUserPassword.php";
        ID = ((MyAppUserID) this.getApplication()).getUserID();

        oldPassword = (EditText) findViewById(R.id.OldPassword);
        newPassword = (EditText) findViewById(R.id.NewPassword);
        verifyPassword = (EditText) findViewById(R.id.NewPasswordVerify);

        Button submit = (Button) findViewById(R.id.SubmitNewPasswordButton);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldPassword.getText().toString().matches("") || newPassword.getText().toString().matches("")
                        || verifyPassword.getText().toString().matches("")){
                    Toast.makeText(getApplicationContext(),
                            "One or more fields are empty", Toast.LENGTH_LONG).show();
                }
                //else if(newPassword.getText().toString() != verifyPassword.getText().toString()){
                //this does not work!, needs something else to match edit text passwords
                else if(newPassword.getText().toString().equals(verifyPassword.getText().toString())){
                    Toast.makeText(getApplicationContext(),
                            "Verfiy new password failed", Toast.LENGTH_LONG).show();
                }
               else {
                    changePasswordInServer tasks = new changePasswordInServer();
                    tasks.execute();
                }
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_my_account_password, menu);
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

    private class changePasswordInServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... arg0) {

            nameValuePairs = new ArrayList<NameValuePair>(3);
            StringBuilder sb = new StringBuilder();
            sb.append(ID);
            nameValuePairs.add(new BasicNameValuePair("ID", sb.toString()));
            nameValuePairs.add(new BasicNameValuePair("OldPassword",oldPassword.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("NewPassword",newPassword.getText().toString()));


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
