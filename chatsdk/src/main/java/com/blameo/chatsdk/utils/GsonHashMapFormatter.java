package com.blameo.chatsdk.utils;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;

public class GsonHashMapFormatter implements JsonDeserializer<HashMap> {
    @Override
    public HashMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String data = json.getAsString();
        Log.e("CCCC", ""+data);
        return data == null ? null : GsonUtil.jsonToMap(data);
    }
}
