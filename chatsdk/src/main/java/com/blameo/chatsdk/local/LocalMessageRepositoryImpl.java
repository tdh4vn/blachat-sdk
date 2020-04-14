package com.blameo.chatsdk.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.blameo.chatsdk.models.pojos.Message;

import java.util.ArrayList;

public class LocalMessageRepositoryImpl extends SQLiteOpenHelper implements LocalMessageRepository {

    private String MESSAGE_TAG = "MESSAGE_DB";

    public LocalMessageRepositoryImpl(Context context) {
        super(context, Constant.MESSAGE_DB_NAME, null, Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i(MESSAGE_TAG, "Create MESSAGE Table ... ");

        String table = "CREATE TABLE " + Constant.MESSAGE_TABLE_NAME + "("
                + Constant.MESSAGE_ID + " STRING PRIMARY KEY,"
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

        db.execSQL(table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(MESSAGE_TAG, "Drop Table ... ");
        db.execSQL("DROP TABLE IF EXISTS " + Constant.MESSAGE_TABLE_NAME);

        onCreate(db);

    }

    @Override
    public void clearAllLocalMessages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Constant.MESSAGE_TABLE_NAME);
    }

    @Override
    public void addLocalMessage(Message message) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constant.MESSAGE_ID, message.getId());
        values.put(Constant.MESSAGE_AUTHOR_ID, message.getAuthor_id());
        values.put(Constant.MESSAGE_CHANNEL_ID, message.getChannel_id());
        values.put(Constant.MESSAGE_CONTENT, message.getContent());
        values.put(Constant.MESSAGE_TYPE, message.getType());
        values.put(Constant.MESSAGE_CREATED_AT, message.getCreated_at());
        values.put(Constant.MESSAGE_UPDATED_AT, message.getUpdated_at());
        values.put(Constant.MESSAGE_SENT_AT, message.getSent_at());
        values.put(Constant.MESSAGE_SEEN_AT, message.getSeen_at());
        values.put(Constant.MESSAGE_CUSTOM_DATA, message.getCustom_data());

        Log.e("DB", "add local message: " + message.getId() + " author_id: " + message.getAuthor_id() + " channel_id: " + message.getChannel_id());

        int res = (int) db.insertWithOnConflict(Constant.MESSAGE_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (res == -1) {
            this.updateMessage(message);
        }

        db.close();
    }

    @Override
    public int getTotalLocalMessages() {

        String countQuery = "SELECT  * FROM " + Constant.MESSAGE_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    @Override
    public int updateMessage(Message message) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

//        values.put(Constant.CHANNEL_COLUMN_NAME, channel.getName());
//        values.put(Constant.CHANNEL_COLUMN_AVATAR, channel.getAvatar());

        return db.update(Constant.MESSAGE_TABLE_NAME, values, Constant.MESSAGE_ID + " = ?",
                new String[]{String.valueOf(message.getId())});
    }

    @Override
    public Message getMessageByID(String id) {

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

        Message message = new Message(cursor.getString(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3),
                cursor.getInt(4), cursor.getString(5),
                cursor.getString(6), cursor.getString(7), cursor.getString(8));
        message.setCustom_data(cursor.getString(9));

        return message;
    }

    @Override
    public boolean checkIfMessageIsExist(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + Constant.MESSAGE_TABLE_NAME + " where " + Constant.MESSAGE_ID + " = " + id;
        Cursor cursor = db.rawQuery(Query, null);
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
    public void exportMessageDB() {
        Log.e(MESSAGE_TAG, "ID       AUTHOR_ID           CHANNEL_ID");
        ArrayList<Message> messages = getAllLocalMessages();
        Log.e(MESSAGE_TAG, "total of all messages: " + messages.size());
        for (Message c : messages) {
            Log.e(MESSAGE_TAG, "" + c.getId() + "       " + c.getAuthor_id() + "        " + c.getChannel_id());
        }
    }

    @Override
    public ArrayList<Message> getAllLocalMessages() {
        ArrayList<Message> messages = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constant.MESSAGE_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) return messages;

        do {
            Message message = new Message(cursor.getString(0),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getInt(4), cursor.getString(5),
                    cursor.getString(6), cursor.getString(7), cursor.getString(8));
            message.setCustom_data(cursor.getString(9));

            messages.add(message);
        } while (cursor.moveToNext());
        return messages;
    }

    @Override
    public ArrayList<Message> getAllMessagesInChannel(String channelId) {
        ArrayList<Message> messages = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constant.MESSAGE_TABLE_NAME
                + " where " + Constant.MESSAGE_CHANNEL_ID + " = " + channelId;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) return messages;

        do {
            Message message = new Message(cursor.getString(0),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getInt(4), cursor.getString(5),
                    cursor.getString(6), cursor.getString(7), cursor.getString(8));
            message.setCustom_data(cursor.getString(9));

            messages.add(message);
        } while (cursor.moveToNext());
        return messages;
    }
}
