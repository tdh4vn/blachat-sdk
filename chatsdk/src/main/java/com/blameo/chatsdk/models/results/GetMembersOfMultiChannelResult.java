package com.blameo.chatsdk.models.results;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetMembersOfMultiChannelResult extends BaseResult{

    @SerializedName("data")
    private ArrayList<MembersInChannelRemoteDTO> membersInChannelRemoteDTOS;

    public ArrayList<MembersInChannelRemoteDTO> getMembersInChannelRemoteDTOS() {
        return membersInChannelRemoteDTOS;
    }

    public void setMembersInChannelRemoteDTOS(ArrayList<MembersInChannelRemoteDTO> membersInChannelRemoteDTOS) {
        this.membersInChannelRemoteDTOS = membersInChannelRemoteDTOS;
    }
}
