package com.blameo.chatsdk.blachat;


import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaMessageType;
import com.blameo.chatsdk.models.bla.BlaUser;

import java.util.ArrayList;
import java.util.HashMap;

public interface BlaChatSDKProxy {

    void init(String token);
    void addMessageListener(BlaMessageListener blaMessageListener);
    void addEventChannelListener(BlaChannelEventListener blaChannelEventListener);
    void getChannels(String channelId, Long offset, Callback<ArrayList<BlaChannel>> callback);
    void getUsersInChannel(String channelId, Callback<ArrayList<ArrayList<BlaUser>>> callback);
    void getUsers(ArrayList<String> userIds, Callback<ArrayList<BlaUser>> callback);
    void getMessages(String lastID, Long limit, Callback<ArrayList<BlaMessage>> callback);
    void createChannel(String name, String avatar, ArrayList<String> userIds, BlaChannelType channelType, Callback<BlaChannel> callback);
    void updateChannel(BlaChannel newChannel, Callback<BlaChannel> callback);
    void deleteChannel(String channelId, Callback<BlaChannel> callback);
    void sendStartTyping(String channelID, Callback<Void> callback);
    void sendStopTyping(String channelID, Callback<Void> callback);
    void markSeenMessage(String messageID, String channelID, Callback<Void> callback);
    void markReceiveMessage(String messageID, String channelID);
    void createMessage(String content, String channelID, BlaMessageType type, HashMap<String, Object> customData);
    void updateMessage(BlaMessage updatedMessage, Callback<BlaMessage> callback);
    void deleteMessage(BlaMessage deletedMessage, Callback<BlaMessage> callback);
    void inviteUserToChannel(String userID, String channelId, Callback<Void> callback);
    void removeUserFromChannel(String userID, String channelId, Callback<Void> callback);
}
