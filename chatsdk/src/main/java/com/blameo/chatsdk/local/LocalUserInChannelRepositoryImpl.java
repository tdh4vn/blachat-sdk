package com.blameo.chatsdk.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.blameo.chatsdk.models.pojos.UserInChannel;

import java.util.ArrayList;

public class LocalUserInChannelRepositoryImpl extends SQLiteOpenHelper implements LocalUserInChannelRepository {

    private String TAG = "UIC_DB";

    public LocalUserInChannelRepositoryImpl(Context context) {
        super(context, Constant.UIC_DB_NAME, null, Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i(TAG, "Create UIC Table ... ");

        String table = "CREATE TABLE " + Constant.UIC_TABLE_NAME + "("
                + Constant.UIC_ID + " STRING PRIMARY KEY,"
                + Constant.UIC_CHANNEL_ID + " TEXT,"
                + Constant.UIC_USER_ID + " TEXT"
                + ")";

        db.execSQL(table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(TAG, "Drop Table ... ");
        db.execSQL("DROP TABLE IF EXISTS " + Constant.UIC_TABLE_NAME);

        onCreate(db);

    }

    @Override
    public void clearAllLocalUIC() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Constant.UIC_TABLE_NAME);
    }

    @Override
    public void saveUserIdsToChannel(String channelId, ArrayList<String> uIds) {

        for(String id: uIds)
            save(channelId, id);
    }

    private void save(String cId, String uId){

        Log.e(TAG, "add local uic: " + cId + " uid: " + uId);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constant.UIC_ID, cId + uId);
        values.put(Constant.UIC_CHANNEL_ID, cId);
        values.put(Constant.UIC_USER_ID, uId);

        int res = (int) db.insertWithOnConflict(Constant.UIC_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (res == -1) {
            Log.i(TAG, "" + uId +" has already inserted in the channel: "+ cId +" before");
        }

        db.close();
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
        Log.e(TAG, "ID       CHANNEL_ID           USER_ID");
        ArrayList<UserInChannel> users = getAllUICs();
        Log.e(TAG, "total UICs: " + users.size());
        for (UserInChannel c : users) {
            Log.e(TAG, "" + c.getId() + "       " + c.getChannelId() + "        " + c.getUserId());
        }
    }

    @Override
    public void deleteUIC(UserInChannel userInChannel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constant.UIC_TABLE_NAME, Constant.UIC_ID + " = ?",
                new String[]{userInChannel.getChannelId() + userInChannel.getUserId()});
        db.close();
    }

    @Override
    public ArrayList<String> getAllUserIdsInChannel(String channelId) {
        ArrayList<String> uIds = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Constant.UIC_TABLE_NAME
                + " where " + Constant.UIC_CHANNEL_ID + " = " + channelId;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) return uIds;

        do {
            uIds.add(cursor.getString(2));
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
                    cursor.getString(1), cursor.getString(2));

            users.add(user);
        } while (cursor.moveToNext());
        return users;
    }
}
