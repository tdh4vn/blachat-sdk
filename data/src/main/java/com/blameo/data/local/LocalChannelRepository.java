package com.blameo.data.local;

import com.blameo.data.models.pojos.Channel;

import java.util.ArrayList;

public interface LocalChannelRepository{
    ArrayList<Channel> getChannels();
    void addLocalChannel(Channel channel);
    int getTotalLocalChannels();
    int updateChannel(Channel channel);
    Channel getChannelByID(String id);
    void exportChannelDB();
    void deleteChannelByID(String channelId);
    boolean checkIfChannelIsExist(String id);
    void clearAllLocalChannels();
}
