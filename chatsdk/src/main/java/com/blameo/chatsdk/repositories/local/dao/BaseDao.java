package com.blameo.chatsdk.repositories.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.blameo.chatsdk.models.entities.BaseEntity;
import com.blameo.chatsdk.repositories.local.Constant;

import java.util.Date;
import java.util.List;

@Dao
public abstract class BaseDao<T extends BaseEntity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void actualInsert(T obj);

    public void insert(T obj) {
        obj.setCreatedAt(new Date());
        obj.setUpdatedAt(new Date());
        actualInsert(obj);
    }

    public void insertMany(List<T> obj) {
        if (obj != null) {
            for (T t: obj) {
                t.setUpdatedAt(new Date());
                t.setUpdatedAt(new Date());
            }
        }
        actualInsertMany(obj);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void actualInsertMany(List<T> objs);

    @Update()
    abstract void actualUpdate(T obj);

    public void update(T obj) {
        actualUpdate(obj);
    }

    @Update()
    public abstract void actualUpdateMany(List<T> objs);

    public void updateMany(List<T> objs) {

        actualUpdateMany(objs);
    }

    @Delete()
    public abstract void delete(T obj);

    @Delete()
    public abstract void deleteMany(List<T> objs);

}
