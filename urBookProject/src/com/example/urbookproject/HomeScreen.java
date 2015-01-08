
package com.example.urbookproject;

import android.support.v7.app.ActionBarActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends ActionBarActivity {

    int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Intent intent = getIntent();
       // ID = intent.getIntExtra("USER_ID", 0);
        ((MyAppUserID) this.getApplication()).setUserID(intent.getIntExtra("USER_ID", 0));

        Button autoButton = (Button) findViewById(R.id.button_auto_search);
        Button searchButton = (Button) findViewById(R.id.button_search);
        Button booksWantedButton = (Button) findViewById(R.id.button_wanted);
        Button booksOwnedButton = (Button) findViewById(R.id.button_owned);
        Button accountButton = (Button) findViewById(R.id.button_account);

        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, CaptureBarcode.class);
                intent.putExtra("USER_ID", ID);
                startActivity(intent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(HomeScreen.this, ManualSearch.class);
                intent.putExtra("USER_ID", ID);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);
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
