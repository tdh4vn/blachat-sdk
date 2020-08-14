package com.blameo.chatsdk.handlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.blameo.chatsdk.blachat.ChannelEventListener;
import com.blameo.chatsdk.blachat.MessagesListener;
import com.blameo.chatsdk.controllers.ChannelController;
import com.blameo.chatsdk.controllers.ChannelControllerImpl;
import com.blameo.chatsdk.controllers.MessageController;
import com.blameo.chatsdk.controllers.MessageControllerImpl;
import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.EventType;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.entities.Channel;
import com.blameo.chatsdk.models.entities.Message;
import com.blameo.chatsdk.models.entities.UserInChannel;
import com.blameo.chatsdk.models.entities.UserReactMessage;
import com.blameo.chatsdk.models.events.CursorEvent;
import com.blameo.chatsdk.models.events.DeleteChannelEvent;
import com.blameo.chatsdk.models.events.DeleteMessageEvent;
import com.blameo.chatsdk.models.events.Event;
import com.blameo.chatsdk.models.events.GetEvent;
import com.blameo.chatsdk.models.events.InviteUsersEvent;
import com.blameo.chatsdk.models.events.Payload;
import com.blameo.chatsdk.models.events.UserLeaveChannel;
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
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.centrifugal.centrifuge.PublishEvent;
import io.github.centrifugal.centrifuge.Subscription;
import retrofit2.Response;

public class EventHandlerImpl implements EventHandler {

    private static final String TAG = "EVENT";
    private Vector<MessagesListener> messageListeners = new Vector<>();

    private Vector<ChannelEventListener> channelEventListeners = new Vector<>();

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private ChannelController channelController;

    private MessageController messageController;

    private MessageRepository messageRepository;

    private UserRepository userRepository;

    private String myId;

    private Gson gson;

    private SharedPreferences sharedPreferences;

    private final String LAST_EVENT_ID = "LAST_EVENT_ID";

    public Vector<ChannelEventListener> getChannelEventListeners() {
        return channelEventListeners;
    }

    public EventHandlerImpl(String myId, Context context) {
        this.myId = myId;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateFormatter());
        gsonBuilder.registerTypeAdapter(HashMap.class, new GsonHashMapFormatter());
        gsonBuilder.setLenient();

