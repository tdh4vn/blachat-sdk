package com.blameo.chatsdk.models;

import com.stfalcon.chatkit.commons.models.IUser;

public class CustomUser implements IUser {

    private com.blameo.chatsdk.models.bla.BlaUser user;

    public CustomUser(com.blameo.chatsdk.models.bla.BlaUser user){
        this.user = user;
    }

    @Override
    public String getId() {
        return user.getId();
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public String getAvatar() {
        return user.getAvatar();
    }
}
