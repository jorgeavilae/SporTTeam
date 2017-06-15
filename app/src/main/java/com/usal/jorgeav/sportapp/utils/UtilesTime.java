package com.usal.jorgeav.sportapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jorge Avila on 15/06/2017.
 */

public class UtilesTime {

    public static String calendarToDate(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return sdf.format(time.getTime());
    }

    public static String calendarToTime(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(time.getTime());
    }

    public static long stringDateToMillis(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        try {
            return sdf.parse(s).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long stringTimeToMillis(String time) {
        int quoteInd = time.indexOf(":");
        int hor = Integer.valueOf(time.substring(0, quoteInd));
        int min = Integer.valueOf(time.substring(++quoteInd, time.length()));
        return (((hor * 60) + min) * 60 * 1000);
    }

    public static String millisToDateTimeString(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yy hh:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    public static long timeStringToMillis(String time) {
        int quoteInd = time.indexOf(":");
        int hor = Integer.valueOf(time.substring(0, quoteInd));
        int min = Integer.valueOf(time.substring(++quoteInd, time.length()));
        return (((hor * 60) + min) * 60 * 1000);
    }

    public static String millisToTimeString(long millis) {
        long min = millis/(60*1000);
        long hor = min/60;
        min -= hor*60;
        return String.format(Locale.getDefault(), "%02d:%02d", hor,min);
    }
}
