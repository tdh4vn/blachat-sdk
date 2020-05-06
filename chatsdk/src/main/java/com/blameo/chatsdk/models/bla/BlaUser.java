package com.blameo.chatsdk.models.bla;

import com.blameo.chatsdk.models.pojos.User;

public class BlaUser extends User {

    private boolean online;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
