package com.blameo.chatsdk.blachat;

import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaMessageType;
import com.blameo.chatsdk.models.bla.BlaUser;

import java.util.ArrayList;
import java.util.HashMap;

public class BlaChatSDK implements BlaChatSDKProxy {

    private static BlaChatSDK instance = null;

    private BlaChatSDK() {
        //TODO: Init properties
    }

    public static BlaChatSDK getInstance() {

        //TODO: thread safe
        if (instance == null) {
            instance = new BlaChatSDK();
        }
        return instance;
    }


    @Override
    public void init(String token) {

    }

    @Override
    public void addMessageListener(BlaMessageListener blaMessageListener) {

    }

    @Override
    public void addEventChannelListener(BlaChannelEventListener blaChannelEventListener) {

    }

    @Override
    public void getChannels(String channelId, Long offset, Callback<ArrayList<BlaChannel>> callback) {

    }

    @Override
    public void getUsersInChannel(String channelId, Callback<ArrayList<ArrayList<BlaUser>>> callback) {

    }

    @Override
    public void getUsers(ArrayList<String> userIds, Callback<ArrayList<BlaUser>> callback) {

    }

    @Override
    public void getMessages(String lastID, Long limit, Callback<ArrayList<BlaMessage>> callback) {

    }

    @Override
    public void createChannel(String name, String avatar, ArrayList<String> userIds, BlaChannelType channelType, Callback<BlaChannel> callback) {

    }

    @Override
    public void updateChannel(BlaChannel newChannel, Callback<BlaChannel> callback) {

    }

    @Override
    public void deleteChannel(String channelId, Callback<BlaChannel> callback) {

    }

    @Override
    public void sendStartTyping(String channelID, Callback<Void> callback) {

    }

    @Override
    public void sendStopTyping(String channelID, Callback<Void> callback) {

    }

    @Override
    public void markSeenMessage(String messageID, String channelID, Callback<Void> callback) {

    }

    @Override
    public void markReceiveMessage(String messageID, String channelID) {

    }

    @Override
    public void createMessage(String content, String channelID, BlaMessageType type, HashMap<String, Object> customData) {

    }

    @Override
    public void updateMessage(BlaMessage updatedMessage, Callback<BlaMessage> callback) {

    }

    @Override
    public void deleteMessage(BlaMessage deletedMessage, Callback<BlaMessage> callback) {

    }

    @Override
    public void inviteUserToChannel(String userID, String channelId, Callback<Void> callback) {

    }

    @Override
    public void removeUserFromChannel(String userID, String channelId, Callback<Void> callback) {

    }
}
