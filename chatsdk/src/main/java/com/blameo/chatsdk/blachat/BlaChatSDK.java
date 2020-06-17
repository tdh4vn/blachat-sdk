package com.blameo.chatsdk.blachat;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.blameo.chatsdk.controllers.ChannelController;
import com.blameo.chatsdk.controllers.ChannelControllerImpl;
import com.blameo.chatsdk.handlers.EventHandler;
import com.blameo.chatsdk.handlers.EventHandlerImpl;
import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaMessageType;
import com.blameo.chatsdk.models.bla.BlaUser;
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
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private HashMap<String ,BlaUser> usersMap = new HashMap<>();

    private EventHandler eventHandler;

    private ChannelController channelController;

    private MessageRepository messageRepository;

    private UserRepository userRepository;

    private ExecutorService executors = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

    private BlaChatSDK() {
    }

    private HashMap<String, BlaUser> getUsersMap(){
        return usersMap;
    }

    private void setUsersMap(HashMap<String, BlaUser> map){
        usersMap = map;
    }

    public static BlaChatSDK getInstance() {
        //TODO: thread safe
        if (instance == null) {
            instance = new BlaChatSDK();
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void initBlaChatSDK(Context context, String userId, String token) {
        this.token = token;
        this.id = userId;
        this.myChannel = "chat#" + userId;
        this.applicationContext = context;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        APIProvider.INSTANCE.setSession(BASE_API_URL, token);


        this.channelController = new ChannelControllerImpl(context, userId);
        this.messageRepository = new MessageRepositoryImpl(context,  userId);
        this.userRepository = new UserRepositoryImpl(context, userId);
        eventHandler = new EventHandlerImpl(id, context);

        realtimeDateInit();

        syncUnsentMessages();

        getEvent();

        fetchAllUsers();
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
                Log.i(TAG, "connect");
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

        Log.i("TAG", "init token " + token);


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

        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<BlaUser> users = userRepository.getAllUsersStates();
                if (users != null && users.size() > 0)
                    Log.i(TAG, "total :"+users.size());
                    for (BlaUser user : users) {
                        if(blaPresenceListener != null)
                            blaPresenceListener.onUpdate(user);
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void removePresenceListener(BlaPresenceListener blaPresenceListener) {

    }

    @Override
    public void getChannels(String lastId, Integer limit, Callback<List<BlaChannel>> callback) {
        try {
            executors.submit(() -> {
                List<BlaChannel> channels = null;
                try {
                    channels = channelController.getChannels(lastId, limit);
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
    public void getMessages(String channelId, String lastId, Integer limit, Callback<List<BlaMessage>> callback) {
        try {
            executors.submit(() -> {
                List<BlaMessage> messages = null;
                try {
                    messages = messageRepository.getMessages(channelId, lastId, limit);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                messages = handleMessages(messages);

                Log.i(TAG, "xyz: "+messages.size());
                BlaChannel channel = channelController.resetUnreadMessagesInChannel(channelId);
                channelUpdated(channel);

                callback.onSuccess(messages);
            }).get();
        } catch (Exception e) {
            callback.onFail(e);
        }
    }

    private void channelUpdated(BlaChannel channel) {
        eventHandler.onChannelUpdate(channel);
    }

    private List<BlaMessage> handleMessages(List<BlaMessage> messages) {
        Log.i(TAG, "abcd: "+messages.size());

        for (BlaMessage message: messages){
            Log.i(TAG, "convert");
            BlaUser author = getUsersMap().get(message.getAuthorId());
//            Log.i(TAG, "author: "+author.getId() + " "+author.getName());
            if(author!= null)
                message.setAuthor(author);
        }

        Log.i(TAG, "def "+messages.size());

        return messages;
    }

    @Override
    public void createChannel(String name, List<String> userIds, BlaChannelType channelType, Map<String, Object> customData, Callback<BlaChannel> callback) throws Exception {
        executors.submit(() -> {
            try {
                BlaChannel channel = channelController.createChannel(
                        name,
                        userIds,
                        channelType
                );
                callback.onSuccess(channel);
            } catch (Exception e) {
                callback.onFail(e);
                e.printStackTrace();
            }
        }).get();
    }

    @Override
    public void updateChannel(BlaChannel newChannel, Callback<BlaChannel> callback) {
        try {
            executors.submit(() -> {
                try {
                    callback.onSuccess(channelController.updateChannel(newChannel));
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onFail(e);
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
                        callback.onSuccess(blaChannel);
                } catch (IOException e) {
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
    public void sendStartTyping(String channelID, Callback<Boolean> callback) {
        try {
            executors.submit(() -> {
                try {
                    channelController.sendStartTypingEvent(channelID);
                    callback.onSuccess(true);
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
    public void sendStopTyping(String channelID, Callback<Boolean> callback) {

        try {
            executors.submit(() -> {
                try {
                    channelController.sendStopTypingEvent(channelID);
                    callback.onSuccess(true);
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
    public void markSeenMessage(String messageId, String channelId, String seenId, Callback<Boolean> callback) {
        try {
            executors.submit(() -> {
                messageRepository.userSeenMyMessage(id, messageId, new Date());
                try {
                    messageRepository.sendSeenEvent(channelId, messageId, seenId);
                    BlaChannel channel = channelController.resetUnreadMessagesInChannel(channelId);
                    channelUpdated(channel);
                    callback.onSuccess(true);
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
    public void markReceiveMessage(String messageId, String channelId, String receiveId, Callback<Boolean> callback) {
        try {
            executors.submit(() -> {
                try {
                    messageRepository.sendReceiveEvent(channelId, messageId, receiveId);
                    callback.onSuccess(true);
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
    public void createMessage(String content, String channelID, BlaMessageType type, Map<String, Object> customData, Callback<BlaMessage> callback) {
        try {
            executors.submit(() -> {
                try {
                    BlaMessage message = messageRepository.createMessage(
                            String.valueOf(new Date().getTime()),
                            id,
                            channelID,
                            content,
                            type.getType(),
                            customData
                    );

                    try{
                        BlaMessage m = messageRepository.sendMessage(message);
                        callback.onSuccess(m);
                        channelController.updateLastMessageOfChannel(m.getChannelId(), m.getId());
                    }catch (Exception e){
                        callback.onSuccess(message);
                        channelController.updateLastMessageOfChannel(message.getChannelId(), message.getId());
                    }

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
        executors.submit(() -> {
            try {
                BlaMessage message = messageRepository.updateMessage(updatedMessage);
                callback.onSuccess(message);
            } catch (Exception e) {
                callback.onFail(e);
                e.printStackTrace();
            }
        });
    }

    @Override
    public void deleteMessage(BlaMessage deletedMessage, Callback<BlaMessage> callback) {
        executors.submit(() -> {
            try {
                BlaMessage message = messageRepository.deleteMessage(deletedMessage);
                callback.onSuccess(message);
            } catch (Exception e) {
                callback.onFail(e);
                e.printStackTrace();
            }
        });
    }

    @Override
    public void inviteUserToChannel(List<String> usersID, String channelId, Callback<Boolean> callback) {

        executors.submit(() -> {
            try {
                channelController.inviteUsersToChannel(channelId, usersID);
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onFail(e);
                e.printStackTrace();
            }
        });

    }

    @Override
    public void removeUserFromChannel(String userID, String channelId, Callback<Boolean> callback) {
        executors.submit(() -> {
            try {
                channelController.removeUserFromChannel(userID, channelId);
                callback.onSuccess(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void getUserPresence(Callback<List<BlaUser>> callback) {

        executors.submit(() -> {
            try {
                callback.onSuccess(userRepository.getUsersPresence());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fetchAllUsers(){
        try {
            executors.submit(() -> {
                try {
                    List<BlaUser> users = userRepository.fetchAllUsers();

                    Map<String, BlaUser> map = users.stream().collect(Collectors.toMap(BlaUser::getId, user -> user));

                    Log.i(TAG, "map: "+map.size());
                    setUsersMap((HashMap<String, BlaUser>) map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void getAllUsers(Callback<List<BlaUser>> callback) {
        try {
            executors.submit(() -> {
                try {
                    List<BlaUser> users = userRepository.getAllUsers();

                    callback.onSuccess(users);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
