package com.blameo.chatsdk.models.bla;


import com.blameo.chatsdk.models.entities.Channel;
import com.blameo.chatsdk.models.entities.Message;
import java.util.Date;
import java.util.HashMap;

public class BlaChannel extends Channel {

    private BlaMessage lastMessage;

    public BlaChannel(Channel channel, Message lastMessage) {
        super(channel.getId(),
                channel.getName(),
                channel.getAvatar(),
                channel.getType(),
                channel.getUpdatedAt(),
                channel.getCreatedAt(),
                channel.getLastMessageId(),
                channel.getCustomData());
        if (lastMessage != null) {
            this.lastMessage = new BlaMessage(lastMessage);
        }
    }

    public BlaChannel(Channel channel) {
        super(channel.getId(),
                channel.getName(),
                channel.getAvatar(),
                channel.getType(),
                channel.getUpdatedAt(),
                channel.getCreatedAt(),
                channel.getLastMessageId(),
                channel.getCustomData());
        if (channel.getLastMessages() != null && !channel.getLastMessages().isEmpty()) {
            this.lastMessage = new BlaMessage(
                    channel.getLastMessages().get(channel.getLastMessages().size() - 1)
            );
        }
    }

    public BlaChannel(String id, String name, String avatar, int type, Date updatedAt, Date createdAt, String lastMessageId, HashMap<String, Object> customData, BlaMessage lastMessage) {
        super(id, name, avatar, type, updatedAt, createdAt, lastMessageId, customData);
        this.lastMessage = lastMessage;
    }

    public BlaMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(BlaMessage lastMessage) {
        this.lastMessage = lastMessage;
    }
}
