package com.example.urbookproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rds23 on 2/2/2015.
 */
public class UserData implements Parcelable {
    public static final Parcelable.Creator<UserData> CREATOR = new Parcelable.Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel source) {
            return new UserData(source);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };
    /* Data members corresponding to urBooks.users table */
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String userID;
    private String userName;

    public UserData() {
        this.userID = "";
        this.userName = "";
        this.password = "";
        this.email = "";
        this.firstName = "";
        this.lastName = "";
    }

    public UserData(String userID, String userName, String password, String email, String firstName,
                    String lastName) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserData(Parcel in) {
        String[] inData = new String[6];
        in.readStringArray(inData);

        this.userID = inData[0];
        this.userName = inData[1];
        this.password = inData[2];
        this.email = inData[3];
        this.firstName = inData[4];
        this.lastName = inData[5];
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeStringArray(new String[]{this.userID, this.userName, this.password,
                this.email, this.firstName, this.lastName});
    }
}
