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
import com.blameo.chatsdk.utils.GsonDateFormatter;
import com.blameo.chatsdk.utils.GsonHashMapFormatter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.centrifugal.centrifuge.PublishEvent;
import io.github.centrifugal.centrifuge.Subscription;

public class EventHandlerImpl implements EventHandler {

    private static final String TAG = "EVENT" ;
    private Vector<BlaMessageListener> messageListeners = new Vector<>();

    private Vector<BlaChannelEventListener> channelEventListeners = new Vector<>();

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private ChannelController channelController;

    private MessageRepository messageRepository;

    private UserRepository userRepository;

    private String myId;

    private Gson gson;

    public EventHandlerImpl(String myId, Context context) {
        this.myId = myId;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateFormatter());
        gsonBuilder.registerTypeAdapter(HashMap.class, new GsonHashMapFormatter());
        gsonBuilder.setLenient();

        this.gson = gsonBuilder.create();
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

                Event p = gson.fromJson(data, Event.class);

                Log.i("EVENT", ""+p.getType() + " "+p.getPayload() + "\n" + data);

                switch (p.getType()) {
                    case "typing_event": {

                        JSONObject jsonObject = new JSONObject(data);
                        Payload typing = gson.fromJson(jsonObject.get("payload").toString(), Payload.class);

                        BlaChannel channel = channelController.getChannelById(typing.channel_id);
                        BlaUser user = userRepository.getUserById(typing.user_id);

                        if (channel != null && user != null) {
                            for(BlaChannelEventListener channelEventListener: channelEventListeners) {
                                channelEventListener.onTyping(channel, user, typing.is_typing ? BlaTypingEvent.START : BlaTypingEvent.STOP);
                            }
                        }
                        break;
                    }
                    case "new_message": {

                        JSONObject jsonObject = new JSONObject(data);

                        Message message = gson.fromJson(jsonObject.get("payload").toString(), Message.class);

                        if (!message.getAuthorId().equals(myId)) {
                            BlaMessage blaMessage = messageRepository.saveMessage(message);
                            for (BlaMessageListener listener: messageListeners) {
                                listener.onNewMessage(blaMessage);
                                channelController.updateLastMessageOfChannel(blaMessage.getChannelId(), blaMessage.getId());
                                messageRepository.sendReceiveEvent(message.getChannelId(), message.getId(), message.getAuthorId());
                            }
                        }
                        break;
                    }

                    case "mark_seen": {

                        JSONObject jsonObject = new JSONObject(data);
                        CursorEvent cursorEvent = gson.fromJson(jsonObject.get("payload").toString(), CursorEvent.class);
                        Log.i(TAG, "abc "+cursorEvent.actor_id + " "+cursorEvent.channel_id + " "+cursorEvent.message_id);
                        BlaChannel channel = channelController.getChannelById(cursorEvent.channel_id);
                        BlaMessage message = messageRepository.getMessageById(cursorEvent.message_id);
                        BlaUser user = userRepository.getUserById(cursorEvent.actor_id);

                        messageRepository.userSeenMyMessage(user.getId(), message.getId(), new Date(cursorEvent.time));

                        if (channel != null) {
                            for (BlaChannelEventListener listener: channelEventListeners) {
                                listener.onUserSeenMessage(channel, user, message);
                            }
                        }
                        break;
                    }

                    case "mark_receive": {
                        JSONObject jsonObject = new JSONObject(data);
                        CursorEvent cursorEvent = gson.fromJson(jsonObject.get("payload").toString(), CursorEvent.class);
                        Log.i(TAG, "rec "+cursorEvent.actor_id + " "+cursorEvent.channel_id + " "+cursorEvent.message_id);
                        BlaChannel channel = channelController.getChannelById(cursorEvent.channel_id);
                        BlaMessage message = messageRepository.getMessageById(cursorEvent.message_id);
                        BlaUser user = userRepository.getUserById(cursorEvent.actor_id);

                        messageRepository.userReceiveMyMessage(user.getId(), message.getId(), new Date(cursorEvent.time));

                        if (channel != null) {
                            for (BlaChannelEventListener listener: channelEventListeners) {
                                ArrayList<BlaUser> users = new ArrayList<>();
                                users.add(user);
                                message.setReceivedBy(users);
                                listener.onUserReceiveMessage(channel, user, message);
                            }
                        }
                        break;
                    }

                    case "new_channel": {

                        JSONObject jsonObject = new JSONObject(data);
                        Channel channel = gson.fromJson(jsonObject.get("payload").toString(), Channel.class);

                        if (channel != null) {
                            if(channelController.checkChannelIsExist(channel.getId()))    return;
                            channelController.onNewChannel(channel);
                            for (BlaChannelEventListener listener: channelEventListeners) {
                                listener.onNewChannel(new BlaChannel(channel));
                            }
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}
