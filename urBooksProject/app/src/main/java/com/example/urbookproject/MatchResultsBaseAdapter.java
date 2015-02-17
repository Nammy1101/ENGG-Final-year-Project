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

public class MatchResultsBaseAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private ArrayList<BookDataMatch> bookDataMatch;
    private Activity activity;
    private int resource;


    public MatchResultsBaseAdapter(Activity activity, int resource,
                                   ArrayList<BookDataMatch> bookDataMatch) {
        this.bookDataMatch = bookDataMatch;

        this.activity = activity;
        this.resource = resource;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if (bookDataMatch != null) {
            return bookDataMatch.size();
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
            username.setText(bookDataMatch.get(position).getUsername());

            if (bookDataMatch.get(position).getTransactionType().equals("trade")) {
                transText.setText("Trade with: ");

                /* Set visibility for each item for trade view */
                incPriceView.setVisibility(View.GONE);
                incCoverView.setVisibility(View.VISIBLE);
                outPriceView.setVisibility(View.GONE);
                outCoverView.setVisibility(View.VISIBLE);

                String outgoingCoverURL = activity.getResources().getString(R.string.server_url)
                        + "covers/" + bookDataMatch.get(position).getOutgoingBook().getBookID()
                        + ".jpg";

                String incomingCoverURL = activity.getResources().getString(R.string.server_url)
                        + "covers/" + bookDataMatch.get(position).getIncomingBook().getBookID()
                        + ".jpg";

                //if (incomingCover.getDrawable() == null) {
                    new DownloadImageTask(incomingCover).execute(incomingCoverURL);
                //}

                //if (outgoingCover.getDrawable() == null) {
                    new DownloadImageTask(outgoingCover).execute(outgoingCoverURL);
                //}
            } else if (bookDataMatch.get(position).getTransactionType().equals("sell")) {
                transText.setText("Sell to: ");

                /* Set visibility for each item for sell view */
                incPriceView.setVisibility(View.VISIBLE);
                incCoverView.setVisibility(View.GONE);
                outPriceView.setVisibility(View.GONE);
                outCoverView.setVisibility(View.VISIBLE);

                incomingPrice.setText("$"+ bookDataMatch.get(position).getPrice());

                String outgoingCoverURL = activity.getResources().getString(R.string.server_url)
                        + "covers/" + bookDataMatch.get(position).getOutgoingBook().getBookID()
                        + ".jpg";

                //if (outgoingCover.getDrawable() == null) {
                    new DownloadImageTask(outgoingCover).execute(outgoingCoverURL);
                //}
            } else if (bookDataMatch.get(position).getTransactionType().equals("buy")) {
                transText.setText("Purchase from: ");

                /* Set visibility for each item for purchase view */
                incPriceView.setVisibility(View.GONE);
                incCoverView.setVisibility(View.VISIBLE);
                outPriceView.setVisibility(View.VISIBLE);
                outCoverView.setVisibility(View.GONE);

                outgoingPrice.setText("$"+ bookDataMatch.get(position).getPrice());

                String incomingCoverURL = activity.getResources().getString(R.string.server_url)
                        + "covers/" + bookDataMatch.get(position).getIncomingBook().getBookID()
                        + ".jpg";

                //if (incomingCover.getDrawable() == null) {
                    new DownloadImageTask(incomingCover).execute(incomingCoverURL);
                //}
            }
        }

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
            scaleImage(bmImage, bmImage.getHeight());
        }
    }
}
