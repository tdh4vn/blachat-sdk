package com.blameo.chatsdk.models.bla;

public class BlaUserPresence {
    private BlaUser blaUser;
    private BlaPresenceState state;

    public BlaUserPresence(BlaUser blaUser, BlaPresenceState state) {
        this.blaUser = blaUser;
        this.state = state;
    }

    public BlaUser getBlaUser() {
        return blaUser;
    }

    public void setBlaUser(BlaUser blaUser) {
        this.blaUser = blaUser;
    }

    public BlaPresenceState getState() {
        return state;
    }

    public void setState(BlaPresenceState state) {
        this.state = state;
    }
}
