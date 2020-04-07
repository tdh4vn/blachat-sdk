package com.blameo.data.models.results;

import com.blameo.data.models.pojos.Channel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetChannelResult extends BaseResult{

    @SerializedName("data")
    private ArrayList<Channel> data;

    public ArrayList<Channel> getData() {
        return data;
    }

    public void setData(ArrayList<Channel> data) {
        this.data = data;
    }

}
