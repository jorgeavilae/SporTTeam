package com.usal.jorgeav.sportapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jorge Avila on 17/05/2017.
 */

public class Utiles {

    public static String millisToDateTimeString(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yy\thh:mm", Locale.getDefault());
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
        return String.format(Locale.getDefault(), "%2d:%2d", hor,min);
    }
}
