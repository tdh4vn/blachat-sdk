package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;


public class CreateMessageBody {

    @SerializedName("type")
    private int type;

    @SerializedName("message")
    private String content;

    @SerializedName("channel_id")
    private String channelId;

    @SerializedName("sent_at")
    private long sentAt;

    @SerializedName("custom_data")
    private String customData;

    @SerializedName("local_id")
    private String localId;

    public CreateMessageBody(int type, String content, String channelId, long sentAt, String customData, String localId) {
        this.type = type;
        this.content = content;
        this.channelId = channelId;
        this.sentAt = sentAt;
        this.customData = customData;
        this.localId = localId;
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

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }
}
