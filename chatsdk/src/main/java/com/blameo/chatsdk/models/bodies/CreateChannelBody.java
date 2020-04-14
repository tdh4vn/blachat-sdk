package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CreateChannelBody {

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
    }

    @SerializedName("userIds")
    private ArrayList<String> userIds;
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
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

    @SerializedName("type")
    private int type;

    public CreateChannelBody(ArrayList<String> userIds, String name, int type) {
        this.userIds = userIds;
        this.name = name;
        this.type = type;
    }
}
