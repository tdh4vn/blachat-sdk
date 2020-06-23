package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;


public class UpdateMessageBody {

    @SerializedName("content")
    private String content;

    @SerializedName("message_id")
    private String messageId;

    @SerializedName("channel_id")
    private String channelId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public UpdateMessageBody(String content, String messageId, String channelId) {
        this.content = content;
        this.messageId = messageId;
        this.channelId = channelId;
    }
}
