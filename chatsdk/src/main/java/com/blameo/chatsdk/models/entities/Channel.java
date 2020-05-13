package com.blameo.chatsdk.models.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.blameo.chatsdk.repositories.local.Constant;
import com.blameo.chatsdk.utils.ChatSdkDateFormatUtil;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = Constant.CHANNEL_TABLE_NAME)
public class Channel implements Serializable {

    @SerializedName("id")
    @PrimaryKey
    private String id;

    @SerializedName("name")
    @ColumnInfo(name = Constant.CHANNEL_NAME)
    private String name;

    @SerializedName("avatar")
    @ColumnInfo(name = Constant.CHANNEL_AVATAR)
    private String avatar;

    @SerializedName("type")
    @ColumnInfo(name = Constant.CHANNEL_TYPE)
    private int type;

    @SerializedName("updated_at")
    @ColumnInfo(name = Constant.CHANNEL_UPDATED_AT)
    private Date updatedAt;

    @SerializedName("created_at")
    @ColumnInfo(name = Constant.CHANNEL_CREATED_AT)
    private Date createdAt;

    @SerializedName("last_message_id")
    @ColumnInfo(name = Constant.CHANNEL_LAST_MESSAGE_ID)
    private String lastMessageId;

    @SerializedName("last_messages")
    @Ignore
    private List<Message> lastMessages;

    @SerializedName("custom_data")
    @ColumnInfo(name = Constant.CHANNEL_CUSTOM_DATA)
    protected String customData;


    public Channel(String id, String name, String avatar, int type, Date updatedAt, Date createdAt, String lastMessageId, String customData) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.type = type;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.lastMessageId = lastMessageId;
        this.customData = customData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedAtString() {
        return ChatSdkDateFormatUtil.parse(updatedAt);
    }

    public String getCreatedAtString() {
        return ChatSdkDateFormatUtil.parse(createdAt);
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public String getCustomData() {
        return customData;
    }

    public List<Message> getLastMessages() {
        return lastMessages;
    }

    public void setLastMessages(List<Message> lastMessages) {
        this.lastMessages = lastMessages;
    }
}
