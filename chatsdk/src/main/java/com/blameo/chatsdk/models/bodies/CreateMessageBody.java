package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;

public class CreateMessageBody {

    @SerializedName("type")
    private int type;

    @SerializedName("message")
    private String content;

    public CreateMessageBody(String content, int type, String channel_id) {
        this.type = type;
        this.content = content;
        this.channel_id = channel_id;
    }

    @SerializedName("channel_id")
    private String channel_id;

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

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }


}
