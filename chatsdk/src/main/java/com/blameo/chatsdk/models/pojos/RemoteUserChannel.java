package com.blameo.chatsdk.models.pojos;

public class RemoteUserChannel {

    private String memberId;
    private String lastSeen;
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
