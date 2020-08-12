package com.blameo.chatsdk.models.events;

import com.google.gson.annotations.SerializedName;

public class DeleteMessageEvent {
    @SerializedName("message_id")
    public String messageId;
}
