package com.usal.jorgeav.sportapp.utils;

import android.content.Context;
import android.database.Cursor;

import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 17/05/2017.
 */

public class Utiles {

    public static User getUserFromContentProvider(Context context, String uid) {
        Cursor cUser = context.getContentResolver().query(
                SportteamContract.UserEntry.CONTENT_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.UserEntry.USER_ID + " = ?",
                new String[]{uid},
                null);
        if (cUser != null) {
            Cursor cSports = context.getContentResolver().query(
                    SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                    SportteamContract.UserSportEntry.USER_SPORT_COLUMNS,
                    SportteamContract.UserSportEntry.USER_ID + " = ?",
                    new String[]{uid},
                    SportteamContract.UserSportEntry.LEVEL + " DESC");
            ArrayList<Sport> sportsArray = new ArrayList<>();
            if (cSports != null) {
                while (cSports.moveToNext()) {
                    Sport s = new Sport(
                            cSports.getString(SportteamContract.UserSportEntry.COLUMN_SPORT),
                            cSports.getFloat(SportteamContract.UserSportEntry.COLUMN_LEVEL),
                            0);
                    sportsArray.add(s);
                }
                cSports.close();
            }
            if (cUser.moveToFirst()) {
                User user = new User(
                        cUser.getString(SportteamContract.UserEntry.COLUMN_USER_ID),
                        cUser.getString(SportteamContract.UserEntry.COLUMN_EMAIL),
                        cUser.getString(SportteamContract.UserEntry.COLUMN_NAME),
                        cUser.getString(SportteamContract.UserEntry.COLUMN_CITY),
                        cUser.getInt(SportteamContract.UserEntry.COLUMN_AGE),
                        cUser.getString(SportteamContract.UserEntry.COLUMN_PHOTO),
                        sportsArray);
                cUser.close();
                return user;
            }
            cUser.close();
        }
        return null;
    }

    public static String getCurrentCity(Context context, String currentUserID) {
        String result = null;
        Cursor c = context.getContentResolver().query(
                SportteamContract.UserEntry.CONTENT_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.UserEntry.USER_ID + " = ?",
                new String[]{currentUserID},
                null);
        if (c != null && c.moveToFirst()) {
            result = c.getString(SportteamContract.UserEntry.COLUMN_CITY);
            c.close();
        }
        return result;
    }
}
