package com.blameo.chatsdk.models.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class InviteUserToChannelBody {

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
    }

    @SerializedName("userIds")
    private ArrayList<String> userIds;
}
