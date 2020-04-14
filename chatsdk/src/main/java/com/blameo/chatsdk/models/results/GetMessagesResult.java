package com.blameo.chatsdk.models.results;

import com.blameo.chatsdk.models.pojos.Message;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetMessagesResult extends BaseResult {

    @SerializedName("data")
    private ArrayList<Message> data;

    public ArrayList<Message> getData() {
        return data;
    }

    public void setData(ArrayList<Message> data) {
        this.data = data;
    }

}
