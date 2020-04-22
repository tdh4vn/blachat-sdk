package com.blameo.chatsdk.models.events;

import com.blameo.chatsdk.models.pojos.CustomData;

import java.io.Serializable;


//TODO: check xem co thay ChannelEvent thanh Channel dc ko
public class ChannelEvent extends CustomData implements Serializable {
    private String id;
    private String name;
    private String avatar;
    private int type;
    private String updatedAt;
    private String createdAt;
    private String last_message_id;

    public ChannelEvent() {
    }

    public ChannelEvent(String id, String name, String avatar, int type, String updatedAt, String createdAt, String last_message_id) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.type = type;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.last_message_id = last_message_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLast_message_id() {
        return last_message_id;
    }

    public void setLast_message_id(String last_message_id) {
        this.last_message_id = last_message_id;
    }



}
