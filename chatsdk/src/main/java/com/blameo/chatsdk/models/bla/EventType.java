package com.blameo.chatsdk.models.bla;

public enum EventType {
    START(1), STOP(2);

    private int id;
    EventType(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }
}
