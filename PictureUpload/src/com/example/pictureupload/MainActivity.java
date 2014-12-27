package com.example.pictureupload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
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
	private static final String TAG = "upload";
	String uploadServer;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int MEDIA_TYPE_IMAGE = 1;
	static final int REQUEST_TAKE_PHOTO = 1;
	private Bitmap imageToSend;
	Uri imageUri;
	ImageView iv;
	String imageFilePath;
	String mCurrentPhotoPath;
	File fileToSend;
	public static int count = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		uploadServer = "http://162.253.8.37/pictureUpload";
        iv = (ImageView) findViewById(R.id.imageview);
		mTakePhoto = (Button) findViewById(R.id.take_photo);
		mTakePhoto.setOnClickListener(this);
	}
	
	public void onSavedInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		
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
		
		/*
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/"; 
        File newdir = new File(dir); 
        newdir.mkdirs();
        
        Random rng = new Random();
        
        count++;
        String file = dir+rng+".jpg";
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {}       

        Uri outputFileUri = Uri.fromFile(newfile);
        
        mCurrentPhotoPath = file;
        imageFilePath=mCurrentPhotoPath;
        
		imageUri = Uri.fromFile(fileToSend);
		
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
	    
        //Toast.makeText(getApplicationContext(), mCurrentPhotoPath , Toast.LENGTH_SHORT).show();*/
		
	}
	
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
			/*
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			imageToSend = imageBitmap;
			//mImageView.setImageBitmap(imageBitmap);
			//SendBitmapTaskFTP sendToFTP = new SendBitmapTaskFTP();
			//sendToFTP.execute();
			SendBitmapTask sendImage = new SendBitmapTask();
			sendImage.execute();
			*/
			/*if(data == null){
				File file = new File(imageUri.getPath());
				fileToSend = file;
			
				SendBitmapTask sendImage = new SendBitmapTask();
				//sendImage.execute();
			
				Log.d("CameraDemo", "Pic saved");
			}*/
			
			Uri fullPhotoUri = data.getData();
			
			try {
				Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fullPhotoUri);
				imageToSend = getResizedBitmap(bm, 600);
				SendBitmapTask sendImage = new SendBitmapTask();
				sendImage.execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Toast.makeText(getApplicationContext(), fullPhotoUri.getPath() , Toast.LENGTH_SHORT).show();
			}
			
			
			
			//Toast.makeText(getApplicationContext(), fullPhotoUri.getPath() , Toast.LENGTH_SHORT).show();
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
	
	public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
}
	   
}
