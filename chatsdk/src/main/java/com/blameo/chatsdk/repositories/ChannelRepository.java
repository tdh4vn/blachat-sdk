package com.blameo.chatsdk.repositories;

import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.models.bla.BlaTypingEvent;
import com.blameo.chatsdk.models.entities.Channel;
import com.blameo.chatsdk.models.entities.ChannelWithUser;
import com.blameo.chatsdk.models.entities.UserInChannel;
import com.blameo.chatsdk.models.results.MembersInChannelRemoteDTO;

import java.io.IOException;
import java.util.List;

public interface ChannelRepository {

    List<MembersInChannelRemoteDTO> getRemoteUserInChannel(List<String> channelIds) throws IOException;

    ChannelWithUser getLocalUserInChannel(String channelId);

    List<BlaChannel> getChannels(String lastChannelId, long limit) throws Exception;

    BlaChannel updateChannel(BlaChannel newChannel);

    BlaChannel getChannelById(String id) throws IOException;

    boolean deleteChannel(String channelID);

    BlaChannel createChannel(String name, String avatar, List<String> userIds, BlaChannelType blaChannelType) throws Exception;

    boolean updateLastMessage(String channelId, String messageId);

    boolean addUserToChannel(String channelId, List<String> userIds) throws Exception;

    boolean sendTypingEvent(String channelId, BlaTypingEvent typingEvent) throws Exception;

    void saveUsersInChannel(List<UserInChannel> userInChannelList);

    void saveChannel(Channel channel);

    boolean checkChannelIsExist(String channelId);

    void usersAddedToChannel(String channelId, List<String> userIds);
}
