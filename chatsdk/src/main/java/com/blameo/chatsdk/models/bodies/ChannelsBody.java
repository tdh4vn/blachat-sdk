package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ChannelsBody {

    @SerializedName("channelIds")
    private List<String> channelIds;

    public List<String> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<String> channelIds) {
        this.channelIds = channelIds;
    }

    public ChannelsBody(List<String> channelIds) {
        this.channelIds = channelIds;
    }
}
