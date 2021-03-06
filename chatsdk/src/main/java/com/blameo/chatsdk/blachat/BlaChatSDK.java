package com.blameo.chatsdk.blachat;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.blameo.chatsdk.controllers.ChannelController;
import com.blameo.chatsdk.controllers.ChannelControllerImpl;
import com.blameo.chatsdk.controllers.MessageController;
import com.blameo.chatsdk.controllers.MessageControllerImpl;
import com.blameo.chatsdk.handlers.EventHandler;
import com.blameo.chatsdk.handlers.EventHandlerImpl;
import com.blameo.chatsdk.handlers.PresenceHandler;
import com.blameo.chatsdk.handlers.PresenceHandlerImpl;
import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaMessageType;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.entities.User;
import com.blameo.chatsdk.models.entities.UserReactMessage;
import com.blameo.chatsdk.repositories.ChannelRepository;
import com.blameo.chatsdk.repositories.ChannelRepositoryImpl;
import com.blameo.chatsdk.repositories.MessageRepository;
import com.blameo.chatsdk.repositories.MessageRepositoryImpl;
import com.blameo.chatsdk.repositories.UserRepository;
import com.blameo.chatsdk.repositories.UserRepositoryImpl;
import com.blameo.chatsdk.repositories.remote.api.APIProvider;
import com.blameo.chatsdk.utils.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.centrifugal.centrifuge.Client;
import io.github.centrifugal.centrifuge.ConnectEvent;
import io.github.centrifugal.centrifuge.DisconnectEvent;
import io.github.centrifugal.centrifuge.DuplicateSubscriptionException;
import io.github.centrifugal.centrifuge.ErrorEvent;
import io.github.centrifugal.centrifuge.EventListener;
import io.github.centrifugal.centrifuge.JoinEvent;
import io.github.centrifugal.centrifuge.LeaveEvent;
import io.github.centrifugal.centrifuge.MessageEvent;
import io.github.centrifugal.centrifuge.Options;
import io.github.centrifugal.centrifuge.PrivateSubEvent;
import io.github.centrifugal.centrifuge.PublishEvent;
import io.github.centrifugal.centrifuge.RefreshEvent;
import io.github.centrifugal.centrifuge.SubscribeErrorEvent;
import io.github.centrifugal.centrifuge.SubscribeSuccessEvent;
import io.github.centrifugal.centrifuge.Subscription;
import io.github.centrifugal.centrifuge.SubscriptionEventListener;
import io.github.centrifugal.centrifuge.TokenCallback;
import io.github.centrifugal.centrifuge.UnsubscribeEvent;

public class BlaChatSDK implements BlaChatSDKProxy {

    private static BlaChatSDK instance = null;

    private static final int DEFAULT_THREAD_POOL_SIZE = 4;
    private static String DEFAULT_HOST = "159.65.2.104";
    private static String BASE_API_URL = "http://" + DEFAULT_HOST;
    private static String CENTRI_URL = "ws://" + DEFAULT_HOST + ":8001/connection/websocket?format=protobuf";

    private String myChannel;
    private String token;
    private String id;
    private Context applicationContext;
    private String TAG = "SDK";

    private PresenceHandler presenceHandler;

    private EventHandler eventHandler;

    private ChannelController channelController;

    private MessageController messageController;

    private MessageRepository messageRepository;

    private UserRepository userRepository;

    private ChannelRepository channelRepository;

    private ExecutorService executors = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

    private BlaChatSDK() {
    }

    public static BlaChatSDK getInstance() {
        //TODO: thread safe
        if (instance == null) {
            instance = new BlaChatSDK();
        }
        return instance;
    }

    @Override
    public void initBlaChatSDK(Context context, String userId, String token) {
        this.token = token;
        this.id = userId;
        this.myChannel = "chat#" + userId;
        this.applicationContext = context;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        APIProvider.INSTANCE.setSession(BASE_API_URL, token);

        this.messageRepository = MessageRepositoryImpl.getInstance(context, userId);
        this.userRepository = UserRepositoryImpl.getInstance(context, userId);
        this.channelRepository = ChannelRepositoryImpl.getInstance(context, userId);
        this.channelController = new ChannelControllerImpl();
        this.messageController = new MessageControllerImpl();

        eventHandler = new EventHandlerImpl(id, context);

        this.presenceHandler = new PresenceHandlerImpl(userRepository);

        this.presenceHandler.startHandler();

        realtimeDateInit();

        syncUnsentMessages();

        getEvent();

        getChannels(null, 100, null);

//        fetchAllUsers();
    }

    private void getEvent() {

        try {
            executors.submit(() ->{
                eventHandler.getEvent();
            });
        }catch (Exception e){}
    }

