package com.blameo.chatsdk.models.events;

public class StatusMessageEvent {
    private String type;
    private CursorEvent payload;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CursorEvent getPayload() {
        return payload;
    }

    public void setPayload(CursorEvent payload) {
        this.payload = payload;
    }


}
