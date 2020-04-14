package com.blameo.chatsdk.models.results;

import com.blameo.chatsdk.models.pojos.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetUsersByIdsResult extends BaseResult {

    @SerializedName("data")
    private ArrayList<User> data;

    public ArrayList<User> getData() {
        return data;
    }

    public void setData(ArrayList<User> data) {
        this.data = data;
    }

}
