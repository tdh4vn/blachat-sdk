package com.blameo.chatsdk.models.results;

import com.blameo.chatsdk.models.entities.RemoteUserChannel;
import com.blameo.chatsdk.models.entities.UserInChannel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MembersInChannelRemoteDTO extends BaseResult {

    @SerializedName("channel_id")
    private String channelId;

    @SerializedName("list_member")
    private ArrayList<RemoteUserChannel> userChannels;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public ArrayList<RemoteUserChannel> getUserChannels() {
        return userChannels;
    }

    public void setUserChannels(ArrayList<RemoteUserChannel> userChannels) {
        this.userChannels = userChannels;
    }


    public List<String> getMemberIds() {
        ArrayList<String> ids = new ArrayList<>();

        for (RemoteUserChannel membersInChannel: userChannels) {
            ids.add(membersInChannel.getMemberId());
        }

        return ids;
    }

    public ArrayList<UserInChannel> toUserInChannel() {
        ArrayList<UserInChannel> userInChannels = new ArrayList<>();

        for (RemoteUserChannel membersInChannel: userChannels) {
            userInChannels.add(new UserInChannel(
                    channelId,
                    membersInChannel.getMemberId(),
                    membersInChannel.getLastReceive(),
                    membersInChannel.getLastSeen()
            ));
        }

        return userInChannels;
    }

}
