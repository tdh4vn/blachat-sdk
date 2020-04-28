package com.blameo.chatsdk.models.pojos;

import java.util.ArrayList;

public class MessageResult {
    private ArrayList<User> seenBy;
    private ArrayList<User> receivedBy;

    public ArrayList<User> getSeenBy() {
        if(seenBy == null)  seenBy = new ArrayList<>();
        return seenBy;
    }

    public void setSeenBy(ArrayList<User> seenBy) {
        this.seenBy = seenBy;
    }

    public ArrayList<User> getReceiveBy() {
        if(receivedBy == null)  receivedBy = new ArrayList<>();
        return receivedBy;
    }

    public void setReceiveBy(ArrayList<User> receivedBy) {
        this.receivedBy = receivedBy;
    }
}
