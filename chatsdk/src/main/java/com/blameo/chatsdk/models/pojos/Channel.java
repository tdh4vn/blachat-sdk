package com.blameo.chatsdk.models.pojos;

import android.database.Cursor;

import com.blameo.chatsdk.utils.ChatSdkDateFormatUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

public class Channel extends CustomData implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("type")
    private int type;

    @SerializedName("updated_at")
    private Date updatedAt;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("last_message_id")
    private String lastMessageId;

    @Expose(serialize = false, deserialize = false)
    private Message lastMessage;

    public Channel(Cursor cursor) throws ParseException {
        this.id = cursor.getString(0);
         this.name = cursor.getString(1);
         this.avatar = cursor.getString(2);
         this.type = cursor.getInt(3);
         this.updatedAt = ChatSdkDateFormatUtil.parse(cursor.getString(4));
         this.createdAt = ChatSdkDateFormatUtil.parse(cursor.getString(5));
         this.lastMessageId = cursor.getString(6);
    }

    public Channel(String id, String name, String avatar, int type, Date updatedAt, Date createdAt, String lastMessageId) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.type = type;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.lastMessageId = lastMessageId;
    }

    public Channel(String id, String name, String avatar, int type, Date updatedAt, Date createdAt, String lastMessageId, Message lastMessage) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.type = type;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.lastMessageId = lastMessageId;
        this.lastMessage = lastMessage;
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

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
