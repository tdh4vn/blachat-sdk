package com.blameo.chatsdk.repositories.local;

import android.util.Log;

import androidx.room.TypeConverter;

import com.blameo.chatsdk.utils.GsonUtil;

import java.util.Date;
import java.util.HashMap;

public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        if(value == null)   return null;
        if(value > 10000000000L)
            return new Date(value);
        return new Date(value * 1000);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static HashMap<String, Object> stringToHashMap(String data) {
        return GsonUtil.jsonToMap(data);
    }

    @TypeConverter
    public static String hashMapToString(HashMap<String, Object> hashMap){
        return hashMap == null ? "" : GsonUtil.mapToJSON(hashMap);
    }
}
