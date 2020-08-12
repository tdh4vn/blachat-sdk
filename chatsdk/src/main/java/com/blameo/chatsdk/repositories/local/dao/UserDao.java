package com.blameo.chatsdk.repositories.local.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.blameo.chatsdk.models.entities.User;
import com.blameo.chatsdk.repositories.local.Constant;

import java.util.List;

@Dao
public abstract class UserDao extends BaseDao<User> {

    @Query("SELECT * FROM " + Constant.USER_TABLE_NAME)
    abstract public List<User> getAllUsers();

    @Query("SELECT * FROM " + Constant.USER_TABLE_NAME + " WHERE " + Constant.USER_ID + " = :id")
    abstract public User getUserById(String id);

    @Query("SELECT * FROM " + Constant.USER_TABLE_NAME + " WHERE " + Constant.USER_ID + " IN (:ids)")
    abstract public List<User> getUsersByIds(String[] ids);

}
