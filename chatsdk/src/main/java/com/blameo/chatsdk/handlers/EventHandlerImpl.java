package com.blameo.chatsdk.handlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.blameo.chatsdk.blachat.ChannelEventListener;
import com.blameo.chatsdk.blachat.MessagesListener;
import com.blameo.chatsdk.controllers.ChannelController;
import com.blameo.chatsdk.controllers.ChannelControllerImpl;
import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.EventType;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.entities.Channel;
import com.blameo.chatsdk.models.entities.Message;
import com.blameo.chatsdk.models.events.CursorEvent;
import com.blameo.chatsdk.models.events.Event;
import com.blameo.chatsdk.models.events.GetEvent;
import com.blameo.chatsdk.models.events.InviteUsersEvent;
import com.blameo.chatsdk.models.events.Payload;
import com.blameo.chatsdk.models.results.GetEventResult;
import com.blameo.chatsdk.repositories.MessageRepository;
import com.blameo.chatsdk.repositories.MessageRepositoryImpl;
import com.blameo.chatsdk.repositories.UserRepository;
import com.blameo.chatsdk.repositories.UserRepositoryImpl;
import com.blameo.chatsdk.repositories.remote.api.APIProvider;
import com.blameo.chatsdk.utils.GsonDateFormatter;
import com.blameo.chatsdk.utils.GsonHashMapFormatter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.centrifugal.centrifuge.PublishEvent;
import io.github.centrifugal.centrifuge.Subscription;
import retrofit2.Response;

public class EventHandlerImpl implements EventHandler {

    private static final String TAG = "EVENT" ;
    private Vector<MessagesListener> messageListeners = new Vector<>();

    private Vector<ChannelEventListener> channelEventListeners = new Vector<>();

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private ChannelController channelController;

    private MessageRepository messageRepository;

    private UserRepository userRepository;

    private String myId;

    private Gson gson;

    private SharedPreferences sharedPreferences;