        this.gson = gsonBuilder.create();
        this.channelController = new ChannelControllerImpl();
        this.messageRepository = MessageRepositoryImpl.getInstance();
        this.messageController = new MessageControllerImpl();
        this.userRepository = UserRepositoryImpl.getInstance();
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
    public void clearAllListener() {
        channelEventListeners.clear();
        messageListeners.clear();
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

            if (!TextUtils.isEmpty(p.getEventId()))
                sharedPreferences.edit().putString(LAST_EVENT_ID, p.getEventId()).apply();

            switch (p.getType()) {
                case "typing_event": {

                    JSONObject jsonObject = new JSONObject(data);
                    Payload typing = gson.fromJson(jsonObject.get("payload").toString(), Payload.class);

                    if (typing.user_id.equals(myId)) return;

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
                    Log.e("NEW_MESSAGE", "1");

                    Message message = gson.fromJson(jsonObject.get("payload").toString(), Message.class);

                    if (messageController.getMessageById(message.getId()) != null) {
                        Log.e("NEW_MESSAGE", "2");
                        break;
                    }

                    Log.e("NEW_MESSAGE", "5");
                    BlaMessage blaMessage = messageController.onNewMessage(message);

                    if (message.getAuthorId().equals(userRepository.getMyId())) {
                        Log.e("NEW_MESSAGE", "3");
                        channelController.updateLastSeen(message.getChannelId(), userRepository.getMyId(), new Date());
                        channelController.updateLastReceived(message.getChannelId(), userRepository.getMyId(), new Date());
                    } else {
                        Log.e("NEW_MESSAGE", "4");
                        messageController.markReactMessage(message.getId(), message.getChannelId(), UserReactMessage.RECEIVE);
                    }

                    if (blaMessage != null) {
                        Log.e("NEW_MESSAGE", "6");
                        channelController.updateLastMessageOfChannel(blaMessage.getChannelId(), blaMessage.getId());
                        Log.e("NEW_MESSAGE", "7");
                        BlaChannel blaChannel = channelController.getChannelById(message.getChannelId());
                        Log.e("NEW_MESSAGE", "8");
                        blaChannel.setLastMessage(blaMessage);
                        Log.e("NEW_MESSAGE", "9");
                        onChannelUpdate(blaChannel);

                        for (MessagesListener listener : messageListeners) {
                            listener.onNewMessage(blaMessage);
                        }
                    }
                    Log.e("NEW_MESSAGE", "10");
                    break;
                }

                case "mark_seen": {

                    JSONObject jsonObject = new JSONObject(data);

                    CursorEvent cursorEvent = gson.fromJson(jsonObject.get("payload").toString(), CursorEvent.class);

                    BlaChannel channel = channelController.getChannelById(cursorEvent.channel_id);

                    BlaMessage message = messageController.userReactMyMessage(cursorEvent.actor_id, cursorEvent.message_id, new Date(cursorEvent.time), UserReactMessage.SEEN);

                    BlaUser user = userRepository.getUserById(cursorEvent.actor_id);

                    if (channel != null && message != null) {
                        for (MessagesListener listener : messageListeners) {
                            listener.onUserSeen(message, user, new Date(cursorEvent.time));
                        }

                        for (ChannelEventListener listener : channelEventListeners) {
                            listener.onUserSeenMessage(channel, user, message);
                        }

                        onChannelUpdate(channel);
                    }


                    break;
                }

                case "mark_receive": {
                    JSONObject jsonObject = new JSONObject(data);
                    CursorEvent cursorEvent = gson.fromJson(jsonObject.get("payload").toString(), CursorEvent.class);
                    BlaChannel channel = channelController.getChannelById(cursorEvent.channel_id);

                    BlaMessage message = messageController.userReactMyMessage(cursorEvent.actor_id, cursorEvent.message_id, new Date(cursorEvent.time), UserReactMessage.RECEIVE);

                    BlaUser user = userRepository.getUserById(cursorEvent.actor_id);

                    if (channel != null && message != null) {
                        for (MessagesListener listener : messageListeners) {
                            listener.onUserReceive(message, user, new Date(cursorEvent.time));
                        }
                        for (ChannelEventListener listener : channelEventListeners) {
                            listener.onUserReceiveMessage(channel, user, message);
                        }

                        onChannelUpdate(channel);
                    }
                    break;
                }

                case "new_channel": {
                    JSONObject jsonObject = new JSONObject(data);
                    Channel channel = gson.fromJson(jsonObject.get("payload").toString(), Channel.class);

                    if (channel != null) {
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
                    channelController.usersAddedToChannel(inviteUsersEvent.channelId, inviteUsersEvent.userIds);
                    List<BlaUser> users = userRepository.getUsersByIds(inviteUsersEvent.userIds);
                    BlaChannel channel = channelController.getChannelById(inviteUsersEvent.channelId);
                    if (channel != null && users != null) {
                        onChannelUpdate(channel);
                    }

                    for (ChannelEventListener channelEventListener: channelEventListeners) {
                        for (BlaUser user: users) {
                            channelEventListener.onMemberJoin(channel, user);
                        }
                    }

                    break;
                }
                case "remove_user_from_channel": {
                    JSONObject jsonObject = new JSONObject(data);
                    UserLeaveChannel userLeaveChannelEvent = gson.fromJson(jsonObject.get("payload").toString(), UserLeaveChannel.class);

                    if (userRepository.getMyId().equals(userLeaveChannelEvent.userID)) {
                        BlaChannel channel = channelController.getChannelById(userLeaveChannelEvent.channelId);
                        channelController.deleteChannel(userLeaveChannelEvent.channelId);
                        for (ChannelEventListener channelEventListener: channelEventListeners) {
                            channelEventListener.onDeleteChannel(channel);
                        }
                    } else {
                        channelController.onUserLeaveChannel(userLeaveChannelEvent.channelId, userLeaveChannelEvent.userID);
                        BlaUser user = userRepository.getUserById(userLeaveChannelEvent.userID);
                        BlaChannel channel = channelController.getChannelById(userLeaveChannelEvent.channelId);

                        for (ChannelEventListener channelEventListener: channelEventListeners) {
                            channelEventListener.onMemberLeave(channel, user);
                        }
                    }
                }
                case "update_channel": {
                    JSONObject jsonObject = new JSONObject(data);
                    Channel channel = gson.fromJson(jsonObject.get("payload").toString(), Channel.class);
                    BlaChannel blaChannel = channelController.onChannelUpdate(channel);
                    onChannelUpdate(blaChannel);
                    break;
                }

                case "delete_message": {
                    JSONObject jsonObject = new JSONObject(data);
                    String messageId = gson.fromJson(jsonObject.get("payload").toString(), DeleteMessageEvent.class).messageId;
                    BlaMessage message = messageController.getMessageById(messageId);
                    if (message != null) {
                        messageController.onDeleteMessage(message);
                        for (MessagesListener listener : messageListeners) {
                            listener.onDeleteMessage(message);
                        }
                        BlaChannel channel = channelController.getChannelById(message.getChannelId());
                        if (channel != null) {
                            if (channel.getLastMessageId().equals(messageId)) {
                                List<BlaMessage> messages = messageController.getMessages(message.getChannelId(), null, 3);
                                if (messages != null && messages.size() > 1) {
                                    channel.setLastMessage(messages.get(0));
                                    channelController.updateLastMessageOfChannel(channel.getId(), messages.get(0).getId());
                                    onChannelUpdate(channel);
                                }
                            }
                        }
                    }
                }

                case "delete_channel": {
                    JSONObject jsonObject = new JSONObject(data);
                    String channelId = gson.fromJson(jsonObject.get("payload").toString(), DeleteChannelEvent.class).channelId;
                    BlaChannel channel = channelController.getChannelById(channelId);
                    if (channel != null) {
                        channelController.deleteChannel(channelId);
                        for (ChannelEventListener listener : channelEventListeners) {
                            listener.onDeleteChannel(channel);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getEvent() {
        String lastEventId = sharedPreferences.getString(LAST_EVENT_ID, "");
        Log.e(LAST_EVENT_ID, lastEventId);
        if (lastEventId.isEmpty()) {
            return;
        }

        try {
            Response<GetEventResult> response = APIProvider.INSTANCE.getBlaChatAPI()
                    .getEvent(lastEventId)
                    .execute();

            assert response.body() != null;
            if (response.isSuccessful() && response.body().getData() != null) {
                Log.e(LAST_EVENT_ID, "size " + response.body().getData().size());
                ArrayList<GetEvent> events = response.body().getData();
                if (response.body().getData().size() == 0) {
                    return;
                }
                sharedPreferences.edit().putString(LAST_EVENT_ID, response.body().getData().get(0).getId()).apply();
                for (int i = response.body().getData().size() - 1 ; i > 0; i--) {
                    publishEvent(events.get(i).getPayload());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChannelUpdate(BlaChannel channel) {
        for (ChannelEventListener listener : channelEventListeners) {
            listener.onUpdateChannel(channel);
        }
    }
}
