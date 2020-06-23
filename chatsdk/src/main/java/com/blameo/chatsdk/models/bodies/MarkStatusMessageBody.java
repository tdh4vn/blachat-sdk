package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;

public class MarkStatusMessageBody {
    @SerializedName("message_id")
    String messageId;

    @SerializedName("channel_id")
    String channelId;

    @SerializedName("receive_id")
    String receiveId;

    public MarkStatusMessageBody(String messageId, String channelId, String receiveId) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.receiveId = receiveId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }
}
