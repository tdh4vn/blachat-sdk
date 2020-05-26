package com.blameo.chatsdk.models.events;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InviteUsersEvent {
    @SerializedName("channel_id")
    public String channelId;

    @SerializedName("user_ids")
    public List<String> userIds;

    @SerializedName("event_id")
    public String eventId;
}
