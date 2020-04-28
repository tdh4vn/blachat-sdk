package com.blameo.chatsdk.models.pojos;

public class UserInChannel {

    private String id;
    private String channelId;
    private String userId;
    private String lastReceive;
    private String lastSeen;

    public UserInChannel(String id, String channelId, String userId, String lastReceive, String lastSeen) {
        this.id = id;
        this.channelId = channelId;
        this.userId = userId;
        this.lastReceive = lastReceive;
        this.lastSeen = lastSeen;
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

    public String getLastReceive() {
        return lastReceive;
    }

    public void setLastReceive(String lastReceive) {
        this.lastReceive = lastReceive;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }
}
