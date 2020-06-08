package com.blameo.chatsdk.blachat;

import com.blameo.chatsdk.models.bla.BlaUserPresence;

public interface BlaPresenceListener {
    void onUpdate(BlaUserPresence user);
}
