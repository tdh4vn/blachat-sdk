package com.blameo.chatsdk.models.events;

import com.google.gson.annotations.SerializedName;

public class UserLeaveChannel {
    @SerializedName("channel_id")
    public String channelId;

    @SerializedName("user_id")
    public String userID;
}
