package com.blameo.chatsdk.models.results;

import com.blameo.chatsdk.models.pojos.User;
import com.google.gson.annotations.SerializedName;

public class UserStatus {
    @SerializedName("ID")
    private String id;

    public UserStatus(String id, int status) {
        this.id = id;
        this.status = status;
    }

    @SerializedName("Status")
    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
