package com.blameo.chatsdk.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatSdkDateFormatUtil {

    private static ChatSdkDateFormatUtil instance;

    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public static ChatSdkDateFormatUtil getInstance() {

        if (instance == null)
            instance = new ChatSdkDateFormatUtil();
        return instance;
    }

    public String getTime(String time) {
        Date date = null;
        try {
            date = inputFormat.parse(time);
        } catch (ParseException e) {
            Log.e("DFU", "err: " + e.getMessage() + " " + e.getCause());
            e.printStackTrace();
        }
        return outputFormat.format(date);
    }

    public String getCurrentTimeUTC() {
        return inputFormat.format(new Date());
    }

}
