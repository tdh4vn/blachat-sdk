package com.blameo.chatsdk.handlers;

import com.blameo.chatsdk.blachat.BlaChannelEventListener;
import com.blameo.chatsdk.blachat.BlaMessageListener;
import com.blameo.chatsdk.models.events.Event;

import io.github.centrifugal.centrifuge.PublishEvent;
import io.github.centrifugal.centrifuge.Subscription;

public interface EventHandler {

    void addMessageListener(BlaMessageListener blaMessageListener);

    void addEventChannelListener(BlaChannelEventListener blaChannelEventListener);

    void removeMessageListener(BlaMessageListener blaMessageListener);

    void removeEventChannelListener(BlaChannelEventListener blaChannelEventListener);

    void onPublish(Subscription sub, PublishEvent event);

}
