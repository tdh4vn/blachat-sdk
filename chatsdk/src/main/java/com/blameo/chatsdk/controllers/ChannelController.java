package com.blameo.chatsdk.controllers;

import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.entities.Channel;

import java.util.List;

public interface ChannelController {
    List<BlaChannel> syncChannels(String lastChannelId, long limit) throws Exception;
    List<BlaUser> getUsersInChannel(String channelId) throws Exception;
    boolean sendStartTypingEvent(String channelId) throws Exception;
    boolean sendStopTypingEvent(String channelId) throws Exception;
    boolean inviteUsersToChannel(String channelId, List<String> userIds) throws Exception;
    BlaChannel onNewChannel(Channel channel) throws Exception ;
    List<BlaChannel> getChannels(String lastChannelId, long limit) throws Exception;
    BlaChannel updateChannel(BlaChannel newChannel);
    boolean deleteChannel(String channelID);
    BlaChannel getChannelById(String channelID);
    BlaChannel createChannel(String name, String avatar, List<String> userIds, BlaChannelType blaChannelType) throws Exception;
}
