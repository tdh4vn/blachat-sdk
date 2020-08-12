package com.blameo.chatsdk.controllers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.models.bla.BlaMessage;
import com.blameo.chatsdk.models.bla.EventType;
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
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class ChannelControllerImpl implements ChannelController {

    private static final String TAG = "CHANNEL_CONTROLLER";
    private UserRepository userRepository;

    private ChannelRepository channelRepository;

    private MessageRepository messageRepository;

    public ChannelControllerImpl(){
        userRepository = UserRepositoryImpl.getInstance();
        channelRepository = ChannelRepositoryImpl.getInstance();
        messageRepository = MessageRepositoryImpl.getInstance();
    }

    @Override
    public List<BlaChannel> syncChannels(String lastChannelId, int limit) throws Exception {
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
        return channelRepository.sendTypingEvent(channelId, EventType.START);
    }

    @Override
    public boolean sendStopTypingEvent(String channelId) throws Exception {
        return channelRepository.sendTypingEvent(channelId, EventType.STOP);
    }

    @Override
    public boolean inviteUsersToChannel(String channelId, List<String> userIds) throws Exception {
        return channelRepository.addUserToChannel(channelId, userIds);
    }

    @Override
    public BlaChannel onNewChannel(Channel channel) throws Exception {

        if (channel.isDirect()) {
            List<MembersInChannelRemoteDTO> channelRemoteDTOS
                    = channelRepository.getRemoteUserInChannel(Collections.singletonList(channel.getId()));
            if (channelRemoteDTOS.size() > 0) {

                MembersInChannelRemoteDTO channelRemoteDTO = channelRemoteDTOS.get(0);
//                channelRepository.saveUsersInChannel(channelRemoteDTO.getUserChannels());

                String otherUserId = "";
                for (String uID: channelRemoteDTO.getMemberIds()){
                    if (!uID.equals(userRepository.getMyId())) {
                        otherUserId = uID;
                        break;
                    }
                }
                if (!TextUtils.isEmpty(otherUserId)) {
                    BlaUser blaUser = userRepository.getUserById(otherUserId);
                    channel.setName(blaUser.getName());
                    channel.setAvatar(blaUser.getAvatar());
                }
            }
        }

        channelRepository.saveChannel(channel);
        getUsersInChannel(channel.getId());
        if (channel.getLastMessages() != null && channel.getLastMessages().size() > 0) {
            messageRepository.saveMessages(channel.getLastMessages());
        }

        return new BlaChannel(channel);
    }

    @Override
    public List<BlaChannel> getChannels(String lastChannelId, int limit) throws Exception {
        return injectAuthorToLastMessageOfChannel(channelRepository.getChannels(lastChannelId, limit, userRepository));
    }

    @Override
    public BlaChannel updateChannel(BlaChannel newChannel) throws IOException {
        return channelRepository.updateChannel(newChannel);
    }

    @Override
    public BlaChannel onChannelUpdate(Channel channelUpdate) throws IOException {
        return channelRepository.onChannelUpdate(channelUpdate);
    }

    @Override
    public boolean deleteChannel(String channelID) throws IOException {
        return channelRepository.deleteChannel(channelID);
    }

    @Override
    public BlaChannel getChannelById(String channelID) throws IOException {
        BlaChannel blaChannel = channelRepository.getChannelById(channelID);
        BlaMessage blaMessage = messageRepository.getMessageById(blaChannel.getLastMessageId());
        blaChannel.setLastMessage(blaMessage);
        return blaChannel;
    }

    @Override
    public BlaChannel createChannel(String name, String avatar, List<String> userIds, BlaChannelType blaChannelType, Map<String, Object> customData) throws Exception {
        return channelRepository.createChannel(name, avatar, userIds, blaChannelType, customData);
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

    @Override
    public void removeUserFromChannel(String userId, String channelId) throws Exception {
        channelRepository.removeUserFromChannel(userId, channelId);
    }

    @Override
    public void updateLastSeen(String channelId, String userId, Date date) {
        channelRepository.updateSeenTime(channelId, userId, date);
    }

    @Override
    public void updateLastReceived(String channelId, String userId, Date date) {
        channelRepository.updateReceiveTime(channelId, userId, date);
    }

    @Override
    public BlaChannel resetUnreadMessagesInChannel(String channelId) {
        return channelRepository.resetUnreadMessagesInChannel(channelId);
    }

    @Override
    public BlaChannel updateUserLastSeenInChannel(String channelId) {
        return channelRepository.updateUserLastSeenInChannel(channelId);
    }

    @Override
    public List<BlaChannel> searchChannel(String q) {
        return injectAuthorToLastMessageOfChannel(channelRepository.searchChannel(q));
    }

    private List<BlaChannel> injectAuthorToLastMessageOfChannel(List<BlaChannel> channels) {
        for(BlaChannel channel: channels){
            if (channel.getLastMessage()!= null){
                User user = userRepository.getUserById(channel.getLastMessage().getAuthorId());
                if(user != null) {
                    channel.getLastMessage().setAuthor(new BlaUser(user));
                }
            }
        }

        return channels;
    }

}
