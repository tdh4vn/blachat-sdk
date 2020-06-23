package com.blameo.chatsdk.models.bla;

import com.blameo.chatsdk.models.entities.User;
import com.blameo.chatsdk.repositories.local.Converters;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.HashMap;

public class BlaUser extends User {

    private boolean online;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public BlaUser(boolean online) {
        this.online = online;
    }

    public BlaUser(String id, String name, String avatar, HashMap<String, Object> customData, Date lastActiveAt) {
        super(id, name, avatar, customData, lastActiveAt);
    }

    public BlaUser(User user) {
        super(user.getId(),
                user.getName(),
                user.getAvatar(),
                user.getCustomData(),
                user.getLastActiveAt());
    }


}
