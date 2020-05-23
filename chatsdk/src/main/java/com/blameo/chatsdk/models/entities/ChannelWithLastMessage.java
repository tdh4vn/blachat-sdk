package com.blameo.chatsdk.models.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.blameo.chatsdk.repositories.local.Constant;

public class ChannelWithLastMessage {
    @Embedded
    public Channel channel;

    @Relation(
            parentColumn = Constant.CHANNEL_LAST_MESSAGE_ID,
            entityColumn = Constant.MESSAGE_ID
    )
    public Message lastMessage;
}
