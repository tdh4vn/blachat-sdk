package com.blameo.chatsdk.models.pojos;

public class RemoteUserChannel {

    private String member_id;
    private String last_seen;
    private String last_receive;

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(String last_seen) {
        this.last_seen = last_seen;
    }

    public String getLast_receive() {
        return last_receive;
    }

    public void setLast_receive(String last_receive) {
        this.last_receive = last_receive;
    }
}
