package com.usal.jorgeav.sportapp.fields;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public class FieldsPresenter implements FieldsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

    FieldsContract.View mFieldsView;

    public FieldsPresenter(FieldsContract.View fieldsView) {
        this.mFieldsView = fieldsView;
    }

    @Override
    public void loadNearbyFields(LoaderManager loaderManager, Bundle b) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        String city = Utiles.getCurrentUserCity(mFieldsView.getActivityContext(), myUserID);
        FirebaseSync.loadFieldsFromCity(city);
        loaderManager.initLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY, b, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_FIELDS_FROM_CITY:
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
                String city = Utiles.getCurrentUserCity(mFieldsView.getActivityContext(), myUserID);
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
        mFieldsView.showFields(null);
    }
}