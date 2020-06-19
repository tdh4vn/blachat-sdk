package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CreateChannelBody {

    @SerializedName("userIds")
    private List<String> userIds;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private int type;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("custom_data")
    private String customData;

    public CreateChannelBody() {
    }

    public CreateChannelBody(List<String> userIds, String name, int type, String avatar, String customData) {
        this.userIds = userIds;
        this.name = name;
        this.type = type;
        this.avatar = avatar;
        this.customData = customData;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }



}
