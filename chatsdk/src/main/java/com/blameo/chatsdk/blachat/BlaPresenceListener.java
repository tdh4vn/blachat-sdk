package com.blameo.chatsdk.blachat;

import com.blameo.chatsdk.models.bla.BlaUser;

import java.util.List;

public interface BlaPresenceListener {
    void onUpdate(List<BlaUser> user);
}
