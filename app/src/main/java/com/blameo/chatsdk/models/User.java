package com.blameo.chatsdk.models;

import com.stfalcon.chatkit.commons.models.IUser;

public class User implements IUser {

    private com.blameo.chatsdk.models.pojos.User user;

    public User(com.blameo.chatsdk.models.pojos.User user){
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
