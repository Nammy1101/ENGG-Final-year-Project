package com.example.urbookproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CaptureBarcode extends ActionBarActivity implements OnClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int MEDIA_TYPE_IMAGE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final String TAG = "upload";
    public static int count = 0;
    String uploadServer;
    Uri imageUri;
    ImageView iv;
    String imageFilePath;
    String mCurrentPhotoPath;
    File fileToSend;
    TextView phpResponse;
    ListView resultsList;
    ArrayList<String> titleArray = new ArrayList<>();
    ArrayList<String> authorArray = new ArrayList<>();
    ArrayList<String> yearArray = new ArrayList<>();
    ArrayList<String> bookID = new ArrayList<>();
    SearchResultsBaseAdapter adapter;
    private Button mTakePhoto;
    private Bitmap imageToSend;
    private UserData userData = new UserData();
    private ArrayList<BookData> bookDataArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_barcode);

        userData = ((MyAppUserID) this.getApplication()).getUserData();

        // uploadServer = "http://172.16.1.253/pictureUpload";
        uploadServer = getString(R.string.server_url_local);
        iv = (ImageView) findViewById(R.id.imageview);
        mTakePhoto = (Button) findViewById(R.id.button_camera);
        mTakePhoto.setOnClickListener(this);

        // phpResponse = (TextView) findViewById(R.id.phpResponse);
        resultsList = (ListView) findViewById(R.id.scan_results);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        takePicture();
    }

    void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri fullPhotoUri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                        fullPhotoUri);
                imageToSend = getResizedBitmap(bm, 1000);
                SendBitmapTask sendImage = new SendBitmapTask();
                sendImage.execute();
            } catch (IOException e) {
                // e.printStackTrace();
                Toast.makeText(getApplicationContext(), fullPhotoUri.getPath(), Toast.LENGTH_SHORT)
                        .show();
            }

            // Toast.makeText(getApplicationContext(), fullPhotoUri.getPath() ,
            // Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private class SendBitmapTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            imageToSend.compress(CompressFormat.PNG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();
            ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

            DefaultHttpClient httpclient = new DefaultHttpClient();
            String result = "Error: ISBN unresolved";
            try {
                // HttpPost httppost = new HttpPost("http://172.16.1.253/pictureUpload.php"); //
                // server
                HttpPost httppost = new HttpPost(uploadServer + "pictureUpload.php"); // server

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("myFile",
                        System.currentTimeMillis() + ".jpg", bs);
                httppost.setEntity(reqEntity);

                Log.i(TAG, "request " + httppost.getRequestLine());
                HttpResponse response = null;
                try {
                    response = httpclient.execute(httppost);
                    result = EntityUtils.toString(response.getEntity());
                } catch (IOException e) {
                    Toast.makeText(CaptureBarcode.this, "f", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                try {
                    if (response != null)
                        Log.i(TAG, "response " + response.getStatusLine().toString());
                } finally {

                }
            } finally {

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(CaptureBarcode.this, "Uploaded", Toast.LENGTH_LONG).show();

            BookData bookData = new BookData();

            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");
                result = jsonMainNode.toString();

                jsonMainNode = new JSONArray(result);
                for (int i = 0; i < jsonMainNode.length(); i++) {
                    try {
                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                        // responseTitle = jsonChildNode.optString("Book_Title").trim();
                        titleArray.add(jsonChildNode.getString("title"));
                        authorArray.add(jsonChildNode.getString("author"));
                        yearArray.add(jsonChildNode.getString("year"));
                        bookID.add(jsonChildNode.getString("book_id"));

                        bookData = new BookData(jsonChildNode.getString("author"),
                                jsonChildNode.getString("book_id"),
                                jsonChildNode.getString("has_cover"),
                                jsonChildNode.getString("isbn10"),
                                jsonChildNode.getString("isbn13"),
                                jsonChildNode.getString("title"),
                                jsonChildNode.getString("year"));

                        bookDataArray.add(bookData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //adapter = new SearchResultsBaseAdapter(CaptureBarcode.this, R.layout.layout_search_results, titleArray, authorArray, yearArray, bookID);
                adapter = new SearchResultsBaseAdapter(CaptureBarcode.this,
                        R.layout.layout_search_results, bookDataArray);
                resultsList.setAdapter(adapter);
                resultsList.setOnItemClickListener(new OnResultsListItemClickListener("SearchResults",
                        bookDataArray));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // phpResponse.setText(result);
            iv.setImageBitmap(imageToSend);
        }

    }
}
