package com.example.urbookproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rds23 on 2/4/2015.
 */
public class BookDataOwned extends BookData {
    public static final Parcelable.Creator<BookDataOwned> CREATOR = new Parcelable.Creator<BookDataOwned>() {
        @Override
        public BookDataOwned createFromParcel(Parcel source) {
            return new BookDataOwned(source);
        }

        @Override
        public BookDataOwned[] newArray(int size) {
            return new BookDataOwned[size];
        }
    };

    private String ownedID;
    private String keep;
    private String trade;
    private String sell;

    public BookDataOwned() {
        super("", "", "", "", "", "", "");
        this.ownedID = "";
        this.keep = "";
        this.trade = "";
        this.sell = "";
    }

    public BookDataOwned(String author, String bookID, String hasCover, String isbn10,
                         String isbn13, String title, String year, String ownedID, String keep,
                         String trade, String sell) {
        super(author, bookID, hasCover, isbn10, isbn13, title, year);
        this.ownedID = ownedID;
        this.keep = keep;
        this.trade = trade;
        this.sell = sell;
    }

    public BookDataOwned(Parcel in) {
        String[] inData = new String[11];
        in.readStringArray(inData);

        super.setAuthor(inData[0]);
        super.setBookID(inData[1]);
        super.setHasCover(inData[2]);
        super.setIsbn10(inData[3]);
        super.setIsbn13(inData[4]);
        super.setTitle(inData[5]);
        super.setYear(inData[6]);
        this.ownedID = inData[7];
        this.keep = inData[8];
        this.trade = inData[9];
        this.sell = inData[10];
    }

    public String getOwnedID() {
        return ownedID;
    }

    public void setOwnedID(String ownedID) {
        this.ownedID = ownedID;
    }

    public String getKeep() {
        return keep;
    }

    public void setKeep(String keep) {
        this.keep = keep;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getSell() {
        return sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
    }

    public String getFilterableString() {
        String filterString = super.getFilterableString();

        if (this.keep.equals("1")) { filterString += " keep"; }
        if (this.trade.equals("1")) { filterString += " trade"; }
        if (this.sell.equals("1")) { filterString += " sell"; }

        return filterString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeStringArray(new String[]{super.getAuthor(), super.getBookID(),
                super.getHasCover(), super.getIsbn10(), super.getIsbn13(), super.getTitle(),
                super.getYear(), this.ownedID, this.keep, this.trade, this.sell});
    }
}
