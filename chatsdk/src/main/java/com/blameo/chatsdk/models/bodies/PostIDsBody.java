package com.blameo.chatsdk.models.bodies;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PostIDsBody {

    @SerializedName("ids")
    private String ids;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        if (ids != null && ids.length() > 0 && ids.charAt(ids.length() - 1) == ',') {
            ids = ids.substring(0, ids.length() - 1);
        }
        this.ids = ids;
    }
}
