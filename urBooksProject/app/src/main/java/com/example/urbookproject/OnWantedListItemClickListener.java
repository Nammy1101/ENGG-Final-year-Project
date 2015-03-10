package com.example.urbookproject;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class OnWantedListItemClickListener implements OnItemClickListener {
    private WantedResultsBaseAdapter adapter;

    public OnWantedListItemClickListener(WantedResultsBaseAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(view.getContext(), WantedListItemChange.class);
        intent.putExtra("bookDataWanted", ((BookDataWanted) adapter.getItem(position)));
        view.getContext().startActivity(intent);
    }
}
