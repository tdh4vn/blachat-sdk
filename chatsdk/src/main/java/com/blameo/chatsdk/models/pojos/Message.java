package com.blameo.chatsdk.models.pojos;

public class Message extends CustomData {
    private String id;
    private String author_id;
    private String channel_id;
    private String content;
    private int type;
    private String created_at;
    private String updated_at;
    private String sent_at;
    private String seen_at;
    private boolean is_system_message;

    public Message(String id, String author_id, String channel_id, String content, int type,
                   String created_at, String updated_at, String sent_at, String seen_at) {
        this.id = id;
        this.author_id = author_id;
        this.channel_id = channel_id;
        this.content = content;
        this.type = type;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.sent_at = sent_at;
        this.seen_at = seen_at;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isIs_system_message() {
        return is_system_message;
    }

    public void setIs_system_message(boolean is_system_message) {
        this.is_system_message = is_system_message;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getSent_at() {
        return sent_at;
    }

    public void setSent_at(String sent_at) {
        this.sent_at = sent_at;
    }

    public String getSeen_at() {
        return seen_at;
    }

    public void setSeen_at(String seen_at) {
        this.seen_at = seen_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }


}
