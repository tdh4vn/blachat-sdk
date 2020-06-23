package com.blameo.chatsdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.blameo.chatsdk.DemoApplication;

public class UserSP {

    private static UserSP instance;
    private String name = "shared_preference";
    private SharedPreferences preferences = DemoApplication.getInstance().getSharedPreferences(name, Context.MODE_PRIVATE);

    private UserSP() {
    }

    public static UserSP getInstance() {
        if(instance == null)
            instance = new UserSP();
        return instance;
    }

    public String getID() {
        return preferences.getString("user_id", "");
    }

    public void setID(String username) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("user_id", username);
        edit.apply();
    }

}
