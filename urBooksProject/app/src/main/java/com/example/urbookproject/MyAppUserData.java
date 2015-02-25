package com.example.urbookproject;

import android.app.Application;

public class MyAppUserData extends Application {
    public ImageCache cache;
    private UserData userData;

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
}
