package com.blameo.chatsdk.repositories;

import com.blameo.chatsdk.models.bla.BlaChannel;
import com.blameo.chatsdk.models.bla.BlaChannelType;
import com.blameo.chatsdk.models.bla.EventType;
import com.blameo.chatsdk.models.entities.Channel;
import com.blameo.chatsdk.models.entities.ChannelWithUser;
import com.blameo.chatsdk.models.entities.UserInChannel;
import com.blameo.chatsdk.models.results.MembersInChannelRemoteDTO;

import java.io.IOException;
import java.util.List;

public interface ChannelRepository {

    List<MembersInChannelRemoteDTO> getRemoteUserInChannel(List<String> channelIds) throws IOException;

    ChannelWithUser getLocalUserInChannel(String channelId);

    List<BlaChannel> getChannels(String lastChannelId, int limit) throws Exception;

    BlaChannel updateChannel(BlaChannel newChannel) throws IOException;

    BlaChannel getChannelById(String id) throws IOException;

    boolean deleteChannel(String channelID) throws IOException;

    BlaChannel createChannel(String name, List<String> userIds, BlaChannelType blaChannelType) throws Exception;

    boolean updateLastMessage(String channelId, String messageId);

    boolean addUserToChannel(String channelId, List<String> userIds) throws Exception;

    boolean sendTypingEvent(String channelId, EventType typingEvent) throws Exception;

    void saveUsersInChannel(List<UserInChannel> userInChannelList);

    void saveChannel(Channel channel);

    boolean checkChannelIsExist(String channelId);

    void usersAddedToChannel(String channelId, List<String> userIds);

    void removeUserFromChannel(String userId, String channelId) throws Exception;
}
