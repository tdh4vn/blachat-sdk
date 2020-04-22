package com.blameo.chatsdk.repositories.local;

import com.blameo.chatsdk.models.pojos.UserInChannel;

import java.util.ArrayList;

public interface LocalUserInChannelRepository {
    ArrayList<String> getAllUserIdsInChannel(String channelId);

    ArrayList<UserInChannel> getAllUICs();

    ArrayList<String> getAllChannelIds(String id);

    void saveUserIdsToChannel(String channelId, ArrayList<String> uIds);

    int getTotalLocalUIC();

    void exportUicDB();

    void deleteUIC(UserInChannel userInChannel);

    void clearAllLocalUIC();
}
