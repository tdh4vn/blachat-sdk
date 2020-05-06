package com.blameo.chatsdk.repositories.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.blameo.chatsdk.models.pojos.Message;
import com.blameo.chatsdk.models.pojos.RemoteUserChannel;
import com.blameo.chatsdk.models.pojos.UserInChannel;

import java.util.ArrayList;

public class LocalUserInChannelRepositoryImpl extends LocalRepository implements LocalUserInChannelRepository {

    private String TAG = "UIC_DB";


    static final String CREATE_SCRIPT = "CREATE TABLE " + Constant.UIC_TABLE_NAME + "("
            + Constant.UIC_ID + " TEXT PRIMARY KEY,"
            + Constant.UIC_CHANNEL_ID + " TEXT,"
            + Constant.UIC_USER_ID + " TEXT,"
            + Constant.UIC_LAST_RECEIVE + " TEXT,"
            + Constant.UIC_LAST_SEEN + " TEXT"
            + ")";

    static final String DROP_SCRIPT = "DROP TABLE IF EXISTS " + Constant.UIC_TABLE_NAME;



    public LocalUserInChannelRepositoryImpl(Context context) {
        super(context);
    }

    @Override
    public void clearAllLocalUIC() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Constant.UIC_TABLE_NAME);
    }

    @Override
    public void saveUserIdsToChannel(String channelId, ArrayList<RemoteUserChannel> rus) {

        for(RemoteUserChannel uc: rus)
            save(channelId, uc);
    }

    public RemoteUserChannel getUIC(String channelId, String userId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constant.UIC_TABLE_NAME, new String[]{Constant.UIC_ID,
                        Constant.UIC_CHANNEL_ID, Constant.UIC_USER_ID,
                        Constant.UIC_LAST_RECEIVE, Constant.UIC_LAST_SEEN}
                , Constant.UIC_ID + "=?",
                new String[]{channelId + userId}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            if(cursor.getCount() == 0) return null;

            RemoteUserChannel channel = new RemoteUserChannel(userId, cursor.getString(3), cursor.getString(4));

            return channel;
        }

        return null;


    }

    @Override
    public void updateUserLastSeenInChannel(String userId, String channelId, Message lastMessage) {

        RemoteUserChannel uic = getUIC(channelId, userId);
        if(uic.getLastSeen().compareTo(lastMessage.getCreatedAtString()) >= 0) return;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.UIC_LAST_SEEN, lastMessage.getCreatedAtString());

        db.update(Constant.UIC_TABLE_NAME, values, Constant.UIC_CHANNEL_ID + " = ?"
                        + " and "+ Constant.UIC_USER_ID + " = ?",
                new String[]{channelId, userId});
    }

    private void save(String cId, RemoteUserChannel uc){

//        Log.e(TAG, "add local uic: " + cId + " uid: " + uc.getMemberId());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constant.UIC_ID, cId + uc.getMemberId());
        values.put(Constant.UIC_CHANNEL_ID, cId);
        values.put(Constant.UIC_USER_ID, uc.getMemberId());
        values.put(Constant.UIC_LAST_RECEIVE, uc.getLastReceive());
        values.put(Constant.UIC_LAST_SEEN, uc.getLastSeen());

        int res = (int) db.insertWithOnConflict(Constant.UIC_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
//        if (res == -1) {
//            Log.i(TAG, "" + uc.getMemberId() +" has already inserted in the channel: "+ cId +" before");
//        }

    }

    @Override
    public int getTotalLocalUIC() {

        String countQuery = "SELECT  * FROM " + Constant.UIC_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    @Override
    public void exportUicDB() {
        Log.e(TAG, "ID       CHANNEL_ID           USER_ID       LAST_SEEN       LAST_RECEIVE");
        ArrayList<UserInChannel> users = getAllUICs();
        Log.e(TAG, "total UICs: " + users.size());
        for (UserInChannel c : users) {
            Log.e(TAG, "" + c.getId() + "       " + c.getChannelId() + "        "
                    + c.getUserId()       +c.getLastSeen() + "      "+ c.getLastReceive());
        }
    }

    @Override
    public void deleteUIC(UserInChannel userInChannel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constant.UIC_TABLE_NAME, Constant.UIC_ID + " = ?",
                new String[]{userInChannel.getChannelId() + userInChannel.getUserId()});
    }

    @Override
    public ArrayList<RemoteUserChannel> getUsersInChannel(String channelId) {
        ArrayList<RemoteUserChannel> uIds = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Constant.UIC_TABLE_NAME
                + " where " + Constant.UIC_CHANNEL_ID + " ='" + channelId + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor == null || !cursor.moveToFirst()) return uIds;

        do {
            RemoteUserChannel uc = new RemoteUserChannel(cursor.getString(2), cursor.getString(3), cursor.getString(4));
            uIds.add(uc);
        } while (cursor.moveToNext());

        return uIds;
    }

    @Override
    public ArrayList<UserInChannel> getAllUICs() {
        ArrayList<UserInChannel> users = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constant.UIC_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) return users;

        do {
            UserInChannel user = new UserInChannel(cursor.getString(0),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));

            users.add(user);
        } while (cursor.moveToNext());
        return users;
    }

    @Override
    public ArrayList<String> getAllChannelIds(String id) {
        ArrayList<String> channels = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constant.UIC_TABLE_NAME
                + " where " + Constant.UIC_USER_ID + "='" + id + "'" + " order by "+Constant.UIC_CHANNEL_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) return channels;

        do {
            channels.add(cursor.getString(1));
        } while (cursor.moveToNext());
        return channels;
    }
}
