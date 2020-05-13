package com.blameo.chatsdk.models.entities;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Junction;
import androidx.room.Relation;

import com.blameo.chatsdk.repositories.local.Constant;

import java.util.List;

@Entity
public class UserWithChannel {
    @Embedded
    public User user;

    @Relation(
            parentColumn = Constant.UIC_USER_ID,
            entityColumn = Constant.UIC_CHANNEL_ID,
            associateBy = @Junction(UserInChannel.class)
    )
    public List<Channel> channels;
}
