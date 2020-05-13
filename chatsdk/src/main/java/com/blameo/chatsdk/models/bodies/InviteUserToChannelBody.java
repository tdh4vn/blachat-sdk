package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class InviteUserToChannelBody {

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    @SerializedName("userIds")
    private List<String> userIds;

    public InviteUserToChannelBody(List<String> userIds) {
        this.userIds = userIds;
    }
}
