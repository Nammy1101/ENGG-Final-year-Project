package com.example.urbookproject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MyAccount extends ActionBarActivity {

	TextView UserName, FirstName, LastName, Email;
    private String url;
    String response;
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
		
		getUserInformation(((MyAppUserID) this.getApplication()).getUserID());
		
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
	
	public void getUserInformation(int ID){
		
	}
}
