package com.blameo.chatsdk.models.bla;

public enum BlaTypingEvent {
    START(1), STOP(2);

    private int id;
    BlaTypingEvent(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }
}
