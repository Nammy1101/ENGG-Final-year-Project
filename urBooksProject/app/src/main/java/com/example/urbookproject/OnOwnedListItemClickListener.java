package com.example.urbookproject;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class OnOwnedListItemClickListener implements OnItemClickListener {
    private OwnedResultsBaseAdapter adapter;

    public OnOwnedListItemClickListener(OwnedResultsBaseAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(view.getContext(), OwnedListItemChange.class);
        intent.putExtra("bookDataOwned", ((BookDataOwned) adapter.getItem(position)));
        view.getContext().startActivity(intent);
    }
}
