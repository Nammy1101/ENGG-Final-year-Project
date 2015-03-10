package com.example.urbookproject;

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

public class InsertBook extends ActionBarActivity implements IAsyncHttpHandler {
    private UserData userData = new UserData();
    private BookData bookData = new BookData();
    private LinearLayout ownedOptions, wantedOptions;
    private String url, selectedOption;
    private String trade, sellPrice, purchasePrice, keep;
    private boolean wantedChecked, ownedChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_book);

        bookData = new BookData();
        bookData = getIntent().getParcelableExtra("bookData");
        userData = ((MyAppUserData) this.getApplication()).getUserData();

        ownedOptions = (LinearLayout) findViewById(R.id.linear_owned_options);
        wantedOptions = (LinearLayout) findViewById(R.id.linear_wanted_options);
        ownedOptions.setVisibility(View.GONE);
        wantedOptions.setVisibility(View.GONE);

        url = getString(R.string.server_url) + "InsertBook.php";

        selectedOption = null;

        TextView bookTitle = (TextView) findViewById(R.id.insert_book_title);
        TextView bookAuthor = (TextView) findViewById(R.id.insert_book_author);
        TextView bookYear = (TextView) findViewById(R.id.insert_book_year);
        ImageView bookCover = (ImageView) findViewById(R.id.insert_book_cover);

        bookTitle.setText(bookData.getTitle());
        bookAuthor.setText(bookData.getAuthor());
        bookYear.setText(bookData.getYear());
        new DownloadImageTask(bookCover).execute(getString(R.string.server_url) + "covers/"
                + bookData.getBookID() + ".jpg");

        Button select = (Button) findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    if (wantedChecked) {
                        HttpPostAsyncTask task = new HttpPostAsyncTask(InsertBook.this);
                        task.execute(url,
                                "book_id", bookData.getBookID(),
                                "user_id", userData.getUserID(),
                                "trade", trade,
                                "purchase", purchasePrice,
                                "option_selected", selectedOption);
                    }

                    if (ownedChecked) {
                        HttpPostAsyncTask task = new HttpPostAsyncTask(InsertBook.this);
                        task.execute(url,
                                "book_id", bookData.getBookID(),
                                "user_id", userData.getUserID(),
                                "keep", keep,
                                "trade", trade,
                                "sell", sellPrice,
                                "option_selected", selectedOption);
                    }
                }
            }
        });
    }

    private boolean validateInput() {
        if (wantedChecked == true) {
            CheckBox tradeCheckbox = (CheckBox) findViewById(R.id.checkbox_wanted_trade);
            CheckBox purchaseCheckbox = (CheckBox) findViewById(R.id.checkbox_wanted_purchase);
            EditText purchaseText = (EditText) findViewById(R.id.edit_text_purchase);
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
            } else if (purchaseCheckbox.isChecked() &&
                    (purchaseString.matches("") || validPrice < 0)) {
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
                purchasePrice = purchaseString;
            } else {
                purchasePrice = "NULL";
            }

            return true;
        } else if (ownedChecked == true) {
            boolean keepRadio = ((RadioButton) findViewById(R.id.radio_owned_keep)).isChecked();
            boolean exchange = ((RadioButton) findViewById(R.id.radio_owned_exchange)).isChecked();

            if (keepRadio) {
                keep = "1";
                trade = "0";
                sellPrice = "NULL";
                return true;
            }

            if (exchange) {
                CheckBox tradeCheckbox = (CheckBox) findViewById(R.id.checkbox_owned_trade);
                CheckBox sellCheckbox = (CheckBox) findViewById(R.id.checkbox_owned_sell);
                EditText sellText = (EditText) findViewById(R.id.edit_text_sell);
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
                    Toast.makeText(getApplicationContext(),
                            "At least one checkbox must be selected.", Toast.LENGTH_SHORT).show();
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
        }

        return false;
    }

    public void onOptionSelectedClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.wantedBook:
                if (checked) {
                    selectedOption = "want";
                    wantedChecked = true;
                    ownedChecked = false;
                    ownedOptions.setVisibility(View.GONE);
                    wantedOptions.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ownedBook:
                if (checked) {
                    selectedOption = "owned";
                    ownedChecked = true;
                    wantedChecked = false;
                    wantedOptions.setVisibility(View.GONE);
                    ownedOptions.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public void onOptionKeepExchangeClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        LinearLayout exchangeOptions = (LinearLayout) findViewById(R.id.linear_owned_exchange);

        switch (view.getId()) {
            case R.id.radio_owned_keep:
                if (checked) {
                    exchangeOptions.setVisibility(View.GONE);
                }
                break;
            case R.id.radio_owned_exchange:
                if (checked) {
                    exchangeOptions.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public void onCheckboxClick(View view) {
        boolean isChecked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.checkbox_wanted_purchase:
                EditText purchaseText = (EditText) findViewById(R.id.edit_text_purchase);

                if (isChecked) {
                    purchaseText.setEnabled(true);
                    purchaseText.requestFocus();
                } else {
                    purchaseText.setEnabled(false);
                }
                break;
            case R.id.checkbox_owned_sell:
                EditText sellText = (EditText) findViewById(R.id.edit_text_sell);

                if (isChecked) {
                    sellText.setEnabled(true);
                    sellText.requestFocus();
                } else {
                    sellText.setEnabled(false);
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
        finish();
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
