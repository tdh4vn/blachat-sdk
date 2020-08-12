package com.blameo.chatsdk.models.events;

import com.google.gson.annotations.SerializedName;

public class DeleteChannelEvent {
    @SerializedName("channel_id")
    public String channelId;
}
