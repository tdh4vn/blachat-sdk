package com.blameo.chatsdk.models.bla;


import com.blameo.chatsdk.models.entities.Channel;
import com.blameo.chatsdk.models.entities.Message;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.HashMap;

public class BlaChannel extends Channel {

    private BlaMessage lastMessage;

    @SerializedName("numberMessageUnread")
    private String numberMessageUnread;

    public BlaChannel(Channel channel, Message lastMessage) {
        super(channel.getId(),
                channel.getName(),
                channel.getAvatar(),
                channel.getType(),
                channel.getUpdatedAt(),
                channel.getCreatedAt(),
                channel.getLastMessageId(),
                channel.getCustomData(),
                channel.getUnreadMessages());

        if (lastMessage != null) {
            this.lastMessage = new BlaMessage(lastMessage);
        }
        this.numberMessageUnread = channel.getUnreadMessages() >= 20? "20+": ""+channel.getUnreadMessages();
    }

    public BlaChannel(Channel channel) {
        super(channel.getId(),
                channel.getName(),
                channel.getAvatar(),
                channel.getType(),
                channel.getUpdatedAt(),
                channel.getCreatedAt(),
                channel.getLastMessageId(),
                channel.getCustomData(),
                channel.getUnreadMessages());
        if (channel.getLastMessages() != null && !channel.getLastMessages().isEmpty()) {
            this.lastMessage = new BlaMessage(
                    channel.getLastMessages().get(channel.getLastMessages().size() - 1)
            );
        }

        this.numberMessageUnread = channel.getUnreadMessages() >= 20? "20+": ""+channel.getUnreadMessages();


    }

    public BlaChannel(String id, String name, String avatar, int type, Date updatedAt, Date createdAt,
                      String lastMessageId, HashMap<String, Object> customData, BlaMessage lastMessage, int unreadMessages) {
        super(id, name, avatar, type, updatedAt, createdAt, lastMessageId, customData, unreadMessages);
        this.lastMessage = lastMessage;
        this.numberMessageUnread = unreadMessages >= 20? "20+": ""+unreadMessages;
    }

    public BlaMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(BlaMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setNumberMessageUnread(String numberMessageUnread) {
        this.numberMessageUnread = numberMessageUnread;
    }

    public String getNumberMessageUnread() {
        return numberMessageUnread;
    }
}
