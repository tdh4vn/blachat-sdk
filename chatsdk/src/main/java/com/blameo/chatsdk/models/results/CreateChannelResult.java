package com.blameo.chatsdk.models.results;

import com.blameo.chatsdk.models.entities.Channel;
import com.google.gson.annotations.SerializedName;

public class CreateChannelResult extends BaseResult {

    @SerializedName("data")
    private Channel data;

    public Channel getData() {
        return data;
    }

    public void setData(Channel data) {
        this.data = data;
    }

}
