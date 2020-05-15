package com.blameo.chatsdk.utils;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatSdkDateFormatUtil {

    private static ChatSdkDateFormatUtil instance;

    private static SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private static String output = "dd-MM-yyyy HH:mm:ss";

    public static Date parse(String dateString) throws ParseException {
        if(TextUtils.isEmpty(dateString))  return null;
        return inputFormat.parse(dateString);
    }

    public static Date parseToDate(Long date) {
        try {
            return new Date(date);
        } catch (Exception e) {
//            return inputFormat.format(new Date());
            return null;
        }

    }

    public static String getTime(String time) {
        Date date = null;
        try {
            date = inputFormat.parse(time);
        } catch (ParseException e) {
            Log.e("DFU", "err: " + e.getMessage() + " " + e.getCause());
            e.printStackTrace();
        }
        return outputFormat.format(date);
    }

    public static String getCurrentTimeUTC() {
        return inputFormat.format(new Date());
    }

    public static String getPastTimeUTC(){
        return "2000-01-01T00:00:00.000Z";
    }

}
