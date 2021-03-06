package com.example.urbookproject;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class OnResultsListItemClickListener implements OnItemClickListener {
    private SearchResultsBaseAdapter adapter;

    public OnResultsListItemClickListener(SearchResultsBaseAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(view.getContext(), InsertBook.class);
        intent.putExtra("bookData", ((BookData) adapter.getItem(position)));
        view.getContext().startActivity(intent);
    }
}
