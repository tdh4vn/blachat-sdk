package com.blameo.chatsdk.blachat;


import android.content.Context;

import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaMessageType;
import com.blameo.chatsdk.models.bla.BlaUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BlaChatSDKProxy {

    void initBlaChatSDK(Context context, String userId, String token) throws Exception;
    void addMessageListener(MessagesListener messagesListener);
    void removeMessageListener(MessagesListener messagesListener);
    void addChannelListener(ChannelEventListener channelEventListener);
    void removeChannelListener(ChannelEventListener channelEventListener);
    void addPresenceListener(BlaPresenceListener blaPresenceListener) throws Exception;
    void removePresenceListener(BlaPresenceListener blaPresenceListener);
    void getChannels(String lastId, Integer limit, Callback<List<BlaChannel>> callback);
    void getUsersInChannel(String channelId, Callback<List<BlaUser>> callback);
    void getUsers(ArrayList<String> userIds, Callback<List<BlaUser>> callback) throws Exception;
    void getMessages(String channelId, String lastId, Integer limit, Callback<List<BlaMessage>> callback);
    void createChannel(String name, String avatar, List<String> userIds, BlaChannelType channelType, Map<String, Object> customData, Callback<BlaChannel> callback) throws Exception;
    void updateChannel(BlaChannel newChannel, Callback<BlaChannel> callback);
    void deleteChannel(BlaChannel blaChannel, Callback<BlaChannel> callback);
    void sendStartTyping(String channelId, Callback<Boolean> callback);
    void sendStopTyping(String channelId, Callback<Boolean> callback);
    void markSeenMessage(String messageId, String channelId, Callback<Boolean> callback);
    void markReceiveMessage(String messageId, String channelId, Callback<Boolean> callback);
    void createMessage(String content, String channelId, BlaMessageType type, Map<String, Object> customData, Callback<BlaMessage> callback);
    void updateMessage(BlaMessage updatedMessage, Callback<BlaMessage> callback);
    void deleteMessage(BlaMessage deletedMessage, Callback<BlaMessage> callback);
    void inviteUserToChannel(List<String> usersIds, String channelId, Callback<Boolean> callback);
    void removeUserFromChannel(String userId, String channelId, Callback<Boolean> callback);
    void getUserPresence(Callback<List<BlaUser>> callback) throws Exception;
    void getAllUsers(Callback<List<BlaUser>> callback);
    void searchChannels(String q, Callback<List<BlaChannel>> callback);
    void getMessageByType(String channelId, BlaMessageType type,Callback<List<BlaMessage>> callback);
    void logout();
    void updateFCMToken(String fcmToken);
}