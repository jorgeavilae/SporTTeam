package com.usal.jorgeav.sportapp.fields.detail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public class DetailFieldPresenter implements DetailFieldContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private DetailFieldContract.View mView;

    public DetailFieldPresenter(@NonNull DetailFieldContract.View view) {
        this.mView = view;
    }

    @Override
    public void openField(LoaderManager loaderManager, Bundle b) {
        String fieldId = b.getString(DetailFieldFragment.BUNDLE_FIELD_ID);
        FirebaseSync.loadAField(fieldId);
        loaderManager.initLoader(SportteamLoader.LOADER_FIELD_ID, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_FIELD_ID:
                String fieldId = args.getString(DetailFieldFragment.BUNDLE_FIELD_ID);
                String sportId = args.getString(DetailFieldFragment.BUNDLE_SPORT_ID);
                return SportteamLoader
                        .cursorLoaderOneField(mView.getActivityContext(), fieldId, sportId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        showFieldDetails(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        showFieldDetails(null);
    }

    private void showFieldDetails(Cursor data) {
        if(data != null && data.moveToFirst()) {
            mView.showFieldId(data.getString(SportteamContract.FieldEntry.COLUMN_FIELD_ID));
            mView.showFieldName(data.getString(SportteamContract.FieldEntry.COLUMN_NAME));
            mView.showFieldAddress(data.getString(SportteamContract.FieldEntry.COLUMN_ADDRESS));
            mView.showFieldRating(data.getFloat(SportteamContract.FieldEntry.COLUMN_PUNCTUATION));
            mView.showFieldSport(data.getString(SportteamContract.FieldEntry.COLUMN_SPORT));
            mView.showFieldOpeningTime(UtilesTime.millisToTimeString(data.getLong(SportteamContract.FieldEntry.COLUMN_OPENING_TIME)));
            mView.showFieldClosingTime(UtilesTime.millisToTimeString(data.getLong(SportteamContract.FieldEntry.COLUMN_CLOSING_TIME)));
        } else {
            mView.showFieldId("");
            mView.showFieldName("");
            mView.showFieldAddress("");
            mView.showFieldRating(-1f);
            mView.showFieldSport("");
            mView.showFieldOpeningTime("");
            mView.showFieldClosingTime("");
        }
    }
}
