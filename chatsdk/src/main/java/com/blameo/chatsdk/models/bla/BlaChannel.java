package com.blameo.chatsdk.models.bla;

import android.database.Cursor;

import com.blameo.chatsdk.models.pojos.Channel;
import com.blameo.chatsdk.models.pojos.Message;
import com.blameo.chatsdk.utils.GsonUtil;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

public class BlaChannel extends Channel {

    private BlaMessage lastMessage;

    public BlaChannel(Cursor cursor) throws ParseException {
        super(cursor);
    }

    public BlaChannel(String id, String name, String avatar, int type, Date updatedAt, Date createdAt, String lastMessageId) {
        super(id, name, avatar, type, updatedAt, createdAt, lastMessageId);
    }

    public BlaChannel(String id, String name, String avatar, int type, Date updatedAt, Date createdAt, String lastMessageId, Message lastMessage) {
        super(id, name, avatar, type, updatedAt, createdAt, lastMessageId, lastMessage);
    }

    public BlaMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(BlaMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public HashMap<String, Object> getCustomData() {
        return GsonUtil.jsonToMap(customData);
    }

    public void setCustomData(HashMap<String, Object> customData) {
        this.customData = GsonUtil.mapToJSON(customData);
    }
}
