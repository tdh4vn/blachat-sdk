package com.blameo.chatsdk.repositories.local.dao;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

import com.blameo.chatsdk.models.entities.UserInChannel;
import com.blameo.chatsdk.repositories.local.Constant;

@Dao
public abstract class UserInChannelDao implements BaseDao<UserInChannel> {
    @Update(entity = UserInChannel.class)
    public abstract void updateLastSeen(UpdateSeen data);

    @Update(entity = UserInChannel.class)
    public abstract void updateLastReceived(UpdateReceived data);

    @Query("SELECT * FROM " + Constant.UIC_TABLE_NAME + " WHERE " + Constant.UIC_CHANNEL_ID + " = :channelId"
    +" AND "+ Constant.UIC_USER_ID +" =:userId")
    abstract public UserInChannel getUserInChannelById(String channelId, String userId);


    public static class UpdateSeen {
        @ColumnInfo(name = Constant.UIC_CHANNEL_ID)
        public String channelId;

        @ColumnInfo(name = Constant.UIC_USER_ID)
        public String userId;

        @ColumnInfo(name = Constant.UIC_LAST_SEEN)
        public long lastSeen;

        public UpdateSeen(String channelId, String userId, long lastSeen) {
            this.channelId = channelId;
            this.userId = userId;
            this.lastSeen = lastSeen;
        }
    }

    public static class UpdateReceived {
        @ColumnInfo(name = Constant.UIC_CHANNEL_ID)
        public String channelId;

        @ColumnInfo(name = Constant.UIC_USER_ID)
        public String userId;

        @ColumnInfo(name = Constant.UIC_LAST_RECEIVE)
        public long lastReceived;

        public UpdateReceived(String channelId, String userId, long lastReceived) {
            this.channelId = channelId;
            this.userId = userId;
            this.lastReceived = lastReceived;
        }
    }

}
