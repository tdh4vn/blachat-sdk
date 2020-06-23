package com.blameo.chatsdk.blachat;

import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.EventType;
import com.blameo.chatsdk.models.bla.BlaUser;

public interface ChannelEventListener {
    void onNewChannel(BlaChannel channel);
    void onUpdateChannel(BlaChannel channel);
    void onDeleteChannel(BlaChannel channel);
    void onUserSeenMessage(BlaChannel channel, BlaUser user, BlaMessage message);
    void onUserReceiveMessage(BlaChannel channel, BlaUser user, BlaMessage message);
    void onTyping(BlaChannel channel, BlaUser blaUser, EventType eventType);
    void onMemberJoin(BlaChannel channel, BlaUser blaUser);
    void onMemberLeave(BlaChannel channel, BlaUser blaUser);
}
