package com.blameo.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.blameo.data.models.pojos.Channel;

import java.util.ArrayList;

public class LocalChannelRepositoryImpl  extends SQLiteOpenHelper implements LocalChannelRepository {

    public LocalChannelRepositoryImpl(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i(Constant.CHANNEL_TAG, "Create CHANNEL Table ... ");

        String table = "CREATE TABLE " + Constant.CHANNEL_TABLE_NAME + "("
                + Constant.CHANNEL_ID + " STRING PRIMARY KEY,"
                + Constant.CHANNEL_COLUMN_NAME + " TEXT,"
                + Constant.CHANNEL_COLUMN_AVATAR + " TEXT,"
                + Constant.CHANNEL_TYPE + " INTEGER,"
                + Constant.CHANNEL_COLUMN_UPDATED_AT + " TEXT,"
                + Constant.CHANNEL_COLUMN_CREATED_AT + " TEXT,"
                + Constant.CHANNEL_LAST_MESSAGE_ID + " TEXT"
                + ")";

        db.execSQL(table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(Constant.CHANNEL_TAG, "Drop Table ... ");
        db.execSQL("DROP TABLE IF EXISTS " + Constant.CHANNEL_TABLE_NAME);

        onCreate(db);

    }

    @Override
    public void clearAllLocalChannels() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Constant.CHANNEL_TABLE_NAME);
    }

    @Override
    public void addLocalChannel(Channel channel) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constant.CHANNEL_ID, channel.getId());
        values.put(Constant.CHANNEL_COLUMN_NAME, channel.getName());
        values.put(Constant.CHANNEL_COLUMN_AVATAR, channel.getAvatar());
        values.put(Constant.CHANNEL_TYPE, channel.getType());
        values.put(Constant.CHANNEL_COLUMN_UPDATED_AT, channel.getUpdated_at());
        values.put(Constant.CHANNEL_COLUMN_CREATED_AT, channel.getCreated_at());
        values.put(Constant.CHANNEL_LAST_MESSAGE_ID, channel.getLast_message_id());

        Log.e("DB", "add local channel: " + channel.getId() + " name: " + channel.getName() + " last_id: " + channel.getLast_message_id());

        int res = (int) db.insertWithOnConflict(Constant.CHANNEL_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (res == -1) {
            this.updateChannel(channel);
        }

        db.close();
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
                        Constant.CHANNEL_TABLE_NAME, Constant.CHANNEL_COLUMN_AVATAR,
                        Constant.CHANNEL_TYPE, Constant.CHANNEL_COLUMN_UPDATED_AT,
                        Constant.CHANNEL_COLUMN_CREATED_AT, Constant.CHANNEL_LAST_MESSAGE_ID}
                , Constant.CHANNEL_ID + "=?",
                new String[]{id}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Channel channel = new Channel(cursor.getString(0),
                cursor.getString(1), cursor.getString(2),
                cursor.getInt(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6));

        return channel;
    }

    @Override
    public boolean checkIfChannelIsExist(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + Constant.CHANNEL_TABLE_NAME + " where " + Constant.CHANNEL_ID + " = " + id;
        Cursor cursor = db.rawQuery(Query, null);
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
        db.close();
    }

    @Override
    public void exportChannelDB() {
        Log.e("DB", "ID       NAME           LAST_ID");
        ArrayList<Channel> channels = getChannels();
        Log.e("DB", "total channels: " + channels.size());
        for (Channel c : channels) {
            Log.e("DB", "" + c.getId() + "       " + c.getName() + "        " + c.getLast_message_id());
        }
    }

    @Override
    public ArrayList<Channel> getChannels() {
        ArrayList<Channel> channels = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constant.CHANNEL_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) return channels;

        do {
            Channel channel = new Channel(cursor.getString(0),
                    cursor.getString(1), cursor.getString(2),
                    cursor.getInt(3), cursor.getString(4),
                    cursor.getString(5), cursor.getString(6));

            channels.add(channel);
        } while (cursor.moveToNext());
        return channels;
    }
}
