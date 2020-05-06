package com.blameo.chatsdk.blachat;

import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaTypingEvent;
import com.blameo.chatsdk.models.bla.BlaUser;

public interface BlaChannelEventListener {
    void onNewChannel(BlaChannel blaChannel);
    void onUpdateChannel(BlaChannel blaChannel);
    void onDeleteChannel(BlaChannel blaChannel);
    void onTyping(BlaChannel blaChannel, BlaUser blaUser, BlaTypingEvent blaTypingEvent);
    void onMemberJoin(BlaChannel blaChannel, BlaUser blaUser);
    void onMemberLeave(BlaChannel blaChannel, BlaUser blaUser);
}
