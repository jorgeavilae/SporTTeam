package com.usal.jorgeav.sportapp.alarms.alarmdetail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

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
        // The only fragment initializing this one is AlarmsFragment
        // in which this method it's already invoked
        // FirebaseSync.loadAnAlarm(alarmId);
        loaderManager.initLoader(SportteamLoader.LOADER_ALARM_ID, b, this);
        loaderManager.initLoader(SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID, b, this);
    }

    @Override
    public void deleteAlarm(Bundle b) {
        String alarmId = b.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
        String userId = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(mView.getActivityContext(), "No se ha podido completar la accion", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseActions.deleteAlarm(userId, alarmId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String alarmId = args.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
        switch (id) {
            case SportteamLoader.LOADER_ALARM_ID:
                return SportteamLoader
                        .cursorLoaderOneAlarm(mView.getActivityContext(), alarmId);
            case SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID:
                String myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                return SportteamLoader
                        .cursorLoaderAlarmCoincidence(mView.getActivityContext(), alarmId, myUserId);
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
        Alarm a = UtilesContentProvider.cursorToSingleAlarm(data);
        if (a != null) {
            mView.showAlarmId(a.getId());
            mView.showAlarmSport(a.getSport_id());
            mView.showAlarmPlace(a.getField_id());
            mView.showAlarmDate(a.getDate_from(), a.getDate_to());
            mView.showAlarmTotalPlayers(a.getTotal_players_from(), a.getTotal_players_to());
            mView.showAlarmEmptyPlayers(a.getEmpty_players_from(), a.getEmpty_players_to());
        } else {
            mView.clearUI();
        }
    }
}
