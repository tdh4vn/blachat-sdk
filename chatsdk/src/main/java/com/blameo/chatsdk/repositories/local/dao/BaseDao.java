package com.blameo.chatsdk.repositories.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.blameo.chatsdk.repositories.local.Constant;

import java.util.List;

@Dao
public interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(T obj);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMany(List<T> objs);

    @Update()
    void update(T obj);

    @Update()
    void updateMany(List<T> objs);

    @Delete()
    void delete(T obj);

    @Delete()
    void deleteMany(List<T> objs);

}
