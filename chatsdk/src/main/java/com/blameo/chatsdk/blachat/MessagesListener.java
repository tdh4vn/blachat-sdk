package com.blameo.chatsdk.blachat;

import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaUser;

import java.util.Date;

public interface MessagesListener {
    void onNewMessage(BlaMessage blaMessage);
    void onUpdateMessage(BlaMessage blaMessage);
    void onDeleteMessage(BlaMessage blaMessage);
    void onUserSeen(BlaMessage blaMessage, BlaUser blaUser, Date seenAt);
    void onUserReceive(BlaMessage blaMessage, BlaUser blaUser,Date receivedAt);
}
