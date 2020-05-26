package com.blameo.chatsdk.controllers;

import android.content.Context;
import android.util.Log;

import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.BlaTypingEvent;
import com.blameo.chatsdk.models.bla.BlaUser;
import com.blameo.chatsdk.models.entities.Channel;
import com.blameo.chatsdk.models.entities.ChannelWithUser;
import com.blameo.chatsdk.models.entities.RemoteUserChannel;
import com.blameo.chatsdk.models.entities.User;
import com.blameo.chatsdk.models.entities.UserInChannel;
import com.blameo.chatsdk.models.results.MembersInChannelRemoteDTO;
import com.blameo.chatsdk.repositories.ChannelRepository;
import com.blameo.chatsdk.repositories.ChannelRepositoryImpl;
import com.blameo.chatsdk.repositories.MessageRepository;
import com.blameo.chatsdk.repositories.MessageRepositoryImpl;
import com.blameo.chatsdk.repositories.UserRepository;
import com.blameo.chatsdk.repositories.UserRepositoryImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChannelControllerImpl implements ChannelController {

    private static final String TAG = "CHANNEL_CONTROLLER";
    private UserRepository userRepository;

    private ChannelRepository channelRepository;

    private MessageRepository messageRepository;

    public ChannelControllerImpl(Context context, String myId){
        userRepository = new UserRepositoryImpl(context, myId);
        channelRepository = new ChannelRepositoryImpl(context);
        messageRepository = new MessageRepositoryImpl(context);
    }

    @Override
    public List<BlaChannel> syncChannels(String lastChannelId, long limit) throws Exception {
        return null;
    }

    @Override
    public List<BlaUser> getUsersInChannel(String channelId) throws Exception {
        ChannelWithUser channelWithUser = channelRepository.getLocalUserInChannel(channelId);

        if (channelWithUser != null && channelWithUser.members != null) {
            if (channelWithUser.members.size() > 0) {
                List<BlaUser> users = new ArrayList<>();
                for (User user: channelWithUser.members) {
                    users.add(new BlaUser(user));
                }
                return users;
            } else {
                List<MembersInChannelRemoteDTO> membersInChannelRemoteDTOS =
                        channelRepository.getRemoteUserInChannel(Collections.singletonList(channelId));
                List<UserInChannel> userInChannels = new ArrayList<>();
                List<String> userIds = new ArrayList<>();
                for (MembersInChannelRemoteDTO membersInChannelRemoteDTO : membersInChannelRemoteDTOS){
                    userInChannels.addAll(membersInChannelRemoteDTO.toUserInChannel());
                    for (RemoteUserChannel remoteUserChannel: membersInChannelRemoteDTO.getUserChannels()) {
                        userIds.add(remoteUserChannel.getMemberId());
                    }
                }
                channelRepository.saveUsersInChannel(userInChannels);

                return userRepository.getUsersByIds(userIds);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public boolean sendStartTypingEvent(String channelId) throws Exception {
        return channelRepository.sendTypingEvent(channelId, BlaTypingEvent.START);
    }

    @Override
    public boolean sendStopTypingEvent(String channelId) throws Exception {
        return channelRepository.sendTypingEvent(channelId, BlaTypingEvent.STOP);
    }

    @Override
    public boolean inviteUsersToChannel(String channelId, List<String> userIds) throws Exception {
        return channelRepository.addUserToChannel(channelId, userIds);
    }

    @Override
    public BlaChannel onNewChannel(Channel channel) throws Exception {
        channelRepository.saveChannel(channel);
        getUsersInChannel(channel.getId());
        return new BlaChannel(channel);
    }

    @Override
    public List<BlaChannel> getChannels(String lastChannelId, long limit) throws Exception {
        return channelRepository.getChannels(lastChannelId, limit);
    }

    @Override
    public BlaChannel updateChannel(BlaChannel newChannel) {
        return channelRepository.updateChannel(newChannel);
    }

    @Override
    public boolean deleteChannel(String channelID) {
        return channelRepository.deleteChannel(channelID);
    }

    @Override
    public BlaChannel getChannelById(String channelID) throws IOException {
        return channelRepository.getChannelById(channelID);
    }

    @Override
    public BlaChannel createChannel(String name, String avatar, List<String> userIds, BlaChannelType blaChannelType) throws Exception {
        return channelRepository.createChannel(name, avatar, userIds, blaChannelType);
    }

    @Override
    public void updateLastMessageOfChannel(String channelId, String messageId) {
        channelRepository.updateLastMessage(channelId, messageId);
    }

    @Override
    public boolean checkChannelIsExist(String channelId) {
        return channelRepository.checkChannelIsExist(channelId);
    }

    @Override
    public void usersAddedToChannel(String channelId, List<String> userIds) {
        channelRepository.usersAddedToChannel(channelId, userIds);
    }

}
