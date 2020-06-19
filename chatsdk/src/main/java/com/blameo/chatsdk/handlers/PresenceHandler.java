package com.blameo.chatsdk.handlers;

import com.blameo.chatsdk.blachat.BlaPresenceListener;
import com.blameo.chatsdk.repositories.UserRepository;

public interface PresenceHandler {

    void addListener(BlaPresenceListener blaPresenceListener);
    void removeListener(BlaPresenceListener blaPresenceListener);

    void startHandler();

}