    private final String LAST_EVENT_ID = "LAST_EVENT_ID";

//    public interface NewerMessageListener{
//        void onNewMessage(Message message);
//        void onNewChannel(Channel channel);
//    }

//    private NewerMessageListener newerMessageListener = message -> {
//
//    };

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
        sharedPreferences = context.getSharedPreferences("EVENT", Context.MODE_PRIVATE);
    }

    @Override
    public void addMessageListener(MessagesListener messagesListener) {
        messageListeners.add(messagesListener);
    }

    @Override
    public void addEventChannelListener(ChannelEventListener channelEventListener) {
        channelEventListeners.add(channelEventListener);
    }

    @Override
    public void removeMessageListener(MessagesListener messagesListener) {
        messageListeners.remove(messagesListener);
    }

    @Override
    public void removeEventChannelListener(ChannelEventListener channelEventListener) {
        channelEventListeners.remove(channelEventListener);
    }

    @Override
    public void onPublish(Subscription sub, PublishEvent event) {
        executorService.submit(() -> {
            try {
                String data = new String(event.getData(), StandardCharsets.UTF_8);
                publishEvent(data);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void publishEvent(String data) {
        try {
            Event p = gson.fromJson(data, Event.class);

            Log.i("EVENT", ""+p.getType() + " "+p.getPayload() + " "+ p.getEventId() + "\n" + data);

            if(!TextUtils.isEmpty(p.getEventId()))
                sharedPreferences.edit().putString(LAST_EVENT_ID, p.getEventId()).apply();

            switch (p.getType()) {
                case "typing_event": {

                    JSONObject jsonObject = new JSONObject(data);
                    Payload typing = gson.fromJson(jsonObject.get("payload").toString(), Payload.class);

                    BlaChannel channel = channelController.getChannelById(typing.channel_id);
                    BlaUser user = userRepository.getUserById(typing.user_id);

                    if (channel != null && user != null) {
                        for (ChannelEventListener channelEventListener : channelEventListeners) {
                            channelEventListener.onTyping(channel, user, typing.is_typing ? EventType.START : EventType.STOP);
                        }
                    }
                    break;
                }
                case "new_message": {

                    JSONObject jsonObject = new JSONObject(data);

                    Message message = gson.fromJson(jsonObject.get("payload").toString(), Message.class);

//                        if (!message.getAuthorId().equals(myId)) {
                    BlaMessage blaMessage = messageRepository.saveMessage(message);
                    for (MessagesListener listener : messageListeners) {
                        listener.onNewMessage(blaMessage);
                        channelController.updateLastMessageOfChannel(blaMessage.getChannelId(), blaMessage.getId());
                        messageRepository.sendReceiveEvent(message.getChannelId(), message.getId(), message.getAuthorId());
                    }
//                        }
                    break;
                }

                case "mark_seen": {

                    JSONObject jsonObject = new JSONObject(data);
                    CursorEvent cursorEvent = gson.fromJson(jsonObject.get("payload").toString(), CursorEvent.class);
                    Log.i(TAG, "abc " + cursorEvent.actor_id + " " + cursorEvent.channel_id + " " + cursorEvent.message_id);
                    BlaChannel channel = channelController.getChannelById(cursorEvent.channel_id);
                    BlaMessage message = messageRepository.getMessageById(cursorEvent.message_id);
                    BlaUser user = userRepository.getUserById(cursorEvent.actor_id);

                    messageRepository.userSeenMyMessage(user.getId(), message.getId(), new Date(cursorEvent.time));

                    if (channel != null) {
                        for (ChannelEventListener listener : channelEventListeners) {
                            listener.onUserSeenMessage(channel, user, message);
                        }
                    }
                    break;
                }

                case "mark_receive": {
                    JSONObject jsonObject = new JSONObject(data);
                    CursorEvent cursorEvent = gson.fromJson(jsonObject.get("payload").toString(), CursorEvent.class);
                    Log.i(TAG, "rec " + cursorEvent.actor_id + " " + cursorEvent.channel_id + " " + cursorEvent.message_id);
                    BlaChannel channel = channelController.getChannelById(cursorEvent.channel_id);
                    BlaMessage message = messageRepository.getMessageById(cursorEvent.message_id);
                    BlaUser user = userRepository.getUserById(cursorEvent.actor_id);

                    messageRepository.userReceiveMyMessage(user.getId(), message.getId(), new Date(cursorEvent.time));

                    if (channel != null) {
                        for (ChannelEventListener listener : channelEventListeners) {
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

                    if (channel != null){
                        if (channelController.checkChannelIsExist(channel.getId())) return;
                        channelController.onNewChannel(channel);
                        for (ChannelEventListener listener : channelEventListeners) {
                            listener.onNewChannel(new BlaChannel(channel));
                        }
                    }
                    break;
                }

                case "invite_user": {
                    JSONObject jsonObject = new JSONObject(data);
                    InviteUsersEvent inviteUsersEvent = gson.fromJson(jsonObject.get("payload").toString(), InviteUsersEvent.class);
                    Log.i(TAG, "invite users: " + inviteUsersEvent.channelId + " " + inviteUsersEvent.userIds.size());
                    channelController.usersAddedToChannel(inviteUsersEvent.channelId, inviteUsersEvent.userIds);
                    break;
                }
            }
        }catch (Exception e){}
    }

    @Override
    public void getEvent(){

        String lastEventId = sharedPreferences.getString(LAST_EVENT_ID, "");
        Log.i(TAG, "last_event_id "+lastEventId);

        if(lastEventId.isEmpty())   return;

        try {
            Response<GetEventResult> response = APIProvider.INSTANCE.getBlaChatAPI()
                    .getEvent(lastEventId)
                    .execute();

            assert response.body() != null;
            if(response.isSuccessful() && response.body().getData() != null){
                ArrayList<GetEvent> events = response.body().getData();
                Log.i(TAG, "get event: "+ events.size());
                if(response.body().getData().size() == 0)   return;
                sharedPreferences.edit().putString(LAST_EVENT_ID, "").apply();

                for(int i = events.size() - 1 ; i >= 0; i--){
                    publishEvent(events.get(i).getPayload());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
