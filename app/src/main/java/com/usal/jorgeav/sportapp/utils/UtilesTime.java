package com.usal.jorgeav.sportapp.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        if (s == null || TextUtils.isEmpty(s)) return 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        try {
            return sdf.parse(s).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long stringTimeToMillis(String time) throws ParseException {
        //TODO esto no esta bien (edit event)
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.parse(time).getTime();
    }

    public static String millisToDateTimeString(long millis) {
        if (millis <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yy hh:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    public static String millisToTimeString(long millis) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(cl.getTime());
    }

    public static String millisToDateString(long millis) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return sdf.format(cl.getTime());
    }
}
