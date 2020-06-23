package com.blameo.chatsdk.models.events;

public class NewChannelEvent {
    private String type;
    private ChannelEvent payload;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ChannelEvent getPayload() {
        return payload;
    }

    public void setPayload(ChannelEvent payload) {
        this.payload = payload;
    }
}
