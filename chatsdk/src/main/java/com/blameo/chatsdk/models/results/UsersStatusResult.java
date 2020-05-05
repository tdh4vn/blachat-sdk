package com.blameo.chatsdk.models.results;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UsersStatusResult {

    @SerializedName("data")
    private ArrayList<UserStatus> data;

    public ArrayList<UserStatus> getData() {
        return data;
    }

    public void setData(ArrayList<UserStatus> data) {
        this.data = data;
    }
}
