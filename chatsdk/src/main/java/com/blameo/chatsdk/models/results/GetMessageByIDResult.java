package com.blameo.chatsdk.models.results;

import com.blameo.chatsdk.models.pojos.Message;
import com.google.gson.annotations.SerializedName;

public class GetMessageByIDResult extends BaseResult {

    @SerializedName("data")
    private Message data;

    public Message getMessage() {
        return data;
    }

    public void setMessage(Message data) {
        this.data = data;
    }

}
