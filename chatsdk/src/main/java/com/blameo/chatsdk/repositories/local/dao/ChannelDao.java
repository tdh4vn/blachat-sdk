package com.blameo.chatsdk.repositories.local.dao;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Query;
import androidx.room.Update;

import com.blameo.chatsdk.models.entities.Channel;
import com.blameo.chatsdk.models.entities.ChannelWithLastMessage;
import com.blameo.chatsdk.models.entities.ChannelWithUser;
import com.blameo.chatsdk.repositories.local.Constant;

import java.util.List;

@Dao
public abstract class ChannelDao implements BaseDao<Channel> {
    @Query("SELECT * FROM " + Constant.CHANNEL_TABLE_NAME +
            " WHERE " + Constant.CHANNEL_UPDATED_AT + " < :lastUpdate" +
            " ORDER BY " + Constant.CHANNEL_UPDATED_AT + " DESC " +
            " LIMIT :limit")
    public abstract List<Channel> getChannels(long lastUpdate, int limit);

    @Query("SELECT * FROM " + Constant.CHANNEL_TABLE_NAME +
            " WHERE " + Constant.CHANNEL_UPDATED_AT + " < :lastUpdate" +
            " ORDER BY " + Constant.CHANNEL_UPDATED_AT + " DESC " +
            " LIMIT :limit")
    public abstract List<ChannelWithLastMessage> getChannelsWithLastMessage(long lastUpdate, int limit);

    @Query("SELECT * FROM " + Constant.CHANNEL_TABLE_NAME +
            " WHERE " + Constant.CHANNEL_ID + " = :cId")
    public abstract Channel getChannelById(String cId);

    @Query("SELECT * FROM " + Constant.CHANNEL_TABLE_NAME +
            " WHERE " + Constant.CHANNEL_ID + " = :cId")
    public abstract ChannelWithLastMessage getChannelWithLastMessageById(String cId);

    @Query("SELECT * FROM " + Constant.CHANNEL_TABLE_NAME +
            " WHERE " + Constant.CHANNEL_ID + " = :cId")
    public abstract ChannelWithUser getChannelWithUserById(String cId);

    @Update(entity = Channel.class)
    public abstract void updateLastMessage(UpdateLastMessageOfChannel data);

    public void saveChannel(List<Channel> channels) {

        //Inject last message id to save

        for (Channel channel: channels) {
            if (channel.getLastMessages() != null && channel.getLastMessages().size() > 0) {
                channel.setLastMessageId(channel.getLastMessages().get(0).getId());
            }
        }

        this.insertMany(channels);

    }

    @Entity
    public static class UpdateLastMessageOfChannel {
        @ColumnInfo(name = Constant.CHANNEL_ID)
        private String channelId;

        @ColumnInfo(name = Constant.CHANNEL_LAST_MESSAGE_ID)
        private String lastMessageId;

        public UpdateLastMessageOfChannel(String channelId, String lastMessageId) {
            this.channelId = channelId;
            this.lastMessageId = lastMessageId;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getLastMessageId() {
            return lastMessageId;
        }

        public void setLastMessageId(String lastMessageId) {
            this.lastMessageId = lastMessageId;
        }
    }

}
