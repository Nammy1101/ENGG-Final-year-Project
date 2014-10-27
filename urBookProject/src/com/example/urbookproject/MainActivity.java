package com.example.urbookproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
   // private String url = "http://204.83.105.45/test.php";
	private String jsonResult;
	EditText username, password;
    private String url = "http://204.83.105.45/test.php";
	String count;
	List<NameValuePair> nameValuePairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
    	username = (EditText) findViewById(R.id.UsernameSignup);
    	password = (EditText) findViewById(R.id.Password);
        
        Button SignIn = (Button) findViewById(R.id.login);
        
        SignIn.setOnClickListener(new View.OnClickListener() {
			
        		

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
		              // dialog = ProgressDialog.show(MainActivity.this, "", 
		                 //       "Validating user...", true);
						accessWebService();
				}
    	});
    }
    
    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[] { url });
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
    
    public void goToSignUp(View view){
    	Intent intent = new Intent(this, Signup.class);
    	startActivity(intent);
    }

    private class JsonReadTask extends AsyncTask<String, Void, String> {

    	 @Override
         protected String doInBackground(String... params) {
     			nameValuePairs = new ArrayList<NameValuePair>(2);
    		
     			nameValuePairs.add(new BasicNameValuePair("username",username.getText().toString().trim()));  // $Edittext_value = $_POST['Edittext_value'];
     			nameValuePairs.add(new BasicNameValuePair("password",password.getText().toString().trim())); 
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
                 ReadHttpResponse();
         }
    }
    
    public void ReadHttpResponse() {

        try {
                JSONObject jsonResponse = new JSONObject(jsonResult);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                        count = jsonChildNode.optString("response").trim();
                }
        } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                                Toast.LENGTH_SHORT).show();
        }
        
    	Toast.makeText(getApplicationContext(), count,
                Toast.LENGTH_SHORT).show();
        
    }
      
}
