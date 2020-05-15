package com.blameo.chatsdk.blachat;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.blameo.chatsdk.controllers.ChannelController;
import com.blameo.chatsdk.controllers.ChannelControllerImpl;
import com.blameo.chatsdk.handlers.EventHandler;
import com.blameo.chatsdk.handlers.EventHandlerImpl;
import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaMessageType;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.entities.Message;
import com.blameo.chatsdk.repositories.MessageRepository;
import com.blameo.chatsdk.repositories.MessageRepositoryImpl;
import com.blameo.chatsdk.repositories.UserRepository;
import com.blameo.chatsdk.repositories.UserRepositoryImpl;
import com.blameo.chatsdk.repositories.remote.api.APIProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.centrifugal.centrifuge.Client;
import io.github.centrifugal.centrifuge.ConnectEvent;
import io.github.centrifugal.centrifuge.DisconnectEvent;
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

    private EventHandler eventHandler;

    private ChannelController channelController;

    private MessageRepository messageRepository;

    private UserRepository userRepository;

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
    public void init(Context context, String userId, String token) {
        this.token = token;
        this.id = userId;
        this.myChannel = "chat#" + userId;
        this.applicationContext = context;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        APIProvider.INSTANCE.setSession(BASE_API_URL, token);

        this.channelController = new ChannelControllerImpl(context, userId);
        this.messageRepository = new MessageRepositoryImpl(context);
        this.userRepository = new UserRepositoryImpl(context, userId);

        eventHandler = new EventHandlerImpl(id, context);

        realtimeDateInit();

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
                eventHandler.onPublish(sub, event);
            };

            public void onJoin(Subscription sub, JoinEvent event) {

            };

            public void onLeave(Subscription sub, LeaveEvent event) {

            };

            public void onSubscribeSuccess(Subscription sub, SubscribeSuccessEvent event) {

            };

            public void onSubscribeError(Subscription sub, SubscribeErrorEvent event) {

            };

            public void onUnsubscribe(Subscription sub, UnsubscribeEvent event) {

            };
        };

        centrifugoClient.connect();

    }

    @Override
    public void addMessageListener(BlaMessageListener blaMessageListener) {
        this.eventHandler.addMessageListener(blaMessageListener);
    }

    @Override
    public void addEventChannelListener(BlaChannelEventListener blaChannelEventListener) {
        this.eventHandler.addEventChannelListener(blaChannelEventListener);
    }

    @Override
    public void addPresenceListener(BlaPresenceListener blaPresenceListener) {

    }

    @Override
    public void getChannels(String channelId, Long offset, Callback<List<BlaChannel>> callback) {
        try {
            executors.submit(() -> {
                List<BlaChannel> channels = null;
                try {
                    channels = channelController.getChannels(channelId, offset);
                } catch (Exception e) {
                    callback.onFail(e);
                    e.printStackTrace();
                }
                callback.onSuccess(channels);
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
                    callback.onFail(e);
                }
                callback.onSuccess(users);
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
                callback.onSuccess(users);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).get();
    }

    @Override
    public void getMessages(String channelId, String lastID, Long limit, Callback<List<BlaMessage>> callback) {
        try {
            executors.submit(() -> {
                List<BlaMessage> messages = null;
                try {
                    messages = messageRepository.getMessages(channelId, lastID, limit);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callback.onSuccess(messages);
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
        }
    }

    @Override
    public void createChannel(String name, String avatar, List<String> userIds, BlaChannelType channelType, Callback<BlaChannel> callback) throws Exception {
        executors.submit(() -> {
            try {
                channelController.createChannel(
                        name,
                        avatar,
                        userIds,
                        channelType
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).get();
    }

    @Override
    public void updateChannel(BlaChannel newChannel, Callback<BlaChannel> callback) {
        try {
            executors.submit(() -> {
                callback.onSuccess(channelController.updateChannel(newChannel));
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }

    }

    @Override
    public void deleteChannel(String channelId, Callback<Boolean> callback) {
        try {
            executors.submit(() -> {
                callback.onSuccess(channelController.deleteChannel(channelId));
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }
    }

    @Override
    public void sendStartTyping(String channelID, Callback<Void> callback) {
        try {
            executors.submit(() -> {
                try {
                    channelController.sendStartTypingEvent(channelID);
                    callback.onSuccess(null);
                } catch (Exception e) {
                    callback.onFail(e);
                    e.printStackTrace();
                }
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }
    }

    @Override
    public void sendStopTyping(String channelID, Callback<Void> callback) {
        try {
            executors.submit(() -> {
                try {
                    channelController.sendStopTypingEvent(channelID);
                    callback.onSuccess(null);
                } catch (Exception e) {
                    callback.onFail(e);
                    e.printStackTrace();
                }
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }
    }

    @Override
    public void markSeenMessage(String messageID, String channelID, Callback<Void> callback) {
        try {
            executors.submit(() -> {
                Message message = messageRepository.getMessageById(messageID);
                try {
                    messageRepository.sendSeenEvent(message.getChannelId(), message.getId(), message.getAuthorId());
                    callback.onSuccess(null);
                } catch (Exception e) {
                    callback.onFail(e);
                    e.printStackTrace();
                }
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }
    }

    @Override
    public void markReceiveMessage(String messageID, String channelID, Callback<Void> callback) {
        try {
            executors.submit(() -> {
                Message message = messageRepository.getMessageById(messageID);
                try {
                    messageRepository.sendReceiveEvent(message.getChannelId(), message.getId(), message.getAuthorId());
                    callback.onSuccess(null);
                } catch (Exception e) {
                    callback.onFail(e);
                    e.printStackTrace();
                }
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }
    }

    @Override
    public void createMessage(String content, String channelID, BlaMessageType type, HashMap<String, Object> customData, Callback<BlaMessage> callback) {
        try {
            executors.submit(() -> {
                try {
                    BlaMessage message = messageRepository.createMessage(
                            String.valueOf(new Date().getTime()),
                            id,
                            channelID,
                            content,
                            customData
                    );

                    messageRepository.sendMessage(message);

                    Log.i("Sadas",""+message.getId());

                    callback.onSuccess(message);

                } catch (Exception e) {
                    callback.onFail(e);
                }
            });
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }
    }

    @Override
    public void updateMessage(BlaMessage updatedMessage, Callback<BlaMessage> callback) {
        //TODO: update message
    }

    @Override
    public void deleteMessage(BlaMessage deletedMessage, Callback<BlaMessage> callback) {
        //TODO: update message
    }

    @Override
    public void inviteUserToChannel(List<String> usersID, String channelId, Callback<Void> callback) {

        try {
            channelController.inviteUsersToChannel(channelId, usersID);
            callback.onSuccess(null);
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }
    }

    @Override
    public void removeUserFromChannel(String userID, String channelId, Callback<Void> callback) {

    }

    @Override
    public void getUsersPresence(Callback<List<BlaUser>> callback) {
        try {
            List<BlaUser> users = userRepository.getUsersPresence();
            callback.onSuccess(users);
        } catch (Exception e) {
            callback.onFail(e);
            e.printStackTrace();
        }
    }

}
