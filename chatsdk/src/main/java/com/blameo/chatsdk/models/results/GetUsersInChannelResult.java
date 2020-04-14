package com.blameo.chatsdk.models.results;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetUsersInChannelResult extends BaseResult{

    @SerializedName("data")
    private ArrayList<String> data;

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

}
