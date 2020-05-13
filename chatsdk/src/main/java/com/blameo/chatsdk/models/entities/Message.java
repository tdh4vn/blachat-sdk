package com.blameo.chatsdk.models.entities;

import android.database.Cursor;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.blameo.chatsdk.repositories.local.Constant;
import com.blameo.chatsdk.utils.ChatSdkDateFormatUtil;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;


@Entity(tableName = Constant.MESSAGE_TABLE_NAME)
public class Message implements Serializable {

    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = Constant.MESSAGE_ID)
    private String id;

    @SerializedName("author_id")
    @ColumnInfo(name = Constant.MESSAGE_AUTHOR_ID)
    private String authorId;

    @SerializedName("channel_id")
    @ColumnInfo(name = Constant.MESSAGE_CHANNEL_ID)
    private String channelId;

    @SerializedName("content")
    @ColumnInfo(name = Constant.MESSAGE_CONTENT)
    private String content;

    @SerializedName("created_at")
    @ColumnInfo(name = Constant.MESSAGE_CREATED_AT)
    private Date createdAt;

    @SerializedName("updated_at")
    @ColumnInfo(name = Constant.MESSAGE_UPDATED_AT)
    private Date updatedAt;

    @SerializedName("sent_at")
    @ColumnInfo(name = Constant.MESSAGE_SENT_AT)
    private Date sentAt;

    @SerializedName("is_system_message")
    @ColumnInfo(name = Constant.MESSAGE_IS_SYSTEM)
    private int isSystemMessage;

    @SerializedName("custom_data")
    @ColumnInfo(name = Constant.MESSAGE_CUSTOM_DATA)
    protected HashMap<String, Object> customData;


    public Message(String id, String authorId, String channelId, String content, Date createdAt, Date updatedAt, Date sentAt, int isSystemMessage, HashMap<String, Object> customData) {
        this.id = id;
        this.authorId = authorId;
        this.channelId = channelId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.sentAt = sentAt;
        this.isSystemMessage = isSystemMessage;
        this.customData = customData;
    }

    public Message() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public int getIsSystemMessage() {
        return isSystemMessage;
    }

    public void setIsSystemMessage(int isSystemMessage) {
        this.isSystemMessage = isSystemMessage;
    }

    public HashMap<String, Object> getCustomData() {
        return customData;
    }

    public void setCustomData(HashMap<String, Object> customData) {
        this.customData = customData;
    }
}
