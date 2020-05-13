package com.blameo.chatsdk.models.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.blameo.chatsdk.repositories.local.Constant;

import java.util.Date;

@Entity(
        tableName = Constant.REACT_MESSAGE_TABLE_NAME,
        primaryKeys = {
                Constant.REACT_MESSAGE_ID,
                Constant.REACT_USER_ID,
                Constant.REACT_TYPE
        }
)
public class UserReactMessage {

    public static final int SEEN = 1;
    public static final int RECEIVE = 2;


    @ColumnInfo(name = Constant.REACT_MESSAGE_ID)
    private String messageId;

    @ColumnInfo(name = Constant.REACT_USER_ID)
    private String userId;

    @ColumnInfo(name = Constant.REACT_TYPE)
    private int type;

    @ColumnInfo(name = Constant.REACT_DATE)
    private Date date;

    public UserReactMessage(String messageId, String userId, int type, Date date) {
        this.messageId = messageId;
        this.userId = userId;
        this.type = type;
        this.date = date;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
