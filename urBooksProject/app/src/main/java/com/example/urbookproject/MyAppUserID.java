
package com.example.urbookproject;

import android.app.Application;

public class MyAppUserID extends Application {
    int ID;

    public int getUserID() {
        return ID;
    }

    public void setUserID(int userID) {
        this.ID = userID;
    }

}
