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

    @SerializedName("email")
    private String email = "";

    @SerializedName("role")
    private Role role = Role.User;

    @SerializedName("gender")
    private String gender = "";

    @SerializedName("connection_status")
    private String connectionStatus;

    @SerializedName("last_active_at")
    private Date lastActiveAt;

//    @SerializedName("custom_data")
//    private HashMap<String, Object> customData;

        @SerializedName("custom_data")
    private String customData;

    public boolean isCheck = false;

    public String dobString = "";

    public User(String id, String name, String avatar, String connection_status, Date last_active_at) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.connectionStatus = connection_status;
        this.lastActiveAt = last_active_at;
    }

    public User(Cursor cursor) throws ParseException {
        this.id = cursor.getString(0);
        this.name =        cursor.getString(1);
        this.avatar =        cursor.getString(2);
        this.connectionStatus = cursor.getString(3);
        this.lastActiveAt = ChatSdkDateFormatUtil.parse(cursor.getString(4));
//        this.customData = GsonUtil.jsonToMap(cursor.getString(5));
        this.customData = cursor.getString(5);
    }

    public User(String id, String name, String avatar, Date createdAt, Date updatedAt, String email, Role role, String gender, String connectionStatus, Date lastActiveAt, String customData) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.email = email;
        this.role = role;
        this.gender = gender;
        this.connectionStatus = connectionStatus;
        this.lastActiveAt = lastActiveAt;
        this.customData = customData;
    }

    public User() {
    }

    public User(String id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public Date getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(Date lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getDobString() {
        return dobString;
    }

    public void setDobString(String dobString) {
        this.dobString = dobString;
    }

    public String getCustomData() {
        return customData;
    }

    public String getLastActiveAtString() {
        return ChatSdkDateFormatUtil.parse(lastActiveAt);
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public String getCustomDataString() {
        return customData;
    }

    public enum Role {
        User,
        Sale
    }
}
