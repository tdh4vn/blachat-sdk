package com.blameo.chatsdk.repositories.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteConnection extends SQLiteOpenHelper {

    private static SQLiteConnection sqLiteConnection = null;

    public static SQLiteConnection getInstance(@Nullable Context context) {
        if(sqLiteConnection == null) {
            synchronized (SQLiteConnection.class){
                if (sqLiteConnection == null) {
                    sqLiteConnection = new SQLiteConnection(context);
                }
            }
        }

        return sqLiteConnection;
    }

    private SQLiteConnection(@Nullable Context context) {
        super(context, Constant.DB_FILE_NAME, null, Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(LocalUserRepositoryImpl.CREATE_SCRIPT);
        sqLiteDatabase.execSQL(LocalMessageRepositoryImpl.CREATE_SCRIPT);
        sqLiteDatabase.execSQL(LocalChannelRepositoryImpl.CREATE_SCRIPT);
        sqLiteDatabase.execSQL(LocalUserInChannelRepositoryImpl.CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(LocalUserInChannelRepositoryImpl.DROP_SCRIPT);
        sqLiteDatabase.execSQL(LocalChannelRepositoryImpl.DROP_SCRIPT);
        sqLiteDatabase.execSQL(LocalMessageRepositoryImpl.DROP_SCRIPT);
        sqLiteDatabase.execSQL(LocalUserRepositoryImpl.DROP_SCRIPT);

        onCreate(sqLiteDatabase);
    }
}
