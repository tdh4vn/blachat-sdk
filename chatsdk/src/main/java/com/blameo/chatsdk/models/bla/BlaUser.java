package com.blameo.chatsdk.models.bla;

import com.blameo.chatsdk.models.entities.User;

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

    public BlaUser(String id, String name, String avatar, HashMap<String, Object> customData) {
        super(id, name, avatar, customData);
    }


    public BlaUser(User user) {
        super(user.getId(),
                user.getAvatar(),
                user.getName(),
                user.getCustomData());
    }

}
