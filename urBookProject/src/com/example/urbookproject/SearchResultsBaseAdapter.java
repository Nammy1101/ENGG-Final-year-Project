
package com.example.urbookproject;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchResultsBaseAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList title, author, year, id;
    private String imageurl;
    private static LayoutInflater inflater = null;
    private int resource;
    int ID;
    private Context context;

    public SearchResultsBaseAdapter(Activity activity, int resource, ArrayList title, ArrayList author,
            ArrayList year, ArrayList id, int userID) {
        this.activity = activity;
        this.title = title;
        this.author = author;
        this.year = year;
        this.id = id;
        this.resource = resource;
        this.ID = userID;
        this.context = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return title.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            //view = inflater.inflate(R.layout.layout_search_results, null);
            view = inflater.inflate(resource, null);
        }

        TextView bookTitle = (TextView) view.findViewById(R.id.book_title);
        TextView bookAuthor = (TextView) view.findViewById(R.id.book_author);
        TextView bookYear = (TextView) view.findViewById(R.id.book_year);
        TextView bookID = (TextView) view.findViewById(R.id.book_id);
        ImageView bookCover = (ImageView) view.findViewById(R.id.book_cover);

        bookTitle.setText(title.get(position).toString());
        bookAuthor.setText(author.get(position).toString());
        bookYear.setText(year.get(position).toString());
        bookID.setText(id.get(position).toString());

        imageurl = activity.getResources().getString(R.string.server_url) + "covers/"
                + id.get(position).toString() + ".jpg";

        new DownloadImageTask(bookCover).execute(imageurl);

        /*
         * view.setOnClickListener(new OnClickListener(){
         * @Override public void onClick(View v) { // TODO Auto-generated method stub Intent intent
         * = new Intent(context, InsertBook.class); intent.putExtra("BOOK_TITLE" ,
         * title.toString()); intent.putExtra("BOOK_AUTHOR" , author.toString());
         * intent.putExtra("BOOK_YEAR" , author.toString()); intent.putExtra("USER_ID", ID);
         * context.startActivity(intent); } });
         */
        return view;
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return position;
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
