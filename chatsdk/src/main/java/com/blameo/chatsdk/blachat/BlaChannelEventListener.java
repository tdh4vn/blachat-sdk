package com.blameo.chatsdk.blachat;

import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaTypingEvent;
import com.blameo.chatsdk.models.bla.BlaUser;

public interface BlaChannelEventListener {
    void onNewChannel(BlaChannel channel);
    void onUpdateChannel(BlaChannel channel);
    void onDeleteChannel(BlaChannel channel);
    void onUserSeenMessage(BlaChannel channel, BlaUser user, BlaMessage message);
    void onUserReceiveMessage(BlaChannel channel, BlaUser user, BlaMessage message);
    void onTyping(BlaChannel channel, BlaUser blaUser, BlaTypingEvent blaTypingEvent);
    void onMemberJoin(BlaChannel channel, BlaUser blaUser);
    void onMemberLeave(BlaChannel channel, BlaUser blaUser);
}
