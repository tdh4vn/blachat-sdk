package com.blameo.chatsdk.models.pojos;

import com.google.gson.annotations.SerializedName;

public class RemoteUserChannel {

    @SerializedName("member_id")
    private String memberId;

    @SerializedName("last_seen")
    private String lastSeen;

    @SerializedName("last_receive")
    private String lastReceive;

    public RemoteUserChannel() {
    }

    public RemoteUserChannel(String memberId, String lastSeen, String lastReceive) {
        this.memberId = memberId;
        this.lastSeen = lastSeen;
        this.lastReceive = lastReceive;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getLastReceive() {
        return lastReceive;
    }

    public void setLastReceive(String lastReceive) {
        this.lastReceive = lastReceive;
    }
}
