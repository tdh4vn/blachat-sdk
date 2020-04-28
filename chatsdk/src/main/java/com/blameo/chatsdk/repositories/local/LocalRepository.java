package com.blameo.chatsdk.repositories.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

public abstract class LocalRepository {

    private SQLiteConnection sqLiteConnection;

    public LocalRepository(@Nullable Context context) {
        sqLiteConnection = SQLiteConnection.getInstance(context);
    }

    public SQLiteDatabase getReadableDatabase() {
        return sqLiteConnection.getReadableDatabase();
    }

    public SQLiteDatabase getWritableDatabase() {
        return sqLiteConnection.getWritableDatabase();
    }
}
