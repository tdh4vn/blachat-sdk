package com.blameo.chatsdk.local;

import com.blameo.chatsdk.models.pojos.UserInChannel;

import java.util.ArrayList;

public interface LocalUserInChannelRepository {
    ArrayList<String> getAllUserIdsInChannel(String channelId);

    ArrayList<UserInChannel> getAllUICs();

    void saveUserIdsToChannel(String channelId, ArrayList<String> uIds);

    int getTotalLocalUIC();

    void exportUicDB();

    void deleteUIC(UserInChannel userInChannel);

    void clearAllLocalUIC();
}
