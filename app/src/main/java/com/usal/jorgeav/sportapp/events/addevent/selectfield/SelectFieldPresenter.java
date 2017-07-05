package com.usal.jorgeav.sportapp.events.addevent.selectfield;

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
 * Created by Jorge Avila on 06/06/2017.
 */

public class SelectFieldPresenter implements SelectFieldContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = SelectFieldPresenter.class.getSimpleName();
    private static final String USERID_KEY = "USERID_KEY";

    SelectFieldContract.View mSelectFieldsView;

    public SelectFieldPresenter(SelectFieldContract.View mSelectFieldsView) {
        this.mSelectFieldsView = mSelectFieldsView;
    }

    @Override
    public void loadFieldsWithSport(LoaderManager loaderManager, Bundle b) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        String city = Utiles.getCurrentCity(mSelectFieldsView.getActivityContext(), myUserID);
        FirebaseSync.loadFieldsFromCity(city);
        loaderManager.initLoader(SportteamLoader.LOADER_FIELDS_WITH_SPORT, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_FIELDS_WITH_SPORT:
                String sportId = args.getString(SelectFieldFragment.BUNDLE_SPORT_ID);
                return SportteamLoader
                        .cursorLoaderFieldsWithSport(mSelectFieldsView.getActivityContext(), sportId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSelectFieldsView.showFields(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mSelectFieldsView.showFields(null);
    }
}