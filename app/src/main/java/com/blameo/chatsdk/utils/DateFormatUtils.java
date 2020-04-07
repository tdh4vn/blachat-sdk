package com.blameo.chatsdk.utils;

import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatUtils {

    private static DateFormatUtils instance;

    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
    private PrettyTime prettyTime;
    private DisplayImageOptions options;

    private DateFormatUtils() {

        if (prettyTime == null) {
            prettyTime = new PrettyTime(new Locale("vi"));
        }
    }

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

    public String getTimeAgo(Date date) {

        String ago = prettyTime.format(date);
        if (ago.contains("khắc")) {
            return "vừa xong";
        } else if (ago.contains("cách đây ")) {
            return ago.replace("cách đây ", "");
        }
        return prettyTime.format(date);
    }
}
