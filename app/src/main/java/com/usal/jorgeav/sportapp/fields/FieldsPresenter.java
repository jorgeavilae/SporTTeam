package com.usal.jorgeav.sportapp.fields;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public class FieldsPresenter implements FieldsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FieldsPresenter.class.getSimpleName();

    FieldsContract.View mFieldsView;

    public FieldsPresenter(FieldsContract.View fieldsView) {
        this.mFieldsView = fieldsView;
    }

    @Override
    public void loadNearbyFields(LoaderManager loaderManager, Bundle b) {
        String city = UtilesPreferences.getCurrentUserCity(mFieldsView.getActivityContext());
        FirebaseSync.loadFieldsFromCity(city, false);
        loaderManager.initLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY, b, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_FIELDS_FROM_CITY:
                String city = UtilesPreferences.getCurrentUserCity(mFieldsView.getActivityContext());
                return SportteamLoader
                        .cursorLoaderFieldsFromCity(mFieldsView.getActivityContext(), city);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFieldsView.showFields(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }
}