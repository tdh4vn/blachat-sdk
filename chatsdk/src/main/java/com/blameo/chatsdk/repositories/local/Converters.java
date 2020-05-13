package com.blameo.chatsdk.repositories.local;

import androidx.room.TypeConverter;

import com.blameo.chatsdk.utils.GsonUtil;

import java.util.Date;
import java.util.HashMap;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
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
        return GsonUtil.mapToJSON(hashMap);
    }
}
