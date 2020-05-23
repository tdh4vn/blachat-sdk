package com.blameo.chatsdk.models.events;

import org.json.JSONObject;

public class Event {
    private String type;
    private JSONObject payload;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONObject getPayload() {
        return payload;
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }


}
