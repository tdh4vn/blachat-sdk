package com.blameo.chatsdk.models.bla;

public enum BlaMessageType {
    TEXT(0), IMAGE(1);

    private int type;
    BlaMessageType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
