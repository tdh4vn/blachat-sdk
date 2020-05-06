package com.blameo.chatsdk.models.pojos;

import android.database.Cursor;

import com.blameo.chatsdk.utils.ChatSdkDateFormatUtil;
import com.blameo.chatsdk.utils.GsonUtil;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

public class User {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name = "";

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;

    private Date lastActiveAt;

    @SerializedName("custom_data")
    private String customData;

    public User(String id, String name, String avatar, Date createdAt, Date updatedAt, Date lastActiveAt, String customData) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastActiveAt = lastActiveAt;
        this.customData = customData;
    }

    public User() {
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

    public Date getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(Date lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public HashMap<String, Object> getCustomData() {
        return GsonUtil.jsonToMap(customData);
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }
}
