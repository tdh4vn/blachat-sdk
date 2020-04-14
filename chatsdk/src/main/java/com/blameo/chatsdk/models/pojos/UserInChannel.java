package com.blameo.chatsdk.models.pojos;

public class UserInChannel {

    private String id;
    private String channelId;
    private String userId;

    public UserInChannel(String id, String channelId, String userId) {
        this.id = id;
        this.channelId = channelId;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
