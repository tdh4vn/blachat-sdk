package com.blameo.chatsdk.models.pojos;

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
