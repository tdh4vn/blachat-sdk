package com.blameo.chatsdk.models;

import com.stfalcon.chatkit.commons.models.IUser;

public class CustomUser implements IUser {

    private com.blameo.chatsdk.models.bla.BlaUser user;

    public CustomUser(com.blameo.chatsdk.models.bla.BlaUser user){
        this.user = user;
    }

    @Override
    public String getId() {
        if (user != null) return user.getId();
        return "";
    }

    @Override
    public String getName() {
        if (user != null) return user.getName();
        return "";
    }

    @Override
    public String getAvatar() {
        if (user != null) return user.getAvatar();
        return "";
    }
}
