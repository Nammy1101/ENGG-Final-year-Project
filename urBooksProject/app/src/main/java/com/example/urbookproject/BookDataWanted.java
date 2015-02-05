package com.example.urbookproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rds23 on 2/4/2015.
 */
public class BookDataWanted extends BookData {
    public static final Parcelable.Creator<BookDataWanted> CREATOR = new Parcelable.Creator<BookDataWanted>() {
        @Override
        public BookDataWanted createFromParcel(Parcel source) {
            return new BookDataWanted(source);
        }

        @Override
        public BookDataWanted[] newArray(int size) {
            return new BookDataWanted[size];
        }
    };

    private String wantedID;
    private String trade;
    private String purchase;

    public BookDataWanted() {
        super("", "", "", "", "", "", "");
        this.wantedID = "";
        this.trade = "";
        this.purchase = "";
    }

    public BookDataWanted(String author, String bookID, String hasCover, String isbn10,
                          String isbn13, String title, String year, String wantedID, String trade,
                          String purchase) {
        super(author, bookID, hasCover, isbn10, isbn13, title, year);
        this.wantedID = wantedID;
        this.trade = trade;
        this.purchase = purchase;
    }

    public BookDataWanted(Parcel in) {
        String[] inData = new String[10];
        in.readStringArray(inData);

        super.setAuthor(inData[0]);
        super.setBookID(inData[1]);
        super.setHasCover(inData[2]);
        super.setIsbn10(inData[3]);
        super.setIsbn13(inData[4]);
        super.setTitle(inData[5]);
        super.setYear(inData[6]);
        this.wantedID = inData[7];
        this.trade = inData[8];
        this.purchase = inData[9];
    }

    public String getWantedID() {
        return wantedID;
    }

    public void setWantedID(String wantedID) {
        this.wantedID = wantedID;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getPurchase() {
        return purchase;
    }

    public void setPurchase(String purchase) {
        this.purchase = purchase;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeStringArray(new String[]{super.getAuthor(), super.getBookID(),
                super.getHasCover(), super.getIsbn10(), super.getIsbn13(), super.getTitle(),
                super.getYear(), this.wantedID, this.trade, this.purchase});
    }
}
