package com.blameo.chatsdk.models.events;

public class ChannelCursor {
    private String userID;
    private String channelID;
    private String time;

    public ChannelCursor(String userID, String channelID, String time) {
        this.userID = userID;
        this.channelID = channelID;
        this.time = time;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
