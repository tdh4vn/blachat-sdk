package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;


public class RemoveUserFromChannelBody {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("channel_id")
    private String channelId;

    public RemoveUserFromChannelBody(String userId, String channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
