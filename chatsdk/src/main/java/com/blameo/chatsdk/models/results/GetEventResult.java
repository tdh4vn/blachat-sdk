package com.blameo.chatsdk.models.results;

import com.blameo.chatsdk.models.events.GetEvent;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetEventResult {

    @SerializedName("data")
    private ArrayList<GetEvent> data;

    @SerializedName("message")
    private String message;

    public ArrayList<GetEvent> getData() {
        return data;
    }

    public void setData(ArrayList<GetEvent> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
