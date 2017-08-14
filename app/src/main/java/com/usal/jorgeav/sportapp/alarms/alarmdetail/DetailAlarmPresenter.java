package com.usal.jorgeav.sportapp.alarms.alarmdetail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.widget.Toast;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.alarms.addalarm.NewAlarmFragment;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.actions.AlarmFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

class DetailAlarmPresenter implements DetailAlarmContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    @SuppressWarnings("unused")
    private static final String TAG = DetailAlarmPresenter.class.getSimpleName();

    private DetailAlarmContract.View mView;

    DetailAlarmPresenter(@NonNull DetailAlarmContract.View view) {
        this.mView = view;
    }

    @Override
    public void openAlarm(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewAlarmFragment.BUNDLE_ALARM_ID)) {
            String alarmId = b.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
            if (alarmId != null) {
                FirebaseSync.loadAnAlarm(alarmId);
                loaderManager.initLoader(SportteamLoader.LOADER_ALARM_ID, b, this);
                loaderManager.initLoader(SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID, b, this);
            }
        }
    }

    @Override
    public void deleteAlarm(Bundle b) {
        String alarmId = b.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
        String userId = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(mView.getActivityContext(), R.string.action_not_done, Toast.LENGTH_SHORT).show();
            return;
        }
        AlarmFirebaseActions.deleteAlarm(userId, alarmId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String alarmId = args.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
        String myUserId = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserId)) return null;
        switch (id) {
            case SportteamLoader.LOADER_ALARM_ID:
                return SportteamLoader
                        .cursorLoaderOneAlarm(mView.getActivityContext(), alarmId);
            case SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID:
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
            mView.showAlarmSport(a.getSport_id());

            Field field = UtilesContentProvider.getFieldFromContentProvider(a.getField_id());
            mView.showAlarmPlace(field, a.getCity());

            mView.showAlarmDate(a.getDate_from(), a.getDate_to());
            mView.showAlarmTotalPlayers(a.getTotal_players_from(), a.getTotal_players_to());
            mView.showAlarmEmptyPlayers(a.getEmpty_players_from(), a.getEmpty_players_to());
        } else {
            mView.clearUI();
        }
    }
}
