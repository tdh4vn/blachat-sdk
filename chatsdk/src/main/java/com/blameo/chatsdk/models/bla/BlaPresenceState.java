package com.blameo.chatsdk.models.bla;

public enum BlaPresenceState {
    ONLINE(2), OFFLINE(1);

    private int status;

    BlaPresenceState(int status){
        this.status = status;
    }

    public int getStatus(){
        return status;
    }
}
