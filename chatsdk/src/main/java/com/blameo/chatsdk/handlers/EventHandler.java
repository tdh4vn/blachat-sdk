package com.blameo.chatsdk.handlers;

import com.blameo.chatsdk.blachat.ChannelEventListener;
import com.blameo.chatsdk.blachat.MessagesListener;
import com.blameo.chatsdk.models.bla.BlaChannel;

import java.util.Vector;

import io.github.centrifugal.centrifuge.PublishEvent;
import io.github.centrifugal.centrifuge.Subscription;

public interface EventHandler {

    void addMessageListener(MessagesListener messagesListener);

    void addEventChannelListener(ChannelEventListener channelEventListener);

    void removeMessageListener(MessagesListener messagesListener);

    void removeEventChannelListener(ChannelEventListener channelEventListener);

    void clearAllListener();

    void onPublish(Subscription sub, PublishEvent event);

    void publishEvent(String data);

    void getEvent();

    void onChannelUpdate(BlaChannel channel);

    Vector<ChannelEventListener> getChannelEventListeners();

}
