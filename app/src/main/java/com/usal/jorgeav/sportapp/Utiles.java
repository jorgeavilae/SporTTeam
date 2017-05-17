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
}
