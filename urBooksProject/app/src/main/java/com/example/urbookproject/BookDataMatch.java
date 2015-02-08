package com.example.urbookproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nammy on 2/7/2015.
 */
public class BookDataMatch extends BookData {
    public static final Parcelable.Creator<BookDataMatch> CREATOR = new Parcelable.Creator<BookDataMatch>() {
        @Override
        public BookDataMatch createFromParcel(Parcel source) {
            return new BookDataMatch(source);
        }

        @Override
        public BookDataMatch[] newArray(int size) {
            return new BookDataMatch[size];
        }
    };

    private String matchID;

    public BookDataMatch() {
        super("", "", "", "", "", "", "");
        this.matchID = "";
    }

    public BookDataMatch(String author, String bookID, String hasCover, String isbn10,
                         String isbn13, String title, String year, String matchID) {
        super(author, bookID, hasCover, isbn10, isbn13, title, year);
        this.matchID = matchID;
    }

    public BookDataMatch(Parcel in) {
        String[] inData = new String[8];
        in.readStringArray(inData);

        super.setAuthor(inData[0]);
        super.setBookID(inData[1]);
        super.setHasCover(inData[2]);
        super.setIsbn10(inData[3]);
        super.setIsbn13(inData[4]);
        super.setTitle(inData[5]);
        super.setYear(inData[6]);
        this.matchID = inData[7];
    }

    public String getMatchID() {
        return matchID;
    }

    public void setMatchID(String matchID) {
        this.matchID = matchID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeStringArray(new String[]{super.getAuthor(), super.getBookID(),
                super.getHasCover(), super.getIsbn10(), super.getIsbn13(), super.getTitle(),
                super.getYear(), this.matchID});
    }

}
