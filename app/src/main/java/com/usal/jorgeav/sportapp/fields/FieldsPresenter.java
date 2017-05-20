package com.usal.jorgeav.sportapp.fields;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public class FieldsPresenter implements FieldsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

    FieldsContract.View mFieldsView;

    public FieldsPresenter(FieldsContract.View fieldsView) {
        this.mFieldsView = fieldsView;
    }

    @Override
    public void loadFields() {
//        FirebaseDatabaseActions.loadFields(mFieldsView.getContext());
    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return this;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case FieldsFragment.LOADER_FIELDS_ID:
                return new CursorLoader(
                        this.mFieldsView.getContext(),
                        SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                        SportteamContract.FieldEntry.FIELDS_COLUMNS,
                        null,
                        null,
                        SportteamContract.FieldEntry.COLUMN_PUNCTUATION + " DESC");
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