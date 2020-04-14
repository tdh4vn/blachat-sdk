package com.blameo.chatsdk.models.pojos;

public class Channel extends CustomData{
    private String id;
    private String name;
    private String avatar;
    private int type;
    private String updated_at;
    private String created_at;
    private Message last_message;
    private String last_message_id;

    // member_ids AL<String>

    public Channel() {
    }

    public Channel(String id, String name, String avatar, int type, String updated_at, String created_at, String last_message_id) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.type = type;
        this.updated_at = updated_at;
        this.created_at = created_at;
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

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Message getLast_message() {
        return last_message;
    }

    public void setLast_message(Message last_message) {
        this.last_message = last_message;
    }

    public String getLast_message_id() {
        return last_message_id;
    }

    public void setLast_message_id(String last_message_id) {
        this.last_message_id = last_message_id;
    }


}
