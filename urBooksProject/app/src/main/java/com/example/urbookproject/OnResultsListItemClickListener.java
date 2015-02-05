package com.example.urbookproject;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;

public class OnResultsListItemClickListener implements OnItemClickListener {
    private String caller;
    private ArrayList<BookData> bookDataArray = new ArrayList<>();
    private ArrayList<BookDataOwned> bookDataOwnedArray = new ArrayList<>();
    private ArrayList<BookDataWanted> bookDataWantedArray = new ArrayList<>();

    public OnResultsListItemClickListener(String caller) {
        this.caller = caller;
    }

    public OnResultsListItemClickListener(String caller, ArrayList<?> objectArray) {
        this.caller = caller;

        if (!objectArray.isEmpty() && objectArray.get(0) instanceof BookDataOwned) {
            for (int i = 0; i < objectArray.size(); i++) {
                bookDataOwnedArray.add((BookDataOwned) objectArray.get(i));
            }
        } else if (!objectArray.isEmpty() && objectArray.get(0) instanceof BookDataWanted) {
            for (int i = 0; i < objectArray.size(); i++) {
                bookDataWantedArray.add((BookDataWanted) objectArray.get(i));
            }
        } else if (!objectArray.isEmpty() && objectArray.get(0) instanceof BookData) {
            for (int i = 0; i < objectArray.size(); i++) {
                bookDataArray.add((BookData) objectArray.get(i));
            }
            bookDataArray = (ArrayList<BookData>) objectArray;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (caller.equals("OwnedList")) {
            //Intent intent = new Intent(view.getContext(), OwnedList.class);
            //intent.putParcelableArrayListExtra("bookData", bookDataOwnedArray);
            //view.getContext().startActivity(intent);
        } else if (caller.equals("WantedList")) {

        } else {
            Intent intent = new Intent(view.getContext(), InsertBook.class);
            intent.putExtra("bookData", bookDataArray.get(position));
            view.getContext().startActivity(intent);
        }
    }
}
