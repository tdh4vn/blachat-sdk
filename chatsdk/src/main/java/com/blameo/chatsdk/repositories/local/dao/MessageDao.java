package com.blameo.chatsdk.repositories.local.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.blameo.chatsdk.models.entities.Message;
import com.blameo.chatsdk.models.entities.MessageWithUserReact;
import com.blameo.chatsdk.repositories.local.Constant;

import java.util.List;

@Dao
public abstract class MessageDao extends BaseDao<Message> {

    @Query("SELECT * FROM " + Constant.MESSAGE_TABLE_NAME +
            " WHERE " + Constant.MESSAGE_CHANNEL_ID + " = :channelId " +
            " AND " + Constant.MESSAGE_CREATED_AT + " <= :lastCreatedAt " +
            " ORDER BY " + Constant.MESSAGE_CREATED_AT + " DESC " +
            " LIMIT :limit")
    public abstract List<Message> getMessagesOfChannel(String channelId, long lastCreatedAt, int limit);

    @Query("SELECT * FROM " + Constant.MESSAGE_TABLE_NAME +
            " WHERE " + Constant.MESSAGE_CHANNEL_ID + " = :channelId " +
            " AND " + Constant.MESSAGE_TYPE + " = :messageType" +
            " ORDER BY " + Constant.MESSAGE_CREATED_AT + " DESC")
    public abstract List<Message> getMessagesByType(String channelId, int messageType);

    @Query("SELECT * FROM " + Constant.MESSAGE_TABLE_NAME +
            " WHERE " + Constant.MESSAGE_ID + " = :mId LIMIT 1")
    public abstract Message getMessageById(String mId);

    @Transaction
    public void updateIdMessage(Message oldMessage, Message newMessage) {
        delete(oldMessage);
        insert(newMessage);
    }

    @Query("SELECT * FROM " + Constant.MESSAGE_TABLE_NAME +
            " WHERE " + Constant.MESSAGE_CHANNEL_ID + " = :channelId "+
            " ORDER BY " + Constant.MESSAGE_CREATED_AT + " DESC ")
    public abstract List<Message> getAllMessageInChannel(String channelId);

    @Query("SELECT * FROM "+ Constant.MESSAGE_TABLE_NAME +
            " WHERE " + Constant.MESSAGE_SENT_AT +  " IS NULL")
    public abstract List<Message> getUnSentMessages();

    @Query("SELECT * FROM "+ Constant.MESSAGE_TABLE_NAME
            + " WHERE " + Constant.MESSAGE_ID + " = :id "
        //    + " AND " + Constant.REACT_TYPE + " = :type"
            + " LIMIT 1")
    public abstract MessageWithUserReact getUserReactMessageByID(String id);


}
