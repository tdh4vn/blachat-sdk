package com.blameo.chatsdk.models.results;

import com.blameo.chatsdk.models.pojos.RemoteUserChannel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MembersInChannel extends BaseResult{

    @SerializedName("channel_id")
    private String channelId;

    @SerializedName("list_member")
    private ArrayList<RemoteUserChannel> userChannels;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public ArrayList<RemoteUserChannel> getUserChannels() {
        return userChannels;
    }

    public void setUserChannels(ArrayList<RemoteUserChannel> userChannels) {
        this.userChannels = userChannels;
    }


}
