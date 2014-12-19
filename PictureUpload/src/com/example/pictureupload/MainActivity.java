package com.example.pictureupload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.commons.net.ftp.*;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	private Button mTakePhoto;
	private ImageView mImageView;
	private static final String TAG = "upload";
	String uploadServer;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	private Bitmap imageToSend;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		uploadServer = "http://162.253.8.37/pictureUpload";
        
		mTakePhoto = (Button) findViewById(R.id.take_photo);
		mImageView = (ImageView) findViewById(R.id.imageview);

		mTakePhoto.setOnClickListener(this);
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
	
	void takePicture(){
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(takePictureIntent.resolveActivity(getPackageManager()) != null){
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			imageToSend = imageBitmap;
			//mImageView.setImageBitmap(imageBitmap);
			//SendBitmapTaskFTP sendToFTP = new SendBitmapTaskFTP();
			//sendToFTP.execute();
			SendBitmapTask sendImage = new SendBitmapTask();
			sendImage.execute();
		}
	}
	
	private class SendBitmapTaskFTP extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			imageToSend.compress(CompressFormat.PNG, 0, bos);
			byte[] bitmapdata = bos.toByteArray();
			ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
			
			FTPClient ftpClient = new FTPClient();
			
			try {
			ftpClient.connect("162.253.8.37", 21);
			ftpClient.login("urbooksupload", "uploadmyfile$1");
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			
			ftpClient.storeFile("/srv/ftp/urbooksupload", bs);

				//ftpClient.storeFile("", bs);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Toast.makeText(getApplicationContext(), "aldjkfldalds", Toast.LENGTH_SHORT).show();
			}
			
			return null;
		}
		
	}
	
	private class SendBitmapTask extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			imageToSend.compress(CompressFormat.PNG, 100, bos);
			byte[] bitmapdata = bos.toByteArray();
			ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			try {
				HttpPost httppost = new HttpPost(
						"http://162.253.8.37/pictureUpload.php"); // server

				MultipartEntity reqEntity = new MultipartEntity();
				reqEntity.addPart("myFile",
						System.currentTimeMillis() + ".jpg", bs);
				httppost.setEntity(reqEntity);

				Log.i(TAG, "request " + httppost.getRequestLine());
				HttpResponse response = null;
				try {
					response = httpclient.execute(httppost);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					Toast.makeText(MainActivity.this, "fuck", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				} catch (IOException e) {
					Toast.makeText(MainActivity.this, "fuck", Toast.LENGTH_LONG).show();
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					if (response != null)
						Log.i(TAG, "response " + response.getStatusLine().toString());
				} finally {

				}
			} finally {

			}
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
		}
		
	}
	
}
