package com.example.urbookproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rstancu on 2/2/2015.
 */
public class BookData implements Parcelable {
    public static final Parcelable.Creator<BookData> CREATOR = new Parcelable.Creator<BookData>() {
        @Override
        public BookData createFromParcel(Parcel source) {
            return new BookData(source);
        }

        @Override
        public BookData[] newArray(int size) {
            return new BookData[size];
        }
    };
    /* Data members corresponding to urBooks.books table */
    private String author;
    private String bookID;
    private String hasCover;
    private String isbn10;
    private String isbn13;
    private String title;
    private String year;

    public BookData() {
        this.author = "";
        this.bookID = "";
        this.hasCover = "";
        this.isbn10 = "";
        this.isbn13 = "";
        this.title = "";
        this.year = "";
    }

    public BookData(String author, String bookID, String hasCover, String isbn10, String isbn13,
                    String title, String year) {
        this.author = author;
        this.bookID = bookID;
        this.hasCover = hasCover;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.title = title;
        this.year = year;
    }

    public BookData(Parcel in) {
        String[] inData = new String[7];
        in.readStringArray(inData);

        this.author = inData[0];
        this.bookID = inData[1];
        this.hasCover = inData[2];
        this.isbn10 = inData[3];
        this.isbn13 = inData[4];
        this.title = inData[5];
        this.year = inData[6];
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getHasCover() {
        return hasCover;
    }

    public void setHasCover(String hasCover) {
        this.hasCover = hasCover;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeStringArray(new String[]{this.author, this.bookID, this.hasCover,
                this.isbn10, this.isbn13, this.title, this.year});
    }
}
