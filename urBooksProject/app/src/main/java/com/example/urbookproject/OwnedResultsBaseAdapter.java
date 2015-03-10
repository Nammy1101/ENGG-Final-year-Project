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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;

public class OwnedResultsBaseAdapter extends BaseAdapter implements Filterable {
    private static LayoutInflater inflater = null;
    private ArrayList<BookDataOwned> bookDataOwnedArray;
    private ArrayList<BookDataOwned> bookDataOwnedFiltered;
    private Activity activity;
    private int resource;
    private MyAppUserData cacheData;
    private BookOwnedFilter bookOwnedFilter = new BookOwnedFilter();

    public OwnedResultsBaseAdapter(Activity activity, int resource,
                                   ArrayList<BookDataOwned> ownedArray) {
        bookDataOwnedArray = ownedArray;
        bookDataOwnedFiltered = ownedArray;

        this.activity = activity;
        this.resource = resource;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.cacheData = ((MyAppUserData) this.activity.getApplicationContext());
    }

    public int getCount() {
        if (bookDataOwnedFiltered != null) {
            return bookDataOwnedFiltered.size();
        }
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (convertView == null) {
            view = inflater.inflate(resource, null);
        }

        String imageURL = "";
        String imageName = "";
        TextView bookTitle = (TextView) view.findViewById(R.id.book_title);
        TextView bookAuthor = (TextView) view.findViewById(R.id.book_author);
        TextView bookYear = (TextView) view.findViewById(R.id.book_year);
        ImageView bookCover = (ImageView) view.findViewById(R.id.book_cover);

        if (resource == R.layout.layout_owned_results) {
            TextView keep = (TextView) view.findViewById(R.id.text_owned_keep);
            TextView trade = (TextView) view.findViewById(R.id.text_owned_trade);
            TextView sell = (TextView) view.findViewById(R.id.text_owned_sell);

            bookTitle.setText(bookDataOwnedFiltered.get(position).getTitle());
            bookAuthor.setText(bookDataOwnedFiltered.get(position).getAuthor());
            bookYear.setText(bookDataOwnedFiltered.get(position).getYear());

            if (bookDataOwnedFiltered.get(position).getKeep().equals("1")) {
                keep.setText("Keep: Yes");
                keep.setVisibility(View.VISIBLE);
                trade.setVisibility(View.GONE);
                sell.setVisibility(View.GONE);
            } else {
                keep.setText("Keep: No");
                keep.setVisibility(View.GONE);
            }

            if (bookDataOwnedFiltered.get(position).getTrade().equals("1")) {
                trade.setText("Trade: Yes");
                trade.setVisibility(View.VISIBLE);
            } else {
                trade.setText("Trade: No");
            }

            if (bookDataOwnedFiltered.get(position).getSell().equals("null") ||
                    bookDataOwnedFiltered.get(position).getSell().equals("0.00")) {
                sell.setText("");
                sell.setVisibility(View.GONE);
            } else {
                sell.setText("Sell for: $" + bookDataOwnedFiltered.get(position).getSell());
                sell.setVisibility(View.VISIBLE);
            }

            imageURL = activity.getResources().getString(R.string.server_url) + "covers/"
                    + bookDataOwnedFiltered.get(position).getBookID() + ".jpg";
            imageName = bookDataOwnedFiltered.get(position).getBookID() + ".jpg";
        }

        new DownloadImageTask(bookCover, imageName).execute(imageURL);

        return view;
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return bookDataOwnedFiltered.get(position);
    }

    public Filter getFilter() {
        return bookOwnedFilter;
    }

    public static class OwnedResultsComparator implements Comparator<BookData> {
        private String sortType;

        public OwnedResultsComparator(String sortType) {
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

    private class BookOwnedFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterInput = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<BookDataOwned> originalList = bookDataOwnedArray;

            int count = originalList.size();
            final ArrayList<BookDataOwned> newList = new ArrayList<>(count);

            String filter;

            for (int i = 0; i < count; i++) {
                filter = originalList.get(i).getFilterableString();

                if (filter.toLowerCase().contains(filterInput)) {
                    newList.add(originalList.get(i));
                }
            }

            results.values = newList;
            results.count = newList.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            bookDataOwnedFiltered = (ArrayList<BookDataOwned>) results.values;
            notifyDataSetChanged();
        }
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
            if (result == null) {
                bmImage.setImageBitmap(cacheData.cache.get(name));
                Log.d("CACHE", "Loaded: [" + name + "] from memory cache.");
                ImageCache.scaleImage(bmImage, cacheData.cache.getImageSize());
            } else {
                if (cacheData.cache.getImageSize() == 0) {
                    cacheData.cache.setImageSize(bmImage.getHeight());
                }

                cacheData.cache.put(name, result);

                bmImage.setImageBitmap(cacheData.cache.get(name));

                ImageCache.scaleImage(bmImage, cacheData.cache.getImageSize());

                Log.d("CACHE", "Fetched: [" + name + "] from URL.");
            }
        }
    }
}
