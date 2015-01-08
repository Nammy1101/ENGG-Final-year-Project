
package com.example.urbookproject;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchResultsBaseAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList title, author, year;
    private static LayoutInflater inflater = null;
    int ID;
    private Context context;

    public SearchResultsBaseAdapter(Activity activity, ArrayList title, ArrayList author,
            ArrayList year, int userID) {
        this.activity = activity;
        this.title = title;
        this.author = author;
        this.year = year;
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
            view = inflater.inflate(R.layout.layout_search_results, null);
        }

        TextView bookTitle = (TextView) view.findViewById(R.id.book_title);
        TextView bookAuthor = (TextView) view.findViewById(R.id.book_author);
        TextView bookYear = (TextView) view.findViewById(R.id.book_year);

        bookTitle.setText(title.get(position).toString());
        bookAuthor.setText(author.get(position).toString());
        bookYear.setText(year.get(position).toString());
        /*
        view.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, InsertBook.class);
				intent.putExtra("BOOK_TITLE" , title.toString());
				intent.putExtra("BOOK_AUTHOR" , author.toString());
				intent.putExtra("BOOK_YEAR" , author.toString());
				intent.putExtra("USER_ID", ID);
				
				context.startActivity(intent);
				
			}
        	
        	
        });
*/
        return view;
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return position;
    }
}
