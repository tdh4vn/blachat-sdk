package com.blameo.chatsdk.repositories.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.blameo.chatsdk.models.pojos.Channel;
import com.blameo.chatsdk.utils.ChatSdkDateFormatUtil;

import java.text.ParseException;
import java.util.ArrayList;

public class LocalChannelRepositoryImpl extends LocalRepository implements LocalChannelRepository {

    private String CHANNEL_TAG = "CHANNEL_DB";

    static final String CREATE_SCRIPT = "CREATE TABLE " + Constant.CHANNEL_TABLE_NAME + "("
            + Constant.CHANNEL_ID + " TEXT PRIMARY KEY,"
            + Constant.CHANNEL_COLUMN_NAME + " TEXT,"
            + Constant.CHANNEL_COLUMN_AVATAR + " TEXT,"
            + Constant.CHANNEL_TYPE + " INTEGER,"
            + Constant.CHANNEL_COLUMN_UPDATED_AT + " TEXT,"
            + Constant.CHANNEL_COLUMN_CREATED_AT + " TEXT,"
            + Constant.CHANNEL_LAST_MESSAGE_ID + " TEXT"
            + ")";

    static final String DROP_SCRIPT = "DROP TABLE IF EXISTS " + Constant.CHANNEL_TABLE_NAME;

    public LocalChannelRepositoryImpl(Context context) {
        super(context);
    }

    @Override
    public void clearAllLocalChannels() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Constant.CHANNEL_TABLE_NAME);
    }

    @Override
    public void updateLastMessage(String channelId, String messageId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.CHANNEL_LAST_MESSAGE_ID, messageId);

        db.update(Constant.CHANNEL_TABLE_NAME, values, Constant.CHANNEL_ID + " = ?",
                new String[]{String.valueOf(channelId)});
    }

    @Override
    public void addLocalChannel(Channel channel) {

        SQLiteDatabase db = this.getWritableDatabase();
        String last_id = null;

        ContentValues values = new ContentValues();
        values.put(Constant.CHANNEL_ID, channel.getId());
        values.put(Constant.CHANNEL_COLUMN_NAME, channel.getName());
        values.put(Constant.CHANNEL_COLUMN_AVATAR, channel.getAvatar());
        values.put(Constant.CHANNEL_TYPE, channel.getType());
        values.put(Constant.CHANNEL_COLUMN_UPDATED_AT, channel.getUpdatedAtString());
        values.put(Constant.CHANNEL_COLUMN_CREATED_AT, channel.getCreatedAtString());
        if(channel.getLastMessage() != null) {
            last_id = channel.getLastMessage().getId();
            values.put(Constant.CHANNEL_LAST_MESSAGE_ID, channel.getLastMessage().getId());
        }

        Log.e("DB", "add local channel: " + channel.getId() + " name: " + channel.getName() + " last_id: " + last_id);

        int res = (int) db.insertWithOnConflict(Constant.CHANNEL_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (res == -1) {
            this.updateChannel(channel);
        }

    }

    @Override
    public int getTotalLocalChannels() {

        String countQuery = "SELECT  * FROM " + Constant.CHANNEL_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    @Override
    public int updateChannel(Channel channel) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Constant.CHANNEL_COLUMN_NAME, channel.getName());
        values.put(Constant.CHANNEL_COLUMN_AVATAR, channel.getAvatar());

        // updating row
        return db.update(Constant.CHANNEL_TABLE_NAME, values, Constant.CHANNEL_ID + " = ?",
                new String[]{String.valueOf(channel.getId())});
    }

    @Override
    public Channel getChannelByID(String id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constant.CHANNEL_TABLE_NAME, new String[]{Constant.CHANNEL_ID,
                        Constant.CHANNEL_COLUMN_NAME, Constant.CHANNEL_COLUMN_AVATAR,
                        Constant.CHANNEL_TYPE, Constant.CHANNEL_COLUMN_UPDATED_AT,
                        Constant.CHANNEL_COLUMN_CREATED_AT, Constant.CHANNEL_LAST_MESSAGE_ID}
                , Constant.CHANNEL_ID + "=?",
                new String[]{id}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            if(cursor.getCount() == 0) return null;

            Channel channel = null;
            try {
                channel = new Channel(cursor);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return channel;
        }

        return null;
    }

    @Override
    public boolean checkIfChannelIsExist(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from " + Constant.CHANNEL_TABLE_NAME + " where " + Constant.CHANNEL_ID + " = " + id;
        Log.i("qqqqabdasdc", ""+query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    @Override
    public void deleteChannelByID(String channelId) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constant.CHANNEL_TABLE_NAME, Constant.CHANNEL_ID + " = ?",
                new String[]{channelId});
    }

    @Override
    public void exportChannelDB() {
        Log.e("DB", "ID       NAME           LAST_ID");
        ArrayList<Channel> channels = getChannels();
        Log.e("DB", "total channels: " + channels.size());
        for (Channel c : channels) {
            Log.e("DB", "" + c.getId() + "       " + c.getName() + "        " + c.getLastMessageId());
        }
    }

    @Override
    public ArrayList<Channel> getChannels() {
        ArrayList<Channel> channels = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constant.CHANNEL_TABLE_NAME + " ORDER BY "+ Constant.CHANNEL_ID + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) return channels;

        do {
            Channel channel = null;
            try {
                channel = new Channel(cursor);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            channels.add(channel);
        } while (cursor.moveToNext());

        for(Channel c : channels){
            Log.e("123", ""+c.getName() + " "+c.getCreatedAtString());
        }
        return channels;
    }
}
