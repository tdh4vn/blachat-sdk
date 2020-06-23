package com.blameo.chatsdk.models.bla;

public enum BlaChannelType {
    GROUP(1), DIRECT(2);

    private int id;

    BlaChannelType(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }

}
