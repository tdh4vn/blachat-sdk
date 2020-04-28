package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;


public class CreateMessageBody {

    @SerializedName("type")
    private int type;

    @SerializedName("message")
    private String content;

    @SerializedName("sent_at")
    private String sentAt;

    @SerializedName("channel_id")
    private String channelId;

    public CreateMessageBody(String content, int type, String channelId) {
        this.type = type;
        this.content = content;
        this.channelId = channelId;
    }

    public CreateMessageBody(int type, String content, String sentAt, String channelId) {
        this.type = type;
        this.content = content;
        this.sentAt = sentAt;
        this.channelId = channelId;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }
}
