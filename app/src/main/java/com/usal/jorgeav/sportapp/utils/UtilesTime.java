package com.usal.jorgeav.sportapp.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Jorge Avila on 15/06/2017.
 */

public class UtilesTime {

    public static Long stringDateToMillis(String dateStr) {
        if (dateStr == null || TextUtils.isEmpty(dateStr)) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GTM")); /* https://stackoverflow.com/a/14400005/4235666 */
        try {
            return sdf.parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long stringTimeToMillis(String timeStr) {
        if (timeStr == null || TextUtils.isEmpty(timeStr)) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GTM")); /* https://stackoverflow.com/a/14400005/4235666 */
        try {
            return sdf.parse(timeStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String millisToDateTimeString(long millis) {
        if (millis <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yy hh:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GTM")); /* https://stackoverflow.com/a/14400005/4235666 */
        return sdf.format(new Date(millis));
    }

    public static String millisToTimeString(long millis) {
        if (millis < 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GTM")); /* https://stackoverflow.com/a/14400005/4235666 */
        return sdf.format(new Date(millis));
    }

    public static String millisToDateString(long millis) {
        if (millis <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GTM")); /* https://stackoverflow.com/a/14400005/4235666 */
        return sdf.format(new Date(millis));
    }

    public static String millisToDateStringShort(long millis) {
        if (millis <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GTM")); /* https://stackoverflow.com/a/14400005/4235666 */
        return sdf.format(new Date(millis));
    }
}
