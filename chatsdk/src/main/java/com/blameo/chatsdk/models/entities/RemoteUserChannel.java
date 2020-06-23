package com.blameo.chatsdk.models.entities;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class RemoteUserChannel {

    @SerializedName("member_id")
    private String memberId;

    @SerializedName("last_seen")
    private Date lastSeen;

    @SerializedName("last_receive")
    private Date lastReceive;

    public RemoteUserChannel() {
    }

    public RemoteUserChannel(String memberId, Date lastReceive, Date lastSeen) {
        this.memberId = memberId;
        this.lastReceive = lastReceive;
        this.lastSeen = lastSeen;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Date getLastReceive() {
        return lastReceive;
    }

    public void setLastReceive(Date lastReceive) {
        this.lastReceive = lastReceive;
    }
}
