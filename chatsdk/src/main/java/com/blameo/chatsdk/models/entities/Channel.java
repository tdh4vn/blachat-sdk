package com.blameo.chatsdk.models.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.repositories.local.Constant;
import com.blameo.chatsdk.repositories.local.Converters;
import com.blameo.chatsdk.utils.BlaChatTextUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Entity(tableName = Constant.CHANNEL_TABLE_NAME)
public class Channel extends BaseEntity {

    @SerializedName("id")
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = Constant.CHANNEL_ID)
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

    @SerializedName(value = "lastMessageId", alternate = "last_message_id")
    @ColumnInfo(name = Constant.CHANNEL_LAST_MESSAGE_ID)
    private String lastMessageId;

    @SerializedName(value = "lastMessages", alternate = "last_messages")
    @Ignore
    private List<Message> lastMessages;

    @TypeConverters(Converters.class)
    @SerializedName(value = "customData", alternate = "custom_data")
    @ColumnInfo(name = Constant.CHANNEL_CUSTOM_DATA)
    protected HashMap<String, Object> customData;

    @ColumnInfo(name = Constant.CHANNEL_UNREAD_MESSAGES)
    private int unreadMessages = 0;

    @ColumnInfo(name = Constant.CHANNEL_FTS)
    private String channelFts;

    public Channel(String id, String name, String avatar, int type, Date updatedAt, Date createdAt,
                   String lastMessageId, HashMap<String, Object> customData, int unreadMessages) {
        super(createdAt, updatedAt);
        this.id = id;
        this.name = name;
        this.channelFts = this.name.toLowerCase() + " " + BlaChatTextUtils.convertToTextSearch(this.name);
        this.avatar = avatar;
        this.type = type;
        this.lastMessageId = lastMessageId;
        this.customData = customData;
        this.unreadMessages = unreadMessages;
    }

    public boolean isDirect() {
        return this.getType() == BlaChannelType.DIRECT.getValue();
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
        this.channelFts = this.name.toLowerCase() + " " + BlaChatTextUtils.convertToTextSearch(this.name);
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

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public void setCustomData(HashMap<String, Object> customData) {
        this.customData = customData;
    }

    public HashMap<String, Object> getCustomData() {
        return customData;
    }

    public List<Message> getLastMessages() {
        return lastMessages;
    }

    public void setLastMessages(List<Message> lastMessages) {
        this.lastMessages = lastMessages;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public String getChannelFts() {
        return channelFts;
    }

    public void setChannelFts(String channelFts) {
        this.channelFts = channelFts;
    }
}
