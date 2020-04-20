package com.blameo.chatsdk.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.blameo.chatsdk.models.pojos.User;

import java.util.ArrayList;

public class LocalUserRepositoryImpl extends SQLiteOpenHelper implements LocalUserRepository {

    private String TAG = "USER_DB";

    public LocalUserRepositoryImpl(Context context) {
        super(context, Constant.USER_DB_NAME, null, Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i(TAG, "Create USER Table ... ");

        String table = "CREATE TABLE " + Constant.USER_TABLE_NAME + "("
                + Constant.USER_ID + " STRING PRIMARY KEY,"
                + Constant.USER_NAME + " TEXT,"
                + Constant.USER_AVATAR + " TEXT,"
                + Constant.USER_CONNECTION_STATUS + " TEXT,"
                + Constant.USER_LAST_ACTIVE_AT + " TEXT,"
                + Constant.USER_CUSTOM_DATA + " TEXT"
                + ")";

        db.execSQL(table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(TAG, "Drop Table ... ");
        db.execSQL("DROP TABLE IF EXISTS " + Constant.USER_TABLE_NAME);

        onCreate(db);

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
        values.put(Constant.USER_CONNECTION_STATUS, user.getConnection_status());
        values.put(Constant.USER_LAST_ACTIVE_AT, user.getLast_active_at());
        values.put(Constant.USER_CUSTOM_DATA, user.getCustom_data());

        Log.e("DB", "add local user: " + user.getId() + " name: " + user.getName() + " avatar: " + user.getAvatar());

        int res = (int) db.insertWithOnConflict(Constant.USER_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (res == -1) {
            this.updateUser(user);
        }

        db.close();
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
        values.put(Constant.USER_CONNECTION_STATUS, user.getConnection_status());
        values.put(Constant.USER_LAST_ACTIVE_AT, user.getLast_active_at());

        // updating row
        return db.update(Constant.USER_TABLE_NAME, values, Constant.USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    @Override
    public User getUserByID(String id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constant.USER_TABLE_NAME, new String[]{Constant.USER_ID,
                        Constant.USER_NAME, Constant.USER_AVATAR,
                        Constant.USER_CONNECTION_STATUS, Constant.USER_LAST_ACTIVE_AT,
                        Constant.USER_CUSTOM_DATA}
                , Constant.USER_ID + "=?",
                new String[]{id}, null, null, null, null);
        User user = null;

        if (cursor != null && cursor.moveToFirst()){

            user = new User(cursor.getString(0),
                    cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4));
            user.setCustom_data(cursor.getString(5));
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
        db.close();
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

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) return users;

        do {
            User user = new User(cursor.getString(0),
                    cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4));
            user.setCustom_data(cursor.getString(5));

            users.add(user);
        } while (cursor.moveToNext());
        return users;
    }
}
