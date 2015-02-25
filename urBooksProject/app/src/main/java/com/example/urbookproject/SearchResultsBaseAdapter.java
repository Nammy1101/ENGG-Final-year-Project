package com.example.urbookproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;

public class SearchResultsBaseAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private ArrayList<BookData> bookDataArray;
    private ArrayList<BookDataOwned> bookDataOwnedArray;
    private ArrayList<BookDataWanted> bookDataWantedArray;
    private ArrayList<BookDataMatch> bookDataMatchArray;
    private Activity activity;
    private String imageURL, imageName;
    private int resource;
    private MyAppUserData cacheData;


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
        } else if (!objectArray.isEmpty() && objectArray.get(0) instanceof BookDataMatch) {
            //for (int i = 0; i < objectArray.size(); i++) {
            //    bookDataArray.add((BookData) objectArray.get(i));
            //}
            bookDataMatchArray = (ArrayList<BookDataMatch>) objectArray;
        } else if (!objectArray.isEmpty() && objectArray.get(0) instanceof BookData) {
            //for (int i = 0; i < objectArray.size(); i++) {
            //    bookDataArray.add((BookData) objectArray.get(i));
            //}
            bookDataArray = (ArrayList<BookData>) objectArray;
        }

        this.activity = activity;
        this.resource = resource;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.cacheData = ((MyAppUserData)this.activity.getApplicationContext());
    }

    public int getCount() {
        if (resource == R.layout.layout_owned_results && bookDataOwnedArray != null) {
            return bookDataOwnedArray.size();
        } else if (resource == R.layout.layout_wanted_results && bookDataWantedArray != null) {
            return bookDataWantedArray.size();
        } else if (resource == R.layout.layout_search_results && bookDataArray != null) {
            return bookDataArray.size();
        } else if (resource == R.layout.layout_match_results && bookDataMatchArray != null) {
            return bookDataMatchArray.size();
        } else {
            return 0;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (convertView == null) {
            view = inflater.inflate(resource, null);
        }

        imageName = "";
        TextView bookTitle = (TextView) view.findViewById(R.id.book_title);
        TextView bookAuthor = (TextView) view.findViewById(R.id.book_author);
        TextView bookYear = (TextView) view.findViewById(R.id.book_year);
        ImageView bookCover = (ImageView) view.findViewById(R.id.book_cover);

        if (resource == R.layout.layout_owned_results) {
            TextView keep = (TextView) view.findViewById(R.id.text_owned_keep);
            TextView trade = (TextView) view.findViewById(R.id.text_owned_trade);
            TextView sell = (TextView) view.findViewById(R.id.text_owned_sell);

            bookTitle.setText(bookDataOwnedArray.get(position).getTitle());
            bookAuthor.setText(bookDataOwnedArray.get(position).getAuthor());
            bookYear.setText(bookDataOwnedArray.get(position).getYear());

            if (bookDataOwnedArray.get(position).getKeep().equals("1")) {
                keep.setText("Keep: Yes");
            } else {
                keep.setText("Keep: No");
            }

            if (bookDataOwnedArray.get(position).getTrade().equals("1")) {
                trade.setText("Trade: Yes");
            } else {
                trade.setText("Trade: No");
            }

            if (bookDataOwnedArray.get(position).getSell().equals("null")) {
                sell.setText("");
            } else {
                sell.setText("Sell for: $" + bookDataOwnedArray.get(position).getSell());
            }

            imageURL = activity.getResources().getString(R.string.server_url) + "covers/"
                    + bookDataOwnedArray.get(position).getBookID() + ".jpg";
            imageName = bookDataOwnedArray.get(position).getBookID() + ".jpg";
        } else if (resource == R.layout.layout_wanted_results) {
            TextView trade = (TextView) view.findViewById(R.id.text_wanted_trade);
            TextView purchase = (TextView) view.findViewById(R.id.text_wanted_purchase);

            bookTitle.setText(bookDataWantedArray.get(position).getTitle());
            bookAuthor.setText(bookDataWantedArray.get(position).getAuthor());
            bookYear.setText(bookDataWantedArray.get(position).getYear());

            if (bookDataWantedArray.get(position).getTrade().equals("1")) {
                trade.setText("Trade: Yes");
            } else {
                trade.setText("Trade: No");
            }

            if (bookDataWantedArray.get(position).getPurchase().equals("null")) {
                purchase.setText("");
            } else {
                purchase.setText("Purchase for: $" + bookDataWantedArray.get(position).getPurchase());
            }

            imageURL = activity.getResources().getString(R.string.server_url) + "covers/"
                    + bookDataWantedArray.get(position).getBookID() + ".jpg";
            imageName = bookDataWantedArray.get(position).getBookID() + ".jpg";
        } else if (resource == R.layout.layout_search_results) {
            bookTitle.setText(bookDataArray.get(position).getTitle());
            bookAuthor.setText(bookDataArray.get(position).getAuthor());
            bookYear.setText(bookDataArray.get(position).getYear());
            imageURL = activity.getResources().getString(R.string.server_url) + "covers/"
                    + bookDataArray.get(position).getBookID() + ".jpg";
            imageName = bookDataArray.get(position).getBookID() + ".jpg";
        }

        new DownloadImageTask(bookCover, imageName).execute(imageURL);

        return view;
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return position;
    }

    private void scaleImage(ImageView view, int boundBoxInDp) {
        /* Borrowed from:
        *   https://argillander.wordpress.com/2011/11/24/scale-image-into-imageview-then-resize-imageview-to-match-the-image/
        */
        // Get the ImageView and its bitmap
        Drawable drawing = view.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();

        // Get current dimensions
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) boundBoxInDp) / width;
        float yScale = ((float) boundBoxInDp) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        width = scaledBitmap.getWidth();
        height = scaledBitmap.getHeight();

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    public static class SearchResultsComparator implements Comparator<BookData> {
        private String sortType;

        public SearchResultsComparator(String sortType) {
            this.sortType = sortType;
        }

        @Override
        public int compare(BookData lhs, BookData rhs) {
            switch (sortType.toLowerCase()) {
                case "titleasc":
                    return lhs.getTitle().compareTo(rhs.getTitle());
                case "titledesc":
                    return rhs.getTitle().compareTo(lhs.getTitle());
                case "yearasc":
                    return lhs.getYear().compareTo(rhs.getYear());
                case "yeardesc":
                    return rhs.getYear().compareTo(lhs.getYear());
                case "authorasc":
                    return lhs.getAuthor().compareTo(rhs.getAuthor());
                case "authordesc":
                    return rhs.getAuthor().compareTo(lhs.getAuthor());
                default:
                    // default is titleasc
                    return lhs.getTitle().compareTo(rhs.getTitle());
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String name;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
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
            if (result == null) {
                bmImage.setImageBitmap(cacheData.cache.get(name));
                Log.d("CACHE", "Loaded: [" + name + "] from memory cache.");
                scaleImage(bmImage, cacheData.cache.getImageSize());
            } else {
                if (cacheData.cache.getImageSize() == 0) {
                    cacheData.cache.setImageSize(bmImage.getHeight());
                }

                cacheData.cache.put(name, result);

                bmImage.setImageBitmap(cacheData.cache.get(name));

                scaleImage(bmImage, cacheData.cache.getImageSize());

                Log.d("CACHE", "Fetched: [" + name + "] from URL.");
            }
        }
    }
}
