package com.blameo.data.models.pojos;

import java.util.Date;

public class Message {
    private String id;
    private String channel_id;
    private String author_id;
    private String content;
    private int type;
    private boolean is_system_message;
    private Date created_at;
    private Date sent_at;

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

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getSent_at() {
        return sent_at;
    }

    public void setSent_at(Date sent_at) {
        this.sent_at = sent_at;
    }

    public Date getSeen_at() {
        return seen_at;
    }

    public void setSeen_at(Date seen_at) {
        this.seen_at = seen_at;
    }

    private Date seen_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
