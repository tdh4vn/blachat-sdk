package com.blameo.chatsdk.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class GsonUtil {

    private static Gson gson = new Gson();
    private static Type type = new TypeToken<HashMap<String, Object>>(){}.getType();

    public static String mapToJSON(HashMap<String, Object> data)  {
        return gson.toJson(data);
    }

    public static HashMap<String, Object> jsonToMap(String data) {
        return gson.fromJson(data, type);
    }
}
