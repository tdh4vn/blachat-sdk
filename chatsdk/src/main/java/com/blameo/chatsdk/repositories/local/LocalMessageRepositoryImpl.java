package com.blameo.chatsdk.repositories.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.blameo.chatsdk.models.pojos.Message;
import com.blameo.chatsdk.utils.ChatSdkDateFormatUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class LocalMessageRepositoryImpl extends LocalRepository implements LocalMessageRepository {

    private String MESSAGE_TAG = "MESSAGE_DB";

    static final String CREATE_SCRIPT = "CREATE TABLE " + Constant.MESSAGE_TABLE_NAME + "("
            + Constant.MESSAGE_ID + " TEXT,"
            + Constant.MESSAGE_AUTHOR_ID + " TEXT,"
            + Constant.MESSAGE_CHANNEL_ID + " TEXT,"
            + Constant.MESSAGE_CONTENT + " TEXT,"
            + Constant.MESSAGE_TYPE + " INTEGER,"
            + Constant.MESSAGE_CREATED_AT + " TEXT,"
            + Constant.MESSAGE_UPDATED_AT + " TEXT,"
            + Constant.MESSAGE_SENT_AT + " TEXT,"
            + Constant.MESSAGE_SEEN_AT + " TEXT,"
            + Constant.MESSAGE_CUSTOM_DATA + " TEXT"
            + ")";

    static final String DROP_SCRIPT = "DROP TABLE IF EXISTS " + Constant.MESSAGE_TABLE_NAME;

    public LocalMessageRepositoryImpl(Context context) {
        super(context);
    }


    @Override
    public void clearAllLocalMessages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Constant.MESSAGE_TABLE_NAME);
    }

    @Override
    public void updateStatusMessage(String messageId, String attr) throws ParseException {

        if(TextUtils.isEmpty(messageId))  return;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(attr, ChatSdkDateFormatUtil.getCurrentTimeUTC());

        exportMessageDB();

        db.update(Constant.MESSAGE_TABLE_NAME, values, Constant.MESSAGE_ID + " = ?",
                new String[]{messageId});
    }

    @Override
    public ArrayList<Message> getUnsentMessage() throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Message> messagesUnsent = new ArrayList<>();

        String query = String.format("SELECT * FROM %s WHERE %s IS NULL", Constant.MESSAGE_TABLE_NAME, Constant.MESSAGE_SENT_AT);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if(cursor.getCount() == 0)  return messagesUnsent;
            while (cursor.moveToNext()) {
                Message message = new Message(cursor);
                messagesUnsent.add(message);
            }
            cursor.close();
        }
        return messagesUnsent;
    }

    @Override
    public void addLocalMessage(Message message) {

        SQLiteDatabase db = this.getWritableDatabase();

        if(checkIfMessageIsExist(message.getId()))  return;

        ContentValues values = new ContentValues();
        values.put(Constant.MESSAGE_ID, message.getId());
        values.put(Constant.MESSAGE_AUTHOR_ID, message.getAuthorId());
        values.put(Constant.MESSAGE_CHANNEL_ID, message.getChannelId());
        values.put(Constant.MESSAGE_CONTENT, message.getContent());
        values.put(Constant.MESSAGE_TYPE, message.getType());
        values.put(Constant.MESSAGE_CREATED_AT, message.getCreatedAtString());
        values.put(Constant.MESSAGE_UPDATED_AT, message.getUpdatedAtString());
        values.put(Constant.MESSAGE_SENT_AT, message.getSentAtString());
        values.put(Constant.MESSAGE_SEEN_AT, message.getSeenAtString());
        values.put(Constant.MESSAGE_CUSTOM_DATA, message.getCustomDataString());

        Log.e("DB", "add local message: " + message.getId() + " author_id: " + message.getAuthorId() + " channel_id: " + message.getChannelId());

        db.insertWithOnConflict(Constant.MESSAGE_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        db.close();
    }

    @Override
    public int getTotalLocalMessages() {

        String countQuery = "SELECT * FROM " + Constant.MESSAGE_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    @Override
    public int updateMessage(String temID, Message message) {
        String mID = TextUtils.isEmpty(temID) ? message.getId() : temID;

        if(TextUtils.isEmpty(message.getId()))  return 0;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        if(!TextUtils.isEmpty(temID))
            values.put(Constant.MESSAGE_ID, message.getId());
        values.put(Constant.MESSAGE_AUTHOR_ID, message.getAuthorId());
        values.put(Constant.MESSAGE_CHANNEL_ID, message.getChannelId());
        values.put(Constant.MESSAGE_CONTENT, message.getContent());
        values.put(Constant.MESSAGE_TYPE, message.getType());
        values.put(Constant.MESSAGE_UPDATED_AT, message.getUpdatedAtString());

        if (!TextUtils.isEmpty(message.getSentAtString())){
            values.put(Constant.MESSAGE_SENT_AT, message.getSentAtString());
        }

        if (!TextUtils.isEmpty(message.getSeenAtString())) {
            values.put(Constant.MESSAGE_SEEN_AT, message.getSeenAtString());
        }

        values.put(Constant.MESSAGE_CUSTOM_DATA, message.getCustomDataString());

        return db.update(Constant.MESSAGE_TABLE_NAME, values, Constant.MESSAGE_ID + " = ?",
                new String[]{mID});
    }

    @Override
    public Message getMessageByID(String id) throws ParseException {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constant.MESSAGE_TABLE_NAME, new String[]{Constant.MESSAGE_ID,
                        Constant.MESSAGE_AUTHOR_ID, Constant.MESSAGE_CHANNEL_ID, Constant.MESSAGE_CONTENT,
                        Constant.MESSAGE_TYPE, Constant.MESSAGE_CREATED_AT,
                        Constant.MESSAGE_UPDATED_AT, Constant.MESSAGE_SENT_AT,
                        Constant.MESSAGE_SEEN_AT, Constant.MESSAGE_CUSTOM_DATA}
                , Constant.MESSAGE_ID + "=?",
                new String[]{id}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if(cursor.getCount() == 0)  return null;

        Message message = new Message(cursor);

        return message;
    }

    @Override
    public boolean checkIfMessageIsExist(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from " + Constant.MESSAGE_TABLE_NAME + " where " + Constant.MESSAGE_ID + " ='" + id + "'";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    @Override
    public void deleteMessageByID(String messageId) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constant.MESSAGE_TABLE_NAME, Constant.MESSAGE_ID + " = ?",
                new String[]{messageId});
        db.close();
    }

    @Override
    public void exportMessageDB() throws ParseException {
        Log.e(MESSAGE_TAG, "ID       AUTHOR_ID           CHANNEL_ID         SEEN_AT         SENT_AT");
        ArrayList<Message> messages = getAllLocalMessages();
        Log.e(MESSAGE_TAG, "total of all messages: " + messages.size());
        for (Message c : messages) {
            Log.e(MESSAGE_TAG, "" + c.getId() + "       " + c.getAuthorId()
                    + "        " + c.getChannelId() + "       " + c.getSeenAtString() + "        " + c.getSentAtString() );
        }
    }

    @Override
    public ArrayList<Message> getAllLocalMessages() throws ParseException {
        ArrayList<Message> messages = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constant.MESSAGE_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor == null || !cursor.moveToFirst()) return messages;

        if (cursor.getCount() == 0) return messages;

        do {
            Message message = new Message(cursor);

            messages.add(message);
        } while (cursor.moveToNext());
        return messages;
    }

    @Override
    public ArrayList<Message> getAllMessagesInChannel(String channelId, String lastId) throws ParseException {
        ArrayList<Message> messages = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constant.MESSAGE_TABLE_NAME
                + " where " + Constant.MESSAGE_CHANNEL_ID + " = '" + channelId + "'"
                + " order by " +Constant.MESSAGE_CREATED_AT
                + " DESC"
                + " limit 20";

        Log.i("asdasda", "id : "+lastId);

        if(lastId.length() > 0){
            Message message = getMessageByID(lastId);

            if(TextUtils.isEmpty(message.getCreatedAtString())){
                message.setCreatedAt(new Date());
            }

            Log.i("ok", "id : "+lastId);
            selectQuery = "SELECT * FROM " + Constant.MESSAGE_TABLE_NAME
                    + " WHERE " + Constant.MESSAGE_CHANNEL_ID + " = '" + channelId + "'"
                    + " AND " + Constant.MESSAGE_CREATED_AT + " <'" + message.getCreatedAtString() + "'"
                    + " ORDER BY " + Constant.MESSAGE_CREATED_AT
                    + " DESC"
                    + " LIMIT 20";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor == null || !cursor.moveToFirst()) return messages;

        if (cursor.getCount() == 0) return messages;

        do {
            Message message = new Message(cursor);

            messages.add(message);
        } while (cursor.moveToNext());

        return messages;
    }
}
