package com.usal.jorgeav.sportapp.alarms.alarmdetail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public abstract class DetailAlarmContract {

    public interface View {
        void showAlarmId(String id);
        void showAlarmSport(String sport);
        void showAlarmPlace(String place);
        void showAlarmDate(String dateFrom, String dateTo);
        void showAlarmTotalPlayers(int totalPlayersFrom, int totalPlayersTo);
        void showAlarmEmptyPlayers(int emptyPlayersFrom, int emptyPlayersTo);
        void showEvents(Cursor data);
        FragmentActivity getActivityContext();
        Fragment getThis();

    }

    public interface Presenter {
        void openAlarm(LoaderManager loaderManager, Bundle b);
    }
}
