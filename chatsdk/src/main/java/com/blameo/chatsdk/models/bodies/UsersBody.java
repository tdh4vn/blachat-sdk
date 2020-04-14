package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UsersBody {

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    @SerializedName("ids")
    private ArrayList<String> ids;

    public UsersBody(ArrayList<String> ids) {
        this.ids = ids;
    }
}
