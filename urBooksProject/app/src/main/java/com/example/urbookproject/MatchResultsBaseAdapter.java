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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class MatchResultsBaseAdapter extends BaseAdapter implements Filterable {
    private static LayoutInflater inflater = null;
    private ArrayList<BookDataMatch> bookDataMatch;
    private ArrayList<BookDataMatch> bookDataFiltered;
    private Activity activity;
    private int resource;
    private MyAppUserData cacheData;
    private BookMatchFilter bookMatchFilter = new BookMatchFilter();

    public MatchResultsBaseAdapter(Activity activity, int resource,
                                   ArrayList<BookDataMatch> bookDataMatch) {
        this.bookDataMatch = bookDataMatch;
        this.bookDataFiltered = bookDataMatch;

        this.activity = activity;
        this.resource = resource;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.cacheData = ((MyAppUserData) this.activity.getApplicationContext());
    }

    public int getCount() {
        if (bookDataFiltered != null) {
            return bookDataFiltered.size();
        }
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (convertView == null) {
            view = inflater.inflate(resource, null);
        }

        TextView username = (TextView) view.findViewById(R.id.match_username);
        TextView transText = (TextView) view.findViewById(R.id.match_transaction_text);

        TextView incomingPrice = (TextView) view.findViewById(R.id.match_text_incoming_price);
        TextView outgoingPrice = (TextView) view.findViewById(R.id.match_text_outgoing_price);
        ImageView incomingCover = (ImageView) view.findViewById(R.id.match_their_book_cover);
        ImageView outgoingCover = (ImageView) view.findViewById(R.id.match_your_book_cover);

        RelativeLayout incPriceView = (RelativeLayout) view.findViewById(R.id.match_their_money);
        RelativeLayout incCoverView = (RelativeLayout) view.findViewById(R.id.match_their_book);
        RelativeLayout outPriceView = (RelativeLayout) view.findViewById(R.id.match_your_money);
        RelativeLayout outCoverView = (RelativeLayout) view.findViewById(R.id.match_your_book);

        if (resource == R.layout.layout_match_results) {
            username.setText(bookDataFiltered.get(position).getUsername());

            if (bookDataFiltered.get(position).getTransactionType().equals("trade")) {
                transText.setText("Trade with: ");

                /* Set visibility for each item for trade view */
                incPriceView.setVisibility(View.GONE);
                incCoverView.setVisibility(View.VISIBLE);
                outPriceView.setVisibility(View.GONE);
                outCoverView.setVisibility(View.VISIBLE);

                String outgoingName = bookDataFiltered.get(position).getOutgoingBook().getBookID()
                        + ".jpg";
                String outgoingCoverURL = activity.getResources().getString(R.string.server_url)
                        + "covers/" + bookDataFiltered.get(position).getOutgoingBook().getBookID()
                        + ".jpg";

                String incomingName = bookDataFiltered.get(position).getIncomingBook().getBookID()
                        + ".jpg";
                String incomingCoverURL = activity.getResources().getString(R.string.server_url)
                        + "covers/" + bookDataFiltered.get(position).getIncomingBook().getBookID()
                        + ".jpg";

                //if (incomingCover.getDrawable() == null) {
                new DownloadImageTask(incomingCover, incomingName).execute(incomingCoverURL);
                //}

                //if (outgoingCover.getDrawable() == null) {
                new DownloadImageTask(outgoingCover, outgoingName).execute(outgoingCoverURL);
                //}
            } else if (bookDataFiltered.get(position).getTransactionType().equals("sell")) {
                transText.setText("Sell to: ");

                /* Set visibility for each item for sell view */
                incPriceView.setVisibility(View.VISIBLE);
                incCoverView.setVisibility(View.GONE);
                outPriceView.setVisibility(View.GONE);
                outCoverView.setVisibility(View.VISIBLE);

                incomingPrice.setText("$" + bookDataFiltered.get(position).getPrice());

                String outgoingName = bookDataFiltered.get(position).getOutgoingBook().getBookID()
                        + ".jpg";
                String outgoingCoverURL = activity.getResources().getString(R.string.server_url)
                        + "covers/" + bookDataFiltered.get(position).getOutgoingBook().getBookID()
                        + ".jpg";

                //if (outgoingCover.getDrawable() == null) {
                new DownloadImageTask(outgoingCover, outgoingName).execute(outgoingCoverURL);
                //}
            } else if (bookDataFiltered.get(position).getTransactionType().equals("buy")) {
                transText.setText("Purchase from: ");

                /* Set visibility for each item for purchase view */
                incPriceView.setVisibility(View.GONE);
                incCoverView.setVisibility(View.VISIBLE);
                outPriceView.setVisibility(View.VISIBLE);
                outCoverView.setVisibility(View.GONE);

                outgoingPrice.setText("$" + bookDataFiltered.get(position).getPrice());

                String incomingName = bookDataFiltered.get(position).getIncomingBook().getBookID()
                        + ".jpg";
                String incomingCoverURL = activity.getResources().getString(R.string.server_url)
                        + "covers/" + bookDataFiltered.get(position).getIncomingBook().getBookID()
                        + ".jpg";

                //if (incomingCover.getDrawable() == null) {
                new DownloadImageTask(incomingCover, incomingName).execute(incomingCoverURL);
                //}
            }
        }

        return view;
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return bookDataFiltered.get(position);
    }

    public Filter getFilter() {
        return bookMatchFilter;
    }

    private class BookMatchFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterInput = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<BookDataMatch> originalList = bookDataMatch;

            int count = originalList.size();
            final ArrayList<BookDataMatch> newList = new ArrayList<>(count);

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
            bookDataFiltered = (ArrayList<BookDataMatch>) results.values;
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
