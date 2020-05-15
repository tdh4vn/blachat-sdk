package com.blameo.chatsdk.models.bla;

public enum BlaMessageType {
    TEXT(1), IMAGE(2);

    private int type;
    BlaMessageType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
