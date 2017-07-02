package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public abstract class NewAlarmContract {

    public interface Presenter {
        void openAlarm(LoaderManager loaderManager, Bundle b);
        void addAlarm(String alarmId, String sport, String field, String city,
                      String dateFrom, String dateTo,
                      String totalFrom, String totalTo,
                      String emptyFrom, String emptyTo);
    }

    public interface View {
        void showAlarmSport(String sport);
        void showAlarmPlace(String place);
        void showAlarmCity(String city);
        void showAlarmDate(Long dateFrom, Long dateTo);
        void showAlarmTotalPlayers(Long totalPlayersFrom, Long totalPlayersTo);
        void showAlarmEmptyPlayers(Long emptyPlayersFrom, Long emptyPlayersTo);
        void clearUI();
        Context getActivityContext();
    }
}
