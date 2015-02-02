
package com.example.urbookproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class OnResultsListItemClickListener implements OnItemClickListener, OnItemLongClickListener {
    private String caller;
    private String title, author, year, bkid;
    private TextView bookTitle, bookAuthor, bookYear, bookID, ownedID, wantedID;
    private BookList list;

    public OnResultsListItemClickListener(String caller) {
        this.caller = caller;
    }
    
    public OnResultsListItemClickListener(String caller, BookList b) {
        this.caller = caller;
        this.list = b;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // TODO Auto-generated method stub

        if (caller.equals("SearchResults")) {
            Context context = view.getContext();
            bookTitle = (TextView) view.findViewById(R.id.book_title);
            bookAuthor = (TextView) view.findViewById(R.id.book_author);
            bookYear = (TextView) view.findViewById(R.id.book_year);
            bookID = (TextView) view.findViewById(R.id.book_id);

            title = bookTitle.getText().toString();
            author = bookAuthor.getText().toString();
            year = bookYear.getText().toString();
            bkid = bookID.getText().toString();

            Intent intent = new Intent(context, InsertBook.class);
            intent.putExtra("BOOK_TITLE", title.toString());
            intent.putExtra("BOOK_AUTHOR", author.toString());
            intent.putExtra("BOOK_YEAR", year.toString());
            intent.putExtra("BOOK_ID", bkid.toString());

            context.startActivity(intent);
        } else if (caller.equals("OwnedList")) {

        } else if (caller.equals("WantedList")) {

        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setCancelable(true);
        builder.setTitle("Delete choice");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //adapter.remove(adapter.getItem(position));
                list.remove(position);
            }
        });
        if (caller.equals("OwnedList")) {
            return true;
        } else if (caller.equals("WantedList")) {
            return true;
        } else {
            return false;
        }
    }
}
