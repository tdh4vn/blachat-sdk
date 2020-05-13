package com.blameo.chatsdk.models.entities;

import com.google.gson.annotations.SerializedName;

public class Me {
    @SerializedName("token")
    private String token;

    @SerializedName("data")
    private User user;

    public Me(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public Me() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
