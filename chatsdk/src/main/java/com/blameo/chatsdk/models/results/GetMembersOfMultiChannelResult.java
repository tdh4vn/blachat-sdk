package com.blameo.chatsdk.models.results;

import com.blameo.chatsdk.models.pojos.RemoteUserChannel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetMembersOfMultiChannelResult extends BaseResult{

    @SerializedName("data")
    private ArrayList<MembersInChannel> membersInChannels;

    public ArrayList<MembersInChannel> getMembersInChannels() {
        return membersInChannels;
    }

    public void setMembersInChannels(ArrayList<MembersInChannel> membersInChannels) {
        this.membersInChannels = membersInChannels;
    }
}
