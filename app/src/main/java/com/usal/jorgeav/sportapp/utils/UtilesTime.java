package com.usal.jorgeav.sportapp.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Clase con métodos auxiliares, invocados desde varios puntos de la aplicación, que proveen de
 * funcionalidad útil para transformar fechas y horas de milisegundos a cadena de texto, y
 * viceversa.
 */
public class UtilesTime {

    /**
     * Dada una fecha en texto con el formato "31/01/19", la transforma a milisegundos
     *
     * @param dateStr fecha en texto con el formato "31/01/19"
     * @return fecha en milisegundos
     */
    public static Long stringDateToMillis(String dateStr) {
        if (dateStr == null || TextUtils.isEmpty(dateStr)) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        try {
            return sdf.parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Dada una hora en texto con el formato "12:00", la transforma a milisegundos
     *
     * @param timeStr hora en texto con el formato "12:00"
     * @return hora en milisegundos
     */
    public static Long stringTimeToMillis(String timeStr) {
        if (timeStr == null || TextUtils.isEmpty(timeStr)) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return sdf.parse(timeStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Dada una fecha y hora en milisegundos, la transforma a texto con el formato "01 Enero 12:00"
     *
     * @param millis fecha y hora en milisegundos
     * @return fecha y hora con la forma "01 Enero 12:00"
     */
    public static String millisToDateTimeString(long millis) {
        if (millis <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM\tHH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    /**
     * Dada una fecha y hora en milisegundos, la transforma a texto con el formato "01 Ene 12:00".
     * Usado para las celdas del widget del launcher, más pequeñas.
     *
     * @param millis fecha y hora en milisegundos
     * @return fecha y hora con la forma "01 Ene 12:00"
     * @see com.usal.jorgeav.sportapp.widget.EventsAppWidget
     */
    public static String millisToDateTimeWidgetString(long millis) {
        if (millis <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM\tHH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    /**
     * Dada una hora en milisegundos, la transforma a texto con el formato "12:00"
     *
     * @param millis hora en milisegundos
     * @return hora con la forma "12:00"
     */
    public static String millisToTimeString(long millis) {
        if (millis < 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    /**
     * Dada una fecha en milisegundos, la transforma a texto con el formato "31/01/19"
     *
     * @param millis fecha en milisegundos
     * @return fecha con la forma "31/01/19"
     */
    public static String millisToDateString(long millis) {
        if (millis <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    /**
     * Dada una fecha en milisegundos, la transforma a texto con el formato "01/Enero"
     *
     * @param millis fecha en milisegundos
     * @return fecha con la forma "01/Enero"
     */
    public static String millisToDateStringShort(long millis) {
        if (millis <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}
