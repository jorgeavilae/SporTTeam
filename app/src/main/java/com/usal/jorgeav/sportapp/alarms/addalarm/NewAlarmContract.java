package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.usal.jorgeav.sportapp.data.Field;

import java.util.ArrayList;

public abstract class NewAlarmContract {

    public interface Presenter {
        void openAlarm(LoaderManager loaderManager, Bundle b);
        void addAlarm(String alarmId, String sport, String field, String city,
                      String dateFrom, String dateTo,
                      String totalFrom, String totalTo,
                      String emptyFrom, String emptyTo);

        void loadFields(LoaderManager loaderManager, Bundle arguments);

        void stopLoadFields(LoaderManager loaderManager);
    }

    public interface View {
        void showAlarmSport(String sport);
        void showAlarmField(String fieldId, String city);
        void showAlarmDate(Long dateFrom, Long dateTo);
        void showAlarmTotalPlayers(Long totalPlayersFrom, Long totalPlayersTo);
        void showAlarmEmptyPlayers(Long emptyPlayersFrom, Long emptyPlayersTo);
        void clearUI();

        Context getActivityContext();

        void retrieveFields(ArrayList<Field> dataList);
    }
}
