package com.blameo.chatsdk.repositories.local;

import com.blameo.chatsdk.models.pojos.Message;
import com.blameo.chatsdk.models.pojos.RemoteUserChannel;
import com.blameo.chatsdk.models.pojos.UserInChannel;

import java.util.ArrayList;

public interface LocalUserInChannelRepository {
    ArrayList<RemoteUserChannel> getAllUserIdsInChannel(String channelId);

    ArrayList<UserInChannel> getAllUICs();

    ArrayList<String> getAllChannelIds(String id);

    void saveUserIdsToChannel(String channelId, ArrayList<RemoteUserChannel> uic);

    void updateUserLastSeenInChannel(String userId, String channelId, Message lastMessage);

    int getTotalLocalUIC();

    void exportUicDB();

    void deleteUIC(UserInChannel userInChannel);

    void clearAllLocalUIC();
}
