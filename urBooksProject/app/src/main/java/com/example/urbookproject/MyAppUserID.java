package com.example.urbookproject;

import android.app.Application;

public class MyAppUserID extends Application {
    int ID;
    private UserData userData;

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public int getUserID() {
        return ID;
    }

    public void setUserID(int userID) {
        this.ID = userID;
    }

}
