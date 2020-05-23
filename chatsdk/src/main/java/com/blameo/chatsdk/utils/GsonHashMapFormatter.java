package com.blameo.chatsdk.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

public class GsonHashMapFormatter implements JsonDeserializer<HashMap> {
    @Override
    public HashMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        String data = json.getAsString();
        if (TextUtils.isEmpty(data)) return null;
        return GsonUtil.jsonToMap(data);

    }
}
