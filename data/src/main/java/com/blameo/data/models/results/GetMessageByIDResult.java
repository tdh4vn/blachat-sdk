package com.blameo.data.models.results;

import com.blameo.data.models.pojos.Message;
import com.google.gson.annotations.SerializedName;

public class GetMessageByIDResult {

    @SerializedName("data")
    private Message data;

    public Message getMessage() {
        return data;
    }

    public void setMessage(Message data) {
        this.data = data;
    }

}
