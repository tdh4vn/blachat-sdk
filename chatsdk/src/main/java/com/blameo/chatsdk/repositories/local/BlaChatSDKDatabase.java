package com.blameo.chatsdk.repositories.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.blameo.chatsdk.models.entities.Channel;
import com.blameo.chatsdk.models.entities.Message;
import com.blameo.chatsdk.models.entities.User;
import com.blameo.chatsdk.models.entities.UserInChannel;
import com.blameo.chatsdk.models.entities.UserReactMessage;
import com.blameo.chatsdk.repositories.local.dao.ChannelDao;
import com.blameo.chatsdk.repositories.local.dao.MessageDao;
import com.blameo.chatsdk.repositories.local.dao.UserDao;
import com.blameo.chatsdk.repositories.local.dao.UserInChannelDao;
import com.blameo.chatsdk.repositories.local.dao.UserReactMessageDao;

@Database(entities = {
        User.class,
        Message.class,
        Channel.class,
        UserInChannel.class,
        UserReactMessage.class
}, version = Constant.DATABASE_VERSION)
public abstract class BlaChatSDKDatabase extends RoomDatabase {
    private static final String DB_NAME = Constant.DB_FILE_NAME;

    private static BlaChatSDKDatabase instance;

    public static synchronized BlaChatSDKDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), BlaChatSDKDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
            return instance;
        }

        return instance;
    }

    public abstract ChannelDao channelDao();

    public abstract MessageDao messageDao();

    public abstract UserDao userDao();

    public abstract UserInChannelDao userInChannelDao();

    public abstract UserReactMessageDao userReactMessageDao();

}
