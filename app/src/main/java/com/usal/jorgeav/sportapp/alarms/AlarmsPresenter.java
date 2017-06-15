package com.usal.jorgeav.sportapp.alarms;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class AlarmsPresenter implements AlarmsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ALARMS_KEY = "ALARMS_KEY";

    AlarmsContract.View mAlarmsView;

    public AlarmsPresenter(AlarmsContract.View mAlarmsView) {
        this.mAlarmsView = mAlarmsView;
    }

    @Override
    public void loadAlarms(LoaderManager loaderManager, Bundle b) {
        loaderManager.initLoader(SportteamLoader.LOADER_MY_ALARMS_ID, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_MY_ALARMS_ID:
                return SportteamLoader
                        .cursorLoaderMyAlarms(mAlarmsView.getActivityContext());
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAlarmsView.showAlarms(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAlarmsView.showAlarms(null);
    }
}
