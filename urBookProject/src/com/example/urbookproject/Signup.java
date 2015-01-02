
package com.example.urbookproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Signup extends Activity {
    private String jsonResult;
    EditText username, password, email, firstName, lastName;
    //private String url = "http://172.16.1.253/signup.php";
    // private String url = getString(R.string.server_url) + "signup.php";
    // private String url = getString(R.string.server_url_local) + "signup.php";
    private String url;
    String count;
    List<NameValuePair> nameValuePairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        url = getString(R.string.server_url) + "signup.php";
        // url = getString(R.string.server_url_local) + "signup.php";

        username = (EditText) findViewById(R.id.UsernameSignup);
        password = (EditText) findViewById(R.id.PasswordSignup);
        email = (EditText) findViewById(R.id.EmailSignup);
        firstName = (EditText) findViewById(R.id.FirstNameSignup);
        lastName = (EditText) findViewById(R.id.LastNameSignup);

        Button signUp = (Button) findViewById(R.id.SignUpButton);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                accessWebService();
            }
        });
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

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[] {
                url
        });
    }

    private class JsonReadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            nameValuePairs = new ArrayList<NameValuePair>(5);

            nameValuePairs.add(new BasicNameValuePair("username", username.getText().toString()
                    .trim())); // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()
                    .trim()));
            nameValuePairs.add(new BasicNameValuePair("email", email.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("lastname", lastName.getText().toString()
                    .trim()));
            nameValuePairs.add(new BasicNameValuePair("firstname", firstName.getText().toString()
                    .trim()));

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
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
            goToMain();
        }
    }

    public void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
