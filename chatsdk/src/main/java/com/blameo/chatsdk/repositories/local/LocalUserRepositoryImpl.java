package com.blameo.chatsdk.repositories.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.blameo.chatsdk.models.pojos.User;

import java.util.ArrayList;

public class LocalUserRepositoryImpl extends LocalRepository implements LocalUserRepository {

    private String TAG = "USER_DB";


    static final String CREATE_SCRIPT = "CREATE TABLE " + Constant.USER_TABLE_NAME + "("
            + Constant.USER_ID + " TEXT PRIMARY KEY,"
            + Constant.USER_NAME + " TEXT,"
            + Constant.USER_AVATAR + " TEXT,"
            + Constant.USER_CONNECTION_STATUS + " TEXT,"
            + Constant.USER_LAST_ACTIVE_AT + " TEXT,"
            + Constant.USER_CUSTOM_DATA + " TEXT"
            + ")";

    static final String DROP_SCRIPT = "DROP TABLE IF EXISTS " + Constant.USER_TABLE_NAME;

    public LocalUserRepositoryImpl(Context context) {
        super(context);
    }

    @Override
    public void clearAllLocalUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Constant.USER_TABLE_NAME);
    }

    @Override
    public void addLocalUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constant.USER_ID, user.getId());
        values.put(Constant.USER_NAME, user.getName());
        values.put(Constant.USER_AVATAR, user.getAvatar());
        values.put(Constant.USER_CONNECTION_STATUS, user.getConnectionStatus());
        values.put(Constant.USER_LAST_ACTIVE_AT, user.getLastActiveAtString());
        values.put(Constant.USER_CUSTOM_DATA, user.getCustomDataString());

        Log.e("DB", "add local user: " + user.getId() + " name: " + user.getName() + " avatar: " + user.getAvatar());

        int res = (int) db.insertWithOnConflict(Constant.USER_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (res == -1) {
            this.updateUser(user);
        }

    }

    @Override
    public int getTotalLocalUsers() {

        String countQuery = "SELECT  * FROM " + Constant.USER_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    @Override
    public int updateUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Constant.USER_NAME, user.getName());
        values.put(Constant.USER_AVATAR, user.getAvatar());
        values.put(Constant.USER_CONNECTION_STATUS, user.getConnectionStatus());
        values.put(Constant.USER_LAST_ACTIVE_AT, user.getLastActiveAtString());

        // updating row
        return db.update(Constant.USER_TABLE_NAME, values, Constant.USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    @Override
    public User getUserByID(String id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constant.USER_TABLE_NAME,
                new String[]{
                        Constant.USER_ID,
                        Constant.USER_NAME, Constant.USER_AVATAR,
                        Constant.USER_CONNECTION_STATUS, Constant.USER_LAST_ACTIVE_AT,
                        Constant.USER_CUSTOM_DATA
                },
                Constant.USER_ID + "=?",
                new String[]{id}, null, null, null, null);
        User user = null;

        if (cursor != null && cursor.moveToFirst()){
            try {
                user = new User(cursor);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return user;
    }

    @Override
    public boolean checkIfUserIsExist(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + Constant.USER_TABLE_NAME + " where " + Constant.USER_ID + " = " + id;
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    @Override
    public void deleteUserByID(String uId) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constant.USER_TABLE_NAME, Constant.USER_ID + " = ?",
                new String[]{uId});
    }

    @Override
    public void exportUserDB() {
        Log.e(TAG, "ID       NAME           AVATAR");
        ArrayList<User> users = getAllUsers();
        Log.e(TAG, "total users: " + users.size());
        for (User c : users) {
            Log.e(TAG, "" + c.getId() + "       " + c.getName() + "        " + c.getAvatar());
        }
    }

    @Override
    public ArrayList<User> getUsersByIds(ArrayList<String> ids) {
        ArrayList<User> users = new ArrayList<>();
        for(String id: ids){
            User user = getUserByID(id);
            if(user != null)    users.add(user);
        }
        return users;
    }

    @Override
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constant.USER_TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) return users;

        do {
            try {
                User user = new User(cursor);users.add(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (cursor.moveToNext());
        return users;
    }
}
