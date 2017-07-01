package com.usal.jorgeav.sportapp.alarms.alarmdetail;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public abstract class DetailAlarmContract {

    public interface View {
        void showAlarmId(String id);
        void showAlarmSport(String sport);
        void showAlarmPlace(String place, String sport);
        void showAlarmDate(Long dateFrom, Long dateTo);
        void showAlarmTotalPlayers(Long totalPlayersFrom, Long totalPlayersTo);
        void showAlarmEmptyPlayers(Long emptyPlayersFrom, Long emptyPlayersTo);
        void showEvents(Cursor data);
        Context getActivityContext();

    }

    public interface Presenter {
        void openAlarm(LoaderManager loaderManager, Bundle b);
        void deleteAlarm(Bundle b);
    }
}
