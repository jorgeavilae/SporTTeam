package com.usal.jorgeav.sportapp.alarms.alarmdetail;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.data.Field;

abstract class DetailAlarmContract {

    public interface View {
        void showAlarmSport(String sport);
        void showAlarmPlace(Field field, String city);
        void showAlarmDate(Long dateFrom, Long dateTo);
        void showAlarmTotalPlayers(Long totalPlayersFrom, Long totalPlayersTo);
        void showAlarmEmptyPlayers(Long emptyPlayersFrom, Long emptyPlayersTo);
        void showEvents(Cursor data);
        void clearUI();
        Context getActivityContext();
        BaseFragment getThis();

    }

    public interface Presenter {
        void openAlarm(LoaderManager loaderManager, Bundle b);
        void deleteAlarm(Bundle b);
    }
}
