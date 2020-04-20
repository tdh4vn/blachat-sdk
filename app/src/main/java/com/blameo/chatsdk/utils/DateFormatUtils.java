package com.blameo.chatsdk.utils;

import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtils {

    private static DateFormatUtils instance;

    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss");
    private DisplayImageOptions options;

    public static DateFormatUtils getInstance() {

        if (instance == null)
            instance = new DateFormatUtils();
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
}
