package com.blameo.chatsdk.controllers;

import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.entities.Channel;

import java.io.IOException;
import java.util.List;

public interface ChannelController {
    List<BlaChannel> syncChannels(String lastChannelId, int limit) throws Exception;
    List<BlaUser> getUsersInChannel(String channelId) throws Exception;
    boolean sendStartTypingEvent(String channelId) throws Exception;
    boolean sendStopTypingEvent(String channelId) throws Exception;
    boolean inviteUsersToChannel(String channelId, List<String> userIds) throws Exception;
    BlaChannel onNewChannel(Channel channel) throws Exception ;
    List<BlaChannel> getChannels(String lastChannelId, int limit) throws Exception;
    BlaChannel updateChannel(BlaChannel newChannel) throws IOException;
    boolean deleteChannel(String channelID) throws IOException;
    BlaChannel getChannelById(String channelID) throws Exception;
    BlaChannel createChannel(String name, List<String> userIds, BlaChannelType blaChannelType) throws Exception;
    void updateLastMessageOfChannel(String channelId, String messageId);
    boolean checkChannelIsExist(String channelId);
    void usersAddedToChannel(String channelId, List<String> userIds);
    void removeUserFromChannel(String userId, String channelId) throws Exception;
}
