package com.blameo.chatsdk.models.bla;

import com.blameo.chatsdk.models.entities.User;
import com.blameo.chatsdk.repositories.local.Converters;

import java.util.Date;
import java.util.HashMap;

public class BlaUser extends User {

    private boolean online;

    private Date lastActiveAt;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public BlaUser(boolean online) {
        this.online = online;
    }

    public BlaUser(String id, String name, String avatar, HashMap<String, Object> customData) {
        super(id, name, avatar, customData);
    }

    public BlaUser(User user) {
        super(user.getId(),
                user.getName(),
                user.getAvatar(),
                user.getCustomData());
    }

    public Date getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(Date lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }
}
