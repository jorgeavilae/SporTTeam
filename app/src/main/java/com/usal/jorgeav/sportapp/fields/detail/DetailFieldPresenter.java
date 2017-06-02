package com.usal.jorgeav.sportapp.fields.detail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public class DetailFieldPresenter implements DetailFieldContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private DetailFieldContract.View mView;

    public DetailFieldPresenter(@NonNull DetailFieldContract.View view) {
        this.mView = view;
    }

    @Override
    public void openField() {
    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return this;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DetailFieldFragment.LOADER_FIELD_ID:
                return new CursorLoader(
                        this.mView.getActivityContext(),
                        SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                        SportteamContract.FieldEntry.FIELDS_COLUMNS,
                        SportteamContract.FieldEntry.FIELD_ID + " = ? AND " + SportteamContract.FieldEntry.SPORT +" = ? ",
                        new String[]{args.getString(DetailFieldFragment.BUNDLE_FIELD_ID),
                                args.getString(DetailFieldFragment.BUNDLE_SPORT_ID)},
                        null);
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
            mView.showFieldOpeningTime(Utiles.millisToTimeString(data.getLong(SportteamContract.FieldEntry.COLUMN_OPENING_TIME)));
            mView.showFieldClosingTime(Utiles.millisToTimeString(data.getLong(SportteamContract.FieldEntry.COLUMN_CLOSING_TIME)));
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
