package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateChannelBody {

    @SerializedName("name")
    private String name;

    @SerializedName("avatar")
    private String avatar;

    public UpdateChannelBody(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
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
}
