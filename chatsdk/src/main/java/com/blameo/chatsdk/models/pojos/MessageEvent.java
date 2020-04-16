package com.blameo.chatsdk.models.pojos;

public class MessageEvent {
    public String id;
    public String author_id;
    public String channel_id;
    public String content;
    public int type;
    public String created_at;
    public Boolean is_system_message;

//    public Message eventToMessage(){
//        return new Message(this.id, this.author_id, this.channel_id,
//                this.content, (Integer) this.type, this.created_at, this.is_system_message);
//    }
}
