package com.blameo.chatsdk.models.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.blameo.chatsdk.repositories.local.Constant;

import java.util.Date;

@Entity(
        tableName = Constant.UIC_TABLE_NAME,
        primaryKeys = {Constant.UIC_CHANNEL_ID, Constant.UIC_USER_ID}
)
public class UserInChannel {

    @ColumnInfo(name = Constant.UIC_CHANNEL_ID)
    private String channelId;

    @ColumnInfo(name = Constant.UIC_USER_ID)
    private String userId;

    @ColumnInfo(name = Constant.UIC_LAST_RECEIVE)
    private Date lastReceive;

    @ColumnInfo(name = Constant.UIC_LAST_SEEN)
    private Date lastSeen;

    public UserInChannel() {
    }

    public UserInChannel(String channelId, String userId, Date lastReceive, Date lastSeen) {
        this.channelId = channelId;
        this.userId = userId;
        this.lastReceive = lastReceive;
        this.lastSeen = lastSeen;
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

    public Date getLastReceive() {
        return lastReceive;
    }

    public void setLastReceive(Date lastReceive) {
        this.lastReceive = lastReceive;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }
}
