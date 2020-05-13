package com.blameo.chatsdk.models.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.blameo.chatsdk.repositories.local.Constant;
import com.blameo.chatsdk.utils.GsonUtil;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.HashMap;


@Entity(tableName = Constant.USER_TABLE_NAME)
public class User {

    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = Constant.USER_ID)
    private String id;

    @SerializedName("name")
    @ColumnInfo(name = Constant.USER_NAME)
    private String name = "";

    @SerializedName("avatar")
    @ColumnInfo(name = Constant.USER_AVATAR)
    private String avatar;

    @SerializedName("custom_data")
    @ColumnInfo(name = Constant.USER_CUSTOM_DATA)
    private HashMap<String, Object> customData;

    public User() {
    }

    public User(String id, String name, String avatar, HashMap<String, Object> customData) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
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

    public HashMap<String, Object> getCustomData() {
        return customData;
    }

    public void setCustomData(HashMap<String, Object> customData) {
        this.customData = customData;
    }
}
