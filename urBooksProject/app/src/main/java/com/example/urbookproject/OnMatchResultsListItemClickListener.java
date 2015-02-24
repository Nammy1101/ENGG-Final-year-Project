package com.example.urbookproject;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

/**
 * Created by Nammy on 2/17/2015.
 */
public class OnMatchResultsListItemClickListener implements AdapterView.OnItemClickListener {
    private MatchResultsBaseAdapter adapter;
    private ArrayList<BookDataMatch> bookDataMatchArray = new ArrayList<>();

    public OnMatchResultsListItemClickListener(MatchResultsBaseAdapter adapter) {
        this.adapter = adapter;
    }

    public OnMatchResultsListItemClickListener( ArrayList<?> objectArray) {
        bookDataMatchArray = (ArrayList<BookDataMatch>) objectArray;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(view.getContext(), MatchedUserInformation.class);
        //intent.putExtra("matchedUserID", bookDataMatchArray.get(position).getUserID());
        intent.putExtra("matchedUserID", ((BookDataMatch) adapter.getItem(position)).getUserID());
        view.getContext().startActivity(intent);

    }

}
