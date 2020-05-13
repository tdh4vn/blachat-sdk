package com.blameo.chatsdk.models.entities;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.blameo.chatsdk.repositories.local.Constant;

import java.util.List;

public class ChannelWithUser {
    @Embedded
    public Channel channel;

    @Relation(
            parentColumn = Constant.UIC_CHANNEL_ID,
            entityColumn = Constant.UIC_USER_ID,
            associateBy = @Junction(UserInChannel.class)
    )
    public List<User> members;
}
