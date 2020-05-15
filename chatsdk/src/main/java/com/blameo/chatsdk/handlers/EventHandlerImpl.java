package com.blameo.chatsdk.handlers;

import android.content.Context;
import android.util.Log;

import com.blameo.chatsdk.blachat.BlaChannelEventListener;
import com.blameo.chatsdk.blachat.BlaMessageListener;
import com.blameo.chatsdk.controllers.ChannelController;
import com.blameo.chatsdk.controllers.ChannelControllerImpl;
import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaTypingEvent;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.entities.Channel;
import com.blameo.chatsdk.models.entities.Message;
import com.blameo.chatsdk.models.events.CursorEvent;
import com.blameo.chatsdk.models.events.Event;
import com.blameo.chatsdk.models.events.Payload;
import com.blameo.chatsdk.repositories.MessageRepository;
import com.blameo.chatsdk.repositories.MessageRepositoryImpl;
import com.blameo.chatsdk.repositories.UserRepository;
import com.blameo.chatsdk.repositories.UserRepositoryImpl;
import com.blameo.chatsdk.utils.ChatSdkDateFormatUtil;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.centrifugal.centrifuge.PublishEvent;
import io.github.centrifugal.centrifuge.Subscription;

public class EventHandlerImpl implements EventHandler {

    private Vector<BlaMessageListener> messageListeners = new Vector<>();

    private Vector<BlaChannelEventListener> channelEventListeners = new Vector<>();

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private ChannelController channelController;

    private MessageRepository messageRepository;

    private UserRepository userRepository;

    private String myId;

    private Gson gson = new Gson();

    public EventHandlerImpl(String myId, Context context) {
        this.myId = myId;
        this.channelController = new ChannelControllerImpl(context, myId);
        this.messageRepository = new MessageRepositoryImpl(context);
        this.userRepository = new UserRepositoryImpl(context, myId);
    }

    @Override
    public void addMessageListener(BlaMessageListener blaMessageListener) {
        messageListeners.add(blaMessageListener);
    }

    @Override
    public void addEventChannelListener(BlaChannelEventListener blaChannelEventListener) {
        channelEventListeners.add(blaChannelEventListener);
    }

    @Override
    public void removeMessageListener(BlaMessageListener blaMessageListener) {
        messageListeners.remove(blaMessageListener);
    }

    @Override
    public void removeEventChannelListener(BlaChannelEventListener blaChannelEventListener) {
        channelEventListeners.remove(blaChannelEventListener);
    }

    @Override
    public void onPublish(Subscription sub, PublishEvent event) {
        executorService.submit(() -> {
            try {
                String data = new String(event.getData(), StandardCharsets.UTF_8);
                Log.i("BLACHAT", "Message from " + sub.getChannel() + ": " + data);

                Event p = gson.fromJson(data, Event.class);
                switch (p.getType()) {
                    case "typing_event": {
                        Payload typing = gson.fromJson(p.getPayload(), Payload.class);

                        BlaChannel channel = channelController.getChannelById(typing.channel_id);
                        BlaUser user = userRepository.getUserById(typing.user_id);

                        if (channel != null && user != null) {
                            for(BlaChannelEventListener channelEventListener: channelEventListeners) {
                                channelEventListener.onTyping(channel, user, typing.is_typing ? BlaTypingEvent.START : BlaTypingEvent.STOP);
                            }
                        }
                    }
                    case "new_message": {
                        Message message = gson.fromJson(p.getPayload(), Message.class);
                        if (message.getAuthorId().equals(myId)) {
                            BlaMessage blaMessage = messageRepository.saveMessage(message);
                            for (BlaMessageListener listener: messageListeners) {
                                listener.onNewMessage(blaMessage);
                            }
                        }
                    }

                    case "mark_seen": {
                        CursorEvent cursorEvent = gson.fromJson(p.getPayload(), CursorEvent.class);
                        BlaChannel channel = channelController.getChannelById(cursorEvent.channel_id);
                        BlaMessage message = messageRepository.getMessageById(cursorEvent.message_id);
                        BlaUser user = userRepository.getUserById(cursorEvent.actor_id);

                        messageRepository.userSeenMyMessage(user.getId(), message.getId(), ChatSdkDateFormatUtil.parse(cursorEvent.time));

                        if (channel != null) {
                            for (BlaChannelEventListener listener: channelEventListeners) {
                                listener.onUserSeenMessage(channel, user, message);
                            }
                        }
                    }

                    case "mark_receive": {
                        CursorEvent cursorEvent = gson.fromJson(p.getPayload(), CursorEvent.class);
                        BlaChannel channel = channelController.getChannelById(cursorEvent.channel_id);
                        BlaMessage message = messageRepository.getMessageById(cursorEvent.message_id);
                        BlaUser user = userRepository.getUserById(cursorEvent.actor_id);

                        messageRepository.userReceiveMyMessage(user.getId(), message.getId(), ChatSdkDateFormatUtil.parse(cursorEvent.time));

                        if (channel != null) {
                            for (BlaChannelEventListener listener: channelEventListeners) {
                                listener.onUserReceiveMessage(channel, user, message);
                            }
                        }
                    }

                    case "new_channel": {
                        Channel channel = gson.fromJson(p.getPayload(), Channel.class);

                        channelController.onNewChannel(channel);

                        if (channel != null) {
                            for (BlaChannelEventListener listener: channelEventListeners) {
                                listener.onNewChannel(new BlaChannel(channel));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}
