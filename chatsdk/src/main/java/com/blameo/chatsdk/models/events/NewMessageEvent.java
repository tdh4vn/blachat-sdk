package com.blameo.chatsdk.models.events;

import com.blameo.chatsdk.models.pojos.Message;

public class NewMessageEvent {
    private String type;
    private Message payload;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Message getPayload() {
        return payload;
    }

    public void setPayload(Message payload) {
        this.payload = payload;
    }


}
