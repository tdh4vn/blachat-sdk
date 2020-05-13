package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UsersBody {

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    @SerializedName("ids")
    private List<String> ids;

    public UsersBody(List<String> ids) {
        this.ids = ids;
    }

}