    private void syncUnsentMessages() {

        try {
            executors.submit(() -> {
                try {
                    messageRepository.syncUnSentMessages();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).get();
        } catch (Exception e) {}
    }

    private void realtimeDateInit() {

        EventListener listener = new EventListener() {
            @Override
            public void onConnect(Client client, ConnectEvent event) {
                super.onConnect(client, event);
            }

            @Override
            public void onDisconnect(Client client, DisconnectEvent event) {
                super.onDisconnect(client, event);
            }

            @Override
            public void onMessage(Client client, MessageEvent event) {
                super.onMessage(client, event);
            }

            @Override
            public void onRefresh(Client client, RefreshEvent event, TokenCallback cb) {
                super.onRefresh(client, event, cb);
            }

            @Override
            public void onError(Client client, ErrorEvent event) {
                super.onError(client, event);
            }

            @Override
            public void onPrivateSub(Client client, PrivateSubEvent event, TokenCallback cb) {
                super.onPrivateSub(client, event, cb);
            }
        };

        Client centrifugoClient = new Client(CENTRI_URL, new Options(), listener);
        centrifugoClient.setToken(token);


        SubscriptionEventListener subListener = new SubscriptionEventListener() {
            public void onPublish(Subscription sub, PublishEvent event) {
                Log.i(TAG, "event " + event.toString());
                eventHandler.onPublish(sub, event);
            }

            ;

            public void onJoin(Subscription sub, JoinEvent event) {
                Log.i(TAG, "on join " + sub.getChannel());
            }

            ;

            public void onLeave(Subscription sub, LeaveEvent event) {
                Log.i(TAG, "on leave");
            }

            ;

            public void onSubscribeSuccess(Subscription sub, SubscribeSuccessEvent event) {
                Log.i(TAG, "subscribe success");
            }

            ;

            public void onSubscribeError(Subscription sub, SubscribeErrorEvent event) {
                Log.i(TAG, "subscribe error " + event.getMessage());
            }

            ;

            public void onUnsubscribe(Subscription sub, UnsubscribeEvent event) {

            }

            ;
        };

        Subscription sub;
        try {
            Log.i(TAG, "sub channel id: " + myChannel);
            sub = centrifugoClient.newSubscription(myChannel, subListener);
        } catch (DuplicateSubscriptionException e) {
            e.printStackTrace();
            return;
        }
        sub.subscribe();

        centrifugoClient.connect();

    }

    @Override
    public void addMessageListener(MessagesListener messagesListener) {
        this.eventHandler.addMessageListener(messagesListener);
    }

    @Override
    public void removeMessageListener(MessagesListener messagesListener) {
        this.eventHandler.removeMessageListener(messagesListener);
    }

    @Override
    public void addChannelListener(ChannelEventListener channelEventListener) {
        this.eventHandler.addEventChannelListener(channelEventListener);
    }

    @Override
    public void removeChannelListener(ChannelEventListener channelEventListener) {
        this.eventHandler.removeEventChannelListener(channelEventListener);
    }

    @Override
    public void addPresenceListener(BlaPresenceListener blaPresenceListener) {
        this.presenceHandler.addListener(blaPresenceListener);
    }

    @Override
    public void removePresenceListener(BlaPresenceListener blaPresenceListener) {
        this.presenceHandler.removeListener(blaPresenceListener);
    }

    @Override
    public void getChannels(String lastId, Integer limit,Callback<List<BlaChannel>> callback) {
        try {
            executors.submit(() -> {
                List<BlaChannel> channels = null;
                try {
                    channels = channelController.getChannels(lastId, limit);
                } catch (Exception e) {
                    if (callback != null) callback.onFail(e);
                    e.printStackTrace();
                }
                if (callback != null) callback.onSuccess(channels);
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
        }
    }

    @Override
    public void getUsersInChannel(String channelId, Callback<List<BlaUser>> callback) {
        try {
            executors.submit(() -> {
                List<BlaUser> users = null;
                try {
                    users = channelController.getUsersInChannel(channelId);
                } catch (Exception e) {
                    if (callback != null) callback.onFail(e);
                }
                if (callback != null) callback.onSuccess(users);
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
        }
    }

    @Override
    public void getUsers(ArrayList<String> userIds, Callback<List<BlaUser>> callback) throws Exception {
        executors.submit(() -> {
            try {
                List<BlaUser> users = userRepository.getUsersByIds(userIds);
                if (callback != null) callback.onSuccess(users);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).get();
    }

    @Override
    public void getMessages(String channelId, String lastId, Integer limit, Callback<List<BlaMessage>> callback) {
        try {
            executors.submit(() -> {
                try {
                    List<BlaMessage> messages;
                    messages = messageController.getMessages(channelId, lastId, limit);
                    BlaChannel channel = channelController.resetUnreadMessagesInChannel(channelId);
                    channelUpdated(channel);

                    if (callback != null) callback.onSuccess(messages);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).get();
        } catch (Exception e) {
            if (callback != null) callback.onFail(e);
        }
    }

    private void channelUpdated(BlaChannel channel) {
        eventHandler.onChannelUpdate(channel);
    }

    @Override
    public void createChannel(String name, String avatar, List<String> userIds, BlaChannelType channelType, Map<String, Object> customData, Callback<BlaChannel> callback) throws Exception {
        executors.submit(() -> {
            try {
                BlaChannel channel = channelController.createChannel(
                        name,
                        avatar,
                        userIds,
                        channelType,
                        customData
                );
                if (callback != null) callback.onSuccess(channel);
            } catch (Exception e) {
                if (callback != null) callback.onFail(e);
                e.printStackTrace();
            }
        }).get();
    }

    @Override
    public void updateChannel(BlaChannel newChannel, Callback<BlaChannel> callback) {
        try {
            executors.submit(() -> {
                try {
                    if (callback != null) callback.onSuccess(channelController.updateChannel(newChannel));
                } catch (IOException e) {
                    e.printStackTrace();
                    if (callback != null) callback.onFail(e);
                }
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }

    }

    @Override
    public void deleteChannel(BlaChannel blaChannel, Callback<BlaChannel> callback) {
        try {
            executors.submit(() -> {
                try {
                    boolean res = channelController.deleteChannel(blaChannel.getId());
                    if(res)
                        if (callback != null) callback.onSuccess(blaChannel);
                } catch (IOException e) {
                    if (callback != null) callback.onFail(e);
                    e.printStackTrace();
                }
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }
    }

    @Override
    public void sendStartTyping(String channelID, Callback<Boolean> callback) {
        try {
            executors.submit(() -> {
                try {
                    channelController.sendStartTypingEvent(channelID);
                    if (callback != null) callback.onSuccess(true);
                } catch (Exception e) {
                    if (callback != null) callback.onFail(e);
                    e.printStackTrace();
                }
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }
    }

    @Override
    public void sendStopTyping(String channelID, Callback<Boolean> callback) {

        try {
            executors.submit(() -> {
                try {
                    channelController.sendStopTypingEvent(channelID);
                    if (callback != null) callback.onSuccess(true);
                } catch (Exception e) {
                    if (callback != null) callback.onFail(e);
                    e.printStackTrace();
                }
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }
    }

    @Override
    public void markSeenMessage(String messageId, String channelId, Callback<Boolean> callback) {
        try {
            executors.submit(() -> {
                try {
                    messageController.markReactMessage(messageId, channelId, UserReactMessage.SEEN);
                    BlaChannel channel = channelController.resetUnreadMessagesInChannel(channelId);
                    channelUpdated(channel);
                    if (callback != null) callback.onSuccess(true);
                } catch (Exception e) {
                    if (callback != null) callback.onFail(e);
                    e.printStackTrace();
                }
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }
    }

    @Override
    public void markReceiveMessage(String messageId, String channelId, Callback<Boolean> callback) {
        try {
            executors.submit(() -> {
                try {
                    messageController.markReactMessage(messageId, channelId, UserReactMessage.RECEIVE);
                    if (callback != null) callback.onSuccess(true);
                } catch (Exception e) {
                    if (callback != null) callback.onFail(e);
                    e.printStackTrace();
                }
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }
    }

    @Override
    public void createMessage(String content, String channelID, BlaMessageType type, Map<String, Object> customData, Callback<BlaMessage> callback) {
        executors.submit(() -> {
            try {
                BlaMessage message = messageController.sendMessage(
                        content,
                        channelID,
                        type,
                        customData
                );
                String json = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create().toJson(message);
                Log.e("json", json);
                if (callback != null) callback.onSuccess(message);
                channelController.updateLastMessageOfChannel(message.getChannelId(), message.getId());
            } catch (Exception e) {
                if (callback != null) callback.onFail(e);
            }
        });
    }

    @Override
    public void updateMessage(BlaMessage updatedMessage, Callback<BlaMessage> callback) {
        executors.submit(() -> {
            try {
                BlaMessage message = messageRepository.updateMessage(updatedMessage);
                if (callback != null) callback.onSuccess(message);
            } catch (Exception e) {
                if (callback != null) callback.onFail(e);
                e.printStackTrace();
            }
        });
    }

    @Override
    public void deleteMessage(BlaMessage deletedMessage, Callback<BlaMessage> callback) {
        executors.submit(() -> {
            try {
                BlaMessage message = messageRepository.deleteMessage(deletedMessage);
                if (callback != null) callback.onSuccess(message);
            } catch (Exception e) {
                if (callback != null)  callback.onFail(e);
                e.printStackTrace();
            }
        });
    }

    @Override
    public void inviteUserToChannel(List<String> usersID, String channelId, Callback<Boolean> callback) {
        executors.submit(() -> {
            try {
                channelController.inviteUsersToChannel(channelId, usersID);
                if (callback != null) callback.onSuccess(true);
            } catch (Exception e) {
                if (callback != null) callback.onFail(e);
                e.printStackTrace();
            }
        });

    }

    @Override
    public void removeUserFromChannel(String userID, String channelId, Callback<Boolean> callback) {
        executors.submit(() -> {
            try {
                channelController.removeUserFromChannel(userID, channelId);
                if (callback != null) callback.onSuccess(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void getUserPresence(Callback<List<BlaUser>> callback) {
        executors.submit(() -> {
            try {
                if (callback != null) callback.onSuccess(userRepository.getUsersPresence());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void getAllUsers(Callback<List<BlaUser>> callback) {
        try {
            executors.submit(() -> {
                try {
                    List<BlaUser> users = userRepository.getAllUsers();

                    if (callback != null) callback.onSuccess(users);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
