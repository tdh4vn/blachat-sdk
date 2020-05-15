package com.blameo.chatsdk.models.bla;

public enum BlaPresenceState {
    ONLINE(1), OFFLINE(2);

    private int status;

    BlaPresenceState(int status){
        this.status = status;
    }

    public int getStatus(){
        return status;
    }
}
