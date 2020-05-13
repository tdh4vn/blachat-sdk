package com.blameo.chatsdk.models.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ChannelWithLastMessage {
    @Embedded
    public Channel channel;
    @Relation(
            parentColumn = "id",
            entityColumn = "last_message_id"
    )
    public Message lastMessage;
}
