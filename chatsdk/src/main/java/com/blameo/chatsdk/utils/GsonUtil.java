package com.blameo.chatsdk.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GsonUtil {

    private static Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonDateFormatter()).create();
    private static Type type = new TypeToken<HashMap<String, Object>>(){}.getType();

    public static String mapToJSON(HashMap<String, Object> data)  {
        if (data == null)   return null;
        return gson.toJson(data);
    }

    public static String mapToJSON(Map<String, Object> data)  {
        if (data == null)   return null;
        return gson.toJson(data);
    }

    public static HashMap<String, Object> jsonToMap(String data) {
        return gson.fromJson(data, type);
    }
}
