
package com.example.urbookproject;

import java.util.ArrayList;

public class BookList {
    public ArrayList<String> title, author, year, isbn10, isbn13, has_cover;
    public ArrayList<String> user_id, book_id, owned_id, wanted_id;
    public ArrayList<String> keep, trade, sell, purchase;

    public BookList() {
        title = new ArrayList<String>();
        author = new ArrayList<String>();
        year = new ArrayList<String>();
        isbn10 = new ArrayList<String>();
        isbn13 = new ArrayList<String>();
        has_cover = new ArrayList<String>();
        user_id = new ArrayList<String>();
        book_id = new ArrayList<String>();
        owned_id = new ArrayList<String>();
        wanted_id = new ArrayList<String>();
        keep = new ArrayList<String>();
        trade = new ArrayList<String>();
        sell = new ArrayList<String>();
        purchase = new ArrayList<String>();
    }

    public void remove(int index) {
        if (title.size() > 0) {
            title.remove(title.get(index));
        }
        if (author.size() > 0) {
            author.remove(author.get(index));
        }
        if (year.size() > 0) {
            year.remove(year.get(index));
        }
        if (isbn10.size() > 0) {
            isbn10.remove(isbn10.get(index));
        }
        if (isbn13.size() > 0) {
            isbn13.remove(isbn13.get(index));
        }
        if (has_cover.size() > 0) {
            has_cover.remove(has_cover.get(index));
        }
        if (user_id.size() > 0) {
            user_id.remove(user_id.get(index));
        }
        if (book_id.size() > 0) {
            book_id.remove(book_id.get(index));
        }
        if (owned_id.size() > 0) {
            owned_id.remove(owned_id.get(index));
        }
        if (wanted_id.size() > 0) {
            wanted_id.remove(wanted_id.get(index));
        }
        if (keep.size() > 0) {
            keep.remove(keep.get(index));
        }
        if (trade.size() > 0) {
            trade.remove(trade.get(index));
        }
        if (sell.size() > 0) {
            sell.remove(sell.get(index));
        }
        if (purchase.size() > 0) {
            purchase.remove(purchase.get(index));
        }
    }
}
