package com.blameo.chatsdk.models.entities;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Junction;
import androidx.room.Relation;

import com.blameo.chatsdk.repositories.local.Constant;

import java.util.List;

public class ChannelWithUser {
    @Embedded
    public Channel channel;

    @Relation(
            parentColumn = Constant.CHANNEL_ID,
            entityColumn = Constant.USER_ID,
            associateBy = @Junction(
                    value = UserInChannel.class,
                    parentColumn = Constant.UIC_CHANNEL_ID,
                    entityColumn = Constant.UIC_USER_ID
            )
    )
    public List<User> members;
}
