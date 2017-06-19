package com.usal.jorgeav.sportapp.alarms.alarmdetail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.FirebaseData;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public class DetailAlarmPresenter implements DetailAlarmContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = DetailAlarmPresenter.class.getSimpleName();

    DetailAlarmContract.View mView;

    public DetailAlarmPresenter(@NonNull DetailAlarmContract.View view) {
        this.mView = view;
    }

    @Override
    public void openAlarm(LoaderManager loaderManager, Bundle b) {
        String alarmId = b.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
        FirebaseData.loadAnAlarm(alarmId);
        loaderManager.initLoader(SportteamLoader.LOADER_ALARM_ID, b, this);
        loaderManager.initLoader(SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String alarmId = args.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
        switch (id) {
            case SportteamLoader.LOADER_ALARM_ID:
                return SportteamLoader
                        .cursorLoaderOneAlarm(mView.getActivityContext(), alarmId);
            case SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID:
                return SportteamLoader
                        .cursorLoaderAlarmCoincidence(mView.getActivityContext(), alarmId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_ALARM_ID:
                showAlarmDetails(data);
                break;
            case SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID:
                mView.showEvents(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_ALARM_ID:
                showAlarmDetails(null);
                break;
            case SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID:
                mView.showEvents(null);
                break;
        }
    }

    private void showAlarmDetails(Cursor data) {
        if (data != null && data.moveToFirst()) {
            String dateFrom = UtilesTime.millisToDateTimeString(data.getLong(SportteamContract.AlarmEntry.COLUMN_DATE_FROM));
            String dateTo = UtilesTime.millisToDateTimeString(data.getLong(SportteamContract.AlarmEntry.COLUMN_DATE_TO));
            int totalPlFrom = data.getInt(SportteamContract.AlarmEntry.COLUMN_TOTAL_PLAYERS_FROM);
            int totalPlTo = data.getInt(SportteamContract.AlarmEntry.COLUMN_TOTAL_PLAYERS_TO);
            int emptyFrom = data.getInt(SportteamContract.AlarmEntry.COLUMN_EMPTY_PLAYERS_FROM);
            int emptyTo = data.getInt(SportteamContract.AlarmEntry.COLUMN_EMPTY_PLAYERS_TO);
            mView.showAlarmId(data.getString(SportteamContract.AlarmEntry.COLUMN_ALARM_ID));
            mView.showAlarmSport(data.getString(SportteamContract.AlarmEntry.COLUMN_SPORT));
            mView.showAlarmPlace(data.getString(SportteamContract.AlarmEntry.COLUMN_FIELD));
            mView.showAlarmDate(dateFrom, dateTo);
            mView.showAlarmTotalPlayers(totalPlFrom, totalPlTo);
            mView.showAlarmEmptyPlayers(emptyFrom, emptyTo);
        } else {
            mView.showAlarmId("");
            mView.showAlarmSport("");
            mView.showAlarmPlace("");
            mView.showAlarmDate("", "");
            mView.showAlarmTotalPlayers(-1, -1);
            mView.showAlarmEmptyPlayers(-1, -1);
        }
    }
}
