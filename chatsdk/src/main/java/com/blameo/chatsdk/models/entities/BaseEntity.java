package com.blameo.chatsdk.models.entities;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.blameo.chatsdk.repositories.local.Converters;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseEntity implements Serializable {

    @ColumnInfo(name = "CREATED_AT", defaultValue = "CURRENT_TIMESTAMP")
    @SerializedName(value = "createdAt", alternate = "created_at")
    @TypeConverters(Converters.class)
    private Date createdAt;

    @ColumnInfo(name = "UPDATED_AT", defaultValue = "CURRENT_TIMESTAMP")
    @SerializedName(value = "updatedAt", alternate = "created_at")
    @TypeConverters(Converters.class)
    private Date updatedAt;

    public BaseEntity(Date createdAt, Date updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public BaseEntity() {
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
