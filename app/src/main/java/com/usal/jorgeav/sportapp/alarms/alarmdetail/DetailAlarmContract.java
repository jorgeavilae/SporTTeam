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
        void showEventId(String id);
        void showEventSport(String sport);
        void showEventPlace(String place);
        void showEventDate(String dateFrom, String dateTo);
        void showEventTotalPlayers(int totalPlayersFrom, int totalPlayersTo);
        void showEventEmptyPlayers(int emptyPlayersFrom, int emptyPlayersTo);
        void showEvents(Cursor data);
        FragmentActivity getActivityContext();
        Fragment getThis();

    }

    public interface Presenter {
        void openEvent(LoaderManager loaderManager, Bundle b);
    }
}
