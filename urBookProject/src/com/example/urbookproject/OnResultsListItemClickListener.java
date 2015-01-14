
package com.example.urbookproject;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class OnResultsListItemClickListener implements OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // TODO Auto-generated method stub

        Context context = view.getContext();
        TextView bookTitle = (TextView) view.findViewById(R.id.book_title);
        TextView bookAuthor = (TextView) view.findViewById(R.id.book_author);
        TextView bookYear = (TextView) view.findViewById(R.id.book_year);
        TextView bookID = (TextView) view.findViewById(R.id.book_id);

        String title = bookTitle.getText().toString();
        String author = bookAuthor.getText().toString();
        String year = bookYear.getText().toString();
        String bkid = bookID.getText().toString();

        Intent intent = new Intent(context, InsertBook.class);
        intent.putExtra("BOOK_TITLE", title.toString());
        intent.putExtra("BOOK_AUTHOR", author.toString());
        intent.putExtra("BOOK_YEAR", year.toString());
        intent.putExtra("BOOK_ID", bkid.toString());

        context.startActivity(intent);
    }
}
