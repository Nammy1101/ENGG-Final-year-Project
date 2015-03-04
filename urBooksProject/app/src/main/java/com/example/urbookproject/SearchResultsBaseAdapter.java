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

public class SearchResultsBaseAdapter extends BaseAdapter implements Filterable {
    private static LayoutInflater inflater = null;
    private ArrayList<BookData> bookDataArray;
    private ArrayList<BookData> bookDataArrayFiltered;
    private Activity activity;
    private int resource;
    private MyAppUserData cacheData;
    private BookSearchFilter bookSearchFilter = new BookSearchFilter();

    public SearchResultsBaseAdapter(Activity activity, int resource,
                                    ArrayList<BookData> bookArray) {
        bookDataArray = bookArray;
        bookDataArrayFiltered = bookArray;

        this.activity = activity;
        this.resource = resource;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.cacheData = ((MyAppUserData) this.activity.getApplicationContext());
    }

    public int getCount() {
        if (bookDataArrayFiltered != null) {
            return bookDataArrayFiltered.size();
        } else {
            return 0;
        }
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

        if (resource == R.layout.layout_search_results) {
            bookTitle.setText(bookDataArrayFiltered.get(position).getTitle());
            bookAuthor.setText(bookDataArrayFiltered.get(position).getAuthor());
            bookYear.setText(bookDataArrayFiltered.get(position).getYear());
            imageURL = activity.getResources().getString(R.string.server_url) + "covers/"
                    + bookDataArrayFiltered.get(position).getBookID() + ".jpg";
            imageName = bookDataArrayFiltered.get(position).getBookID() + ".jpg";
        }

        new DownloadImageTask(bookCover, imageName).execute(imageURL);

        return view;
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return bookDataArrayFiltered.get(position);
    }

    public Filter getFilter() {
        return bookSearchFilter;
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

    private class BookSearchFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterInput = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<BookData> originalList = bookDataArray;

            int count = originalList.size();
            final ArrayList<BookData> newList = new ArrayList<>(count);

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
            bookDataArrayFiltered = (ArrayList<BookData>) results.values;
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
