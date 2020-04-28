package com.blameo.chatsdk.models.results;

import com.blameo.chatsdk.models.pojos.RemoteUserChannel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetUsersInChannelResult extends BaseResult{

    @SerializedName("data")
    private ArrayList<RemoteUserChannel> data;

    public ArrayList<RemoteUserChannel> getData() {
        return data;
    }

    public void setData(ArrayList<RemoteUserChannel> data) {
        this.data = data;
    }

}
