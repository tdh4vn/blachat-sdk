package com.blameo.chatsdk.models.bla;

public enum BlaPresenceType {
    OFFLINE(1), ONLINE(2);

    private int id;
    BlaPresenceType(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }
}
