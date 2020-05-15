package com.blameo.chatsdk.blachat;

import com.blameo.chatsdk.models.bla.BlaUser;

public interface BlaPresenceListener {
    void onUpdate(BlaUser user);
}
