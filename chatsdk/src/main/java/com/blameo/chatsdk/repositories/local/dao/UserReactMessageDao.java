package com.blameo.chatsdk.repositories.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.blameo.chatsdk.models.entities.MessageWithUserReact;
import com.blameo.chatsdk.models.entities.UserReactMessage;
import com.blameo.chatsdk.repositories.local.Constant;

import java.util.List;

@Dao
public abstract class UserReactMessageDao implements BaseDao<UserReactMessage> {
    @Query("SELECT * FROM " + Constant.REACT_MESSAGE_TABLE_NAME + " WHERE " + Constant.REACT_MESSAGE_ID + " = :mID" )
    public abstract List<UserReactMessage> userReactMessageList(String mID);
}
