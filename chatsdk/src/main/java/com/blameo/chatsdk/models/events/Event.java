package com.blameo.chatsdk.models.events;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class Event {

    private String type;

    @SerializedName("event_id")
    private String eventId;

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


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
