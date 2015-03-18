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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;


public class OwnedListItemChange extends ActionBarActivity implements IAsyncHttpHandler {
    private BookDataOwned bookDataOwned = new BookDataOwned();
    private String url;
    private String keep, sellPrice, trade;
    private MyAppUserData cacheData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owned_list_item_change);

        bookDataOwned = new BookDataOwned();
        bookDataOwned = getIntent().getParcelableExtra("bookDataOwned");
        cacheData = ((MyAppUserData) this.getApplication());

        url = getString(R.string.server_url) + "ChangeOwnedBook.php";

        TextView bookTitle = (TextView) findViewById(R.id.change_owned_book_title);
        TextView bookAuthor = (TextView) findViewById(R.id.change_owned_book_author);
        TextView bookYear = (TextView) findViewById(R.id.change_owned_book_year);
        ImageView bookCover = (ImageView) findViewById(R.id.change_owned_book_cover);

        TextView keepText = (TextView) findViewById(R.id.change_owned_keep_text);
        TextView tradeText = (TextView) findViewById(R.id.change_owned_trade_text);
        TextView sellText = (TextView) findViewById(R.id.change_owned_sell_text);

        bookTitle.setText(bookDataOwned.getTitle());
        bookAuthor.setText(bookDataOwned.getAuthor());
        bookYear.setText(bookDataOwned.getYear());

        if (bookDataOwned.getKeep().equals("1")) {
            keepText.setText("Keep:  Yes");
        } else {
            keepText.setText("Keep:  No");
        }

        if (bookDataOwned.getTrade().equals("1")) {
            tradeText.setText("Trade: Yes");
        } else {
            tradeText.setText("Trade: No");
        }

        if (bookDataOwned.getSell().equals("null") || bookDataOwned.getSell().equals("0.00")) {
            sellText.setText("Sell:  No");
        } else {
            sellText.setText("Sell:  $" + bookDataOwned.getSell());
        }

        String bookImageName = bookDataOwned.getBookID() + ".jpg";
        String bookImageURL = getString(R.string.server_url) + "covers/" + bookImageName;
        new DownloadImageTask(bookCover, bookImageName).execute(bookImageURL);

        Button confirm = (Button) findViewById(R.id.change_owned_select);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    HttpPostAsyncTask task = new HttpPostAsyncTask(OwnedListItemChange.this);
                    task.execute(url,
                            "owned_id", bookDataOwned.getOwnedID(),
                            "keep", keep,
                            "trade", trade,
                            "sell", sellPrice);
                }
            }
        });
    }

    public void onCheckboxClick(View view) {
        boolean isChecked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.checkbox_change_owned_sell:
                EditText sellText = (EditText) findViewById(R.id.edit_change_text_sell);

                if (isChecked) {
                    sellText.setEnabled(true);
                    sellText.requestFocus();
                } else {
                    sellText.setEnabled(false);
                }
                break;
        }
    }

    public void onOptionKeepExchangeClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        LinearLayout options = (LinearLayout) findViewById(R.id.linear_change_owned_exchange);

        switch (view.getId()) {
            case R.id.radio_change_owned_keep:
                if (checked) {
                    options.setVisibility(View.GONE);
                }
                break;
            case R.id.radio_change_owned_exchange:
                if (checked) {
                    options.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private boolean validateInput() {
        boolean keepRadio = ((RadioButton) findViewById(R.id.radio_change_owned_keep)).isChecked();
        boolean exchange = ((RadioButton) findViewById(R.id.radio_change_owned_exchange)).isChecked();

        if (keepRadio) {
            keep = "1";
            trade = "0";
            sellPrice = "NULL";
            return true;
        }

        if (exchange) {
            CheckBox tradeCheckbox = (CheckBox) findViewById(R.id.checkbox_change_owned_trade);
            CheckBox sellCheckbox = (CheckBox) findViewById(R.id.checkbox_change_owned_sell);
            EditText sellText = (EditText) findViewById(R.id.edit_change_text_sell);
            String sellString = sellText.getText().toString();
            float fPrice;
            keep = "0";

            try {
                fPrice = Float.parseFloat(sellString);
            } catch (NumberFormatException e) {
                fPrice = 0.00f;
            }

            int validPrice = Float.compare(fPrice, 0.01f);


            if (!tradeCheckbox.isChecked() && !sellCheckbox.isChecked()) {
                Toast.makeText(getApplicationContext(), "At least one checkbox must be selected.",
                        Toast.LENGTH_SHORT).show();
                return false;
            } else if (sellCheckbox.isChecked() && (sellString.matches("") || validPrice < 0)) {
                Toast.makeText(getApplicationContext(), "You must enter a valid selling price.",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

            if (tradeCheckbox.isChecked()) {
                trade = "1";
            } else {
                trade = "0";
            }

            if (sellText.isEnabled() && sellCheckbox.isChecked()) {
                sellPrice = sellString;
            } else {
                sellPrice = "NULL";
            }

            return true;
        }

        return false;
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
        Intent intent = new Intent(OwnedListItemChange.this, OwnedList.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OwnedListItemChange.this, OwnedList.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_owned_list_item_change, menu);
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
        String name;

        public DownloadImageTask(ImageView bmImage, String name) {
            this.bmImage = bmImage;
            this.name = name;
        }

        protected Bitmap doInBackground(String... urls) {
            if (cacheData.cache.containsKey(name)) {
                return null;
            }

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
            if (cacheData.cache.getImageSizeLarge() == 0) {
                cacheData.cache.setImageSizeLarge(340);
            }

            if (result == null) {
                bmImage.setImageBitmap(cacheData.cache.get(name));
                Log.d("CACHE", "Loaded: [" + name + "] from memory cache.");
                ImageCache.scaleImage(bmImage, cacheData.cache.getImageSizeLarge());
            } else {
                cacheData.cache.put(name, result);

                bmImage.setImageBitmap(cacheData.cache.get(name));

                ImageCache.scaleImage(bmImage, cacheData.cache.getImageSizeLarge());

                Log.d("CACHE", "Fetched: [" + name + "] from URL.");
            }
        }
    }
}
