package com.blameo.chatsdk.repositories.local.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.blameo.chatsdk.models.entities.Message;
import com.blameo.chatsdk.repositories.local.Constant;

import java.util.List;

@Dao
public abstract class MessageDao implements BaseDao<Message> {

    @Query("SELECT * FROM " + Constant.MESSAGE_TABLE_NAME +
            " WHERE " + Constant.MESSAGE_CHANNEL_ID + " = :channelId " +
            " AND " + Constant.MESSAGE_CREATED_AT + " > :lastCreatedAt " +
            " ORDER BY " + Constant.MESSAGE_CREATED_AT + " ASC " +
            " LIMIT :limit")
    public abstract List<Message> getMessagesOfChannel(String channelId, long lastCreatedAt, long limit);

    @Query("SELECT * FROM " + Constant.MESSAGE_TABLE_NAME +
            " WHERE " + Constant.MESSAGE_ID + " = :mId LIMIT 1")
    public abstract Message getMessageById(String mId);

    @Transaction
    public void updateIdMessage(Message oldMessage, Message newMessage) {
        delete(oldMessage);
        insert(newMessage);
    }
}
