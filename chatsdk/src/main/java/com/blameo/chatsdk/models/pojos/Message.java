package com.blameo.chatsdk.models.pojos;

import android.database.Cursor;

import com.blameo.chatsdk.utils.ChatSdkDateFormatUtil;
import com.blameo.chatsdk.utils.GsonUtil;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

public class Message extends CustomData implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("author_id")
    private String authorId;

    @SerializedName("channel_id")
    private String channelId;

    @SerializedName("content")
    private String content;

    @SerializedName("type")
    private int type;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;

    @SerializedName("sent_at")
    private Date sentAt;

    @SerializedName("seen_at")
    private Date seenAt;


    @SerializedName("is_system_message")
    private boolean isSystemMessage;

    @SerializedName("custom_data")
    private HashMap<String, Object> customData;


    public Message(String id, String authorId, String channelId, String content, int type, Date createdAt, Date updatedAt, Date sentAt, Date seenAt, boolean isSystemMessage, HashMap<String, Object> customData) {
        this.id = id;
        this.authorId = authorId;
        this.channelId = channelId;
        this.content = content;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.sentAt = sentAt;
        this.seenAt = seenAt;
        this.isSystemMessage = isSystemMessage;
        this.customData = customData;
    }

    public Message(String id, String authorId, String channelId, String content, int type, HashMap<String, Object> customData) {
        this.id = id;
        this.authorId = authorId;
        this.channelId = channelId;
        this.content = content;
        this.type = type;
        this.customData = customData;
    }

    public Message(Cursor cursor) throws ParseException {
        this.id = cursor.getString(0);
        this.authorId = cursor.getString(1);
        this.channelId = cursor.getString(2);
        this.content = cursor.getString(3);
        this.type = cursor.getInt(4);
        this.createdAt = ChatSdkDateFormatUtil.parse(cursor.getString(5));
        this.updatedAt = ChatSdkDateFormatUtil.parse(cursor.getString(6));
        this.sentAt = ChatSdkDateFormatUtil.parse(cursor.getString(7));
        this.seenAt = ChatSdkDateFormatUtil.parse(cursor.getString(8));
        this.customData = GsonUtil.jsonToMap(cursor.getString(9));
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public Date getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(Date seenAt) {
        this.seenAt = seenAt;
    }

    public String getCreatedAtString() {
        return ChatSdkDateFormatUtil.parse(createdAt);
    }

    public String getUpdatedAtString() {
        return ChatSdkDateFormatUtil.parse(updatedAt);
    }

    public String getSentAtString() {
        return ChatSdkDateFormatUtil.parse(sentAt);
    }

    public String getSeenAtString() {
        return ChatSdkDateFormatUtil.parse(seenAt);
    }

    public String getCustomDataString() {
        return GsonUtil.mapToJSON(customData);
    }

    public boolean isSystemMessage() {
        return isSystemMessage;
    }

    public void setSystemMessage(boolean systemMessage) {
        isSystemMessage = systemMessage;
    }
}
