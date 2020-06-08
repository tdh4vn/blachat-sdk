package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;


public class DeleteMessageBody {

    @SerializedName("message_id")
    private String messageId;

    @SerializedName("channel_id")
    private String channelId;

    public DeleteMessageBody(String messageId, String channelId) {
        this.messageId = messageId;
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
