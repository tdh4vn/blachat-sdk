package com.blameo.chatsdk.repositories.local;

import android.util.Log;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.blameo.chatsdk.utils.GsonUtil;

import java.util.Date;
import java.util.HashMap;

public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        Log.e("BBBB", "val: "+value);
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        Log.e("BBBB", "date: "+date);
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static HashMap<String, Object> stringToHashMap(String data) {
        Log.e("BBBB", "1: "+data);
        return GsonUtil.jsonToMap(data);
    }

    @TypeConverter
    public static String hashMapToString(HashMap<String, Object> hashMap){
        Log.e("BBBB", "2: "+hashMap);
        return hashMap == null ? "" : GsonUtil.mapToJSON(hashMap);
    }
}
