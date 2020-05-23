package com.blameo.chatsdk.utils;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

public class GsonDateFormatter implements JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        long epoch = json.getAsLong();
        if(epoch > 10000000000L)
            return new Date(epoch);
        return new Date(epoch * 1000);
    }
}
