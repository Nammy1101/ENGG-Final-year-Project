package com.example.urbookproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class SearchResultsBaseAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private ArrayList<BookData> bookDataArray;
    private ArrayList<BookDataOwned> bookDataOwnedArray;
    private ArrayList<BookDataWanted> bookDataWantedArray;
    private ArrayList<BookDataMatch> bookDataMatchArray;
    private Activity activity;
    private String imageurl;
    private int resource;


    public SearchResultsBaseAdapter(Activity activity, int resource, ArrayList<?> objectArray) {
        if (!objectArray.isEmpty() && objectArray.get(0) instanceof BookDataOwned) {
            //for (int i = 0; i < objectArray.size(); i++) {
            //    bookDataOwnedArray.add((BookDataOwned) objectArray.get(i));
            //}
            bookDataOwnedArray = (ArrayList<BookDataOwned>) objectArray;
        } else if (!objectArray.isEmpty() && objectArray.get(0) instanceof BookDataWanted) {
            //for (int i = 0; i < objectArray.size(); i++) {
            //    bookDataWantedArray.add((BookDataWanted) objectArray.get(i));
            //}
            bookDataWantedArray = (ArrayList<BookDataWanted>) objectArray;
        } else if (!objectArray.isEmpty() && objectArray.get(0) instanceof BookData) {
            //for (int i = 0; i < objectArray.size(); i++) {
            //    bookDataArray.add((BookData) objectArray.get(i));
            //}
            bookDataArray = (ArrayList<BookData>) objectArray;
        } else if (!objectArray.isEmpty() && objectArray.get(0) instanceof BookDataMatch) {
            //for (int i = 0; i < objectArray.size(); i++) {
            //    bookDataArray.add((BookData) objectArray.get(i));
            //}
            bookDataMatchArray = (ArrayList<BookDataMatch>) objectArray;
        }

        this.activity = activity;
        this.resource = resource;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if (resource == R.layout.layout_owned_results) {
            return bookDataOwnedArray.size();
        } else if (resource == R.layout.layout_wanted_results) {
            return bookDataWantedArray.size();
        } else if (resource == R.layout.layout_search_results) {
            return bookDataArray.size();
        }else if (resource == R.layout.layout_match_results){
            return bookDataMatchArray.size();
        }
        else {
            return 0;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (convertView == null) {
            view = inflater.inflate(resource, null);
        }

        TextView bookTitle = (TextView) view.findViewById(R.id.book_title);
        TextView bookAuthor = (TextView) view.findViewById(R.id.book_author);
        TextView bookYear = (TextView) view.findViewById(R.id.book_year);
        ImageView bookCover = (ImageView) view.findViewById(R.id.book_cover);

        if (resource == R.layout.layout_owned_results) {
            bookTitle.setText(bookDataOwnedArray.get(position).getTitle());
            bookAuthor.setText(bookDataOwnedArray.get(position).getAuthor());
            bookYear.setText(bookDataOwnedArray.get(position).getYear());
            imageurl = activity.getResources().getString(R.string.server_url) + "covers/"
                    + bookDataOwnedArray.get(position).getBookID() + ".jpg";
        } else if (resource == R.layout.layout_wanted_results) {
            bookTitle.setText(bookDataWantedArray.get(position).getTitle());
            bookAuthor.setText(bookDataWantedArray.get(position).getAuthor());
            bookYear.setText(bookDataWantedArray.get(position).getYear());
            imageurl = activity.getResources().getString(R.string.server_url) + "covers/"
                    + bookDataWantedArray.get(position).getBookID() + ".jpg";
        } else if (resource == R.layout.layout_search_results) {
            bookTitle.setText(bookDataArray.get(position).getTitle());
            bookAuthor.setText(bookDataArray.get(position).getAuthor());
            bookYear.setText(bookDataArray.get(position).getYear());
            imageurl = activity.getResources().getString(R.string.server_url) + "covers/"
                    + bookDataArray.get(position).getBookID() + ".jpg";
        }else if (resource == R.layout.layout_match_results) {
            bookTitle.setText(bookDataMatchArray.get(position).getTitle());
            bookAuthor.setText(bookDataMatchArray.get(position).getAuthor());
            bookYear.setText(bookDataMatchArray.get(position).getYear());
            imageurl = activity.getResources().getString(R.string.server_url) + "covers/"
                    + bookDataMatchArray.get(position).getBookID() + ".jpg";
        }

        new DownloadImageTask(bookCover).execute(imageurl);

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
