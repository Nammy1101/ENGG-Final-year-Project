package com.example.urbookproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;


public class WantedListItemChange extends ActionBarActivity implements IAsyncHttpHandler {
    private BookDataWanted bookDataWanted = new BookDataWanted();
    private String url;
    private String purchasePrice, trade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wanted_list_item_change);

        bookDataWanted = new BookDataWanted();
        bookDataWanted = getIntent().getParcelableExtra("bookDataWanted");

        url = getString(R.string.server_url) + "ChangeWantedBook.php";

        TextView bookTitle = (TextView) findViewById(R.id.change_wanted_book_title);
        TextView bookAuthor = (TextView) findViewById(R.id.change_wanted_book_author);
        TextView bookYear = (TextView) findViewById(R.id.change_wanted_book_year);
        ImageView bookCover = (ImageView) findViewById(R.id.change_wanted_book_cover);

        TextView tradeText = (TextView) findViewById(R.id.change_wanted_trade_text);
        TextView purchaseText = (TextView) findViewById(R.id.change_wanted_buy_text);

        bookTitle.setText(bookDataWanted.getTitle());
        bookAuthor.setText(bookDataWanted.getAuthor());
        bookYear.setText(bookDataWanted.getYear());

        if (bookDataWanted.getTrade().equals("1")) {
            tradeText.setText("Trade: Yes");
        } else {
            tradeText.setText("Trade: No");
        }

        if (bookDataWanted.getPurchase().equals("null") ||
                bookDataWanted.getPurchase().equals("0.00")) {
            purchaseText.setText("Buy:   No");
        } else {
            purchaseText.setText("Buy:   $" + bookDataWanted.getPurchase());
        }

        new DownloadImageTask(bookCover).execute(getString(R.string.server_url) + "covers/"
                + bookDataWanted.getBookID() + ".jpg");

        Button confirm = (Button) findViewById(R.id.change_wanted_select);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    HttpPostAsyncTask task = new HttpPostAsyncTask(WantedListItemChange.this);
                    task.execute(url,
                            "wanted_id", bookDataWanted.getWantedID(),
                            "trade", trade,
                            "purchase", purchasePrice);
                }
            }
        });
    }

    private boolean validateInput() {
        CheckBox tradeCheckbox = (CheckBox) findViewById(R.id.checkbox_change_wanted_trade);
        CheckBox purchaseCheckbox = (CheckBox) findViewById(R.id.checkbox_change_wanted_purchase);
        EditText purchaseText = (EditText) findViewById(R.id.edit_change_text_purchase);
        String purchaseString = purchaseText.getText().toString();
        float fPrice;

        try {
            fPrice = Float.parseFloat(purchaseString);
        } catch (NumberFormatException e) {
            fPrice = 0.00f;
        }

        int validPrice = Float.compare(fPrice, 0.01f);

        if (!tradeCheckbox.isChecked() && !purchaseCheckbox.isChecked()) {
            Toast.makeText(getApplicationContext(), "At least one checkbox must be selected.",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (purchaseCheckbox.isChecked() && (purchaseString.matches("") || validPrice < 0)) {
            Toast.makeText(getApplicationContext(), "You must enter a valid purchase amount.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (tradeCheckbox.isChecked()) {
            trade = "1";
        } else {
            trade = "0";
        }

        if (purchaseText.isEnabled() && purchaseCheckbox.isChecked()) {
            purchasePrice = purchaseText.getText().toString();
        } else {
            purchasePrice = "NULL";
        }

        return true;
    }

    public void onCheckboxClick(View view) {
        boolean isChecked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.checkbox_change_wanted_purchase:
                EditText purchaseText = (EditText) findViewById(R.id.edit_change_text_purchase);

                if (isChecked) {
                    purchaseText.setEnabled(true);
                    purchaseText.requestFocus();
                } else {
                    purchaseText.setEnabled(false);
                }
                break;
        }
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
        Intent intent = new Intent(WantedListItemChange.this, WantedList.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WantedListItemChange.this, WantedList.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wanted_list_item_change, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
