package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ChannelsBody {

    @SerializedName("channelIds")
    private ArrayList<String> channelIds;

    public ArrayList<String> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(ArrayList<String> channelIds) {
        this.channelIds = channelIds;
    }

    public ChannelsBody(ArrayList<String> channelIds) {
        this.channelIds = channelIds;
    }
}
