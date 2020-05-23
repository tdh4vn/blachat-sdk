package com.blameo.chatsdk.blachat;


import android.content.Context;

import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaMessageType;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.entities.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface BlaChatSDKProxy {

    void init(Context context, String userId, String token);
    void addMessageListener(BlaMessageListener blaMessageListener);
    void addEventChannelListener(BlaChannelEventListener blaChannelEventListener);
    void addPresenceListener(BlaPresenceListener blaPresenceListener) throws Exception;
    void getChannels(String channelId, Long offset, Callback<List<BlaChannel>> callback);
    void getUsersInChannel(String channelId, Callback<List<BlaUser>> callback);
    void getUsers(ArrayList<String> userIds, Callback<List<BlaUser>> callback) throws Exception;
    void getMessages(String channelId, String lastID, Long limit, Callback<List<BlaMessage>> callback);
    void createChannel(String name, String avatar, List<String> userIds, BlaChannelType channelType, Callback<BlaChannel> callback) throws Exception;
    void updateChannel(BlaChannel newChannel, Callback<BlaChannel> callback);
    void deleteChannel(String channelId, Callback<Boolean> callback);
    void sendStartTyping(String channelID, Callback<Void> callback);
    void sendStopTyping(String channelID, Callback<Void> callback);
    void markSeenMessage(String messageID, String channelID, Callback<Void> callback);
    void markReceiveMessage(String messageID, String channelID, Callback<Void> callback);
    void createMessage(String content, String channelID, BlaMessageType type, HashMap<String, Object> customData, Callback<BlaMessage> callback);
    void updateMessage(BlaMessage updatedMessage, Callback<BlaMessage> callback);
    void deleteMessage(BlaMessage deletedMessage, Callback<BlaMessage> callback);
    void inviteUserToChannel(List<String> usersID, String channelId, Callback<Void> callback);
    void removeUserFromChannel(String userID, String channelId, Callback<Void> callback);
    void getUsersPresence(Callback<List<BlaUser>> callback) throws Exception;
    void getAllUsers(Callback<List<BlaUser>> callback);
}
