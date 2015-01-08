package com.example.urbookproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class InsertBook extends ActionBarActivity {
	
	String title, author, year;
	int ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert_book);
		Intent intent = getIntent();
		title = intent.getStringExtra("BOOK_TITLE");
		author = intent.getStringExtra("BOOK_AUTHOR");
		year = intent.getStringExtra("BOOK_YEAR");
		ID = ((MyAppUserID) this.getApplication()).getUserID();
		
		TextView bookTitle = (TextView) findViewById(R.id.insert_book_title);
        TextView bookAuthor = (TextView) findViewById(R.id.insert_book_author);
        TextView bookYear = (TextView) findViewById(R.id.insert_book_year);
        
        bookTitle.setText(title);
        bookAuthor.setText(author);
        bookYear.setText(year);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.insert_book, menu);
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
