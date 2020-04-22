package com.blameo.chatsdk.repositories.local;

import com.blameo.chatsdk.models.pojos.Channel;

import java.util.ArrayList;

public interface LocalChannelRepository {
    ArrayList<Channel> getChannels();

    void addLocalChannel(Channel channel);

    int getTotalLocalChannels();

    int updateChannel(Channel channel);

    Channel getChannelByID(String id);

    void exportChannelDB();

    void deleteChannelByID(String channelId);

    boolean checkIfChannelIsExist(String id);

    void clearAllLocalChannels();

    void updateLastMessage(String channelId, String messageId);
}
