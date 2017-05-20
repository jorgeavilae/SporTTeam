package com.usal.jorgeav.sportapp.profile;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.FirebaseDatabaseActions;

import java.util.Locale;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class ProfilePresenter implements ProfileContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private ProfileContract.View mUserView;

    public ProfilePresenter(ProfileContract.View userView) {
        mUserView = userView;
    }

    @Override
    public void loadUser() {
        FirebaseDatabaseActions.loadProfile(mUserView.getContext());
    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return this;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ProfileFragment.LOADER_MYPROFILE_ID:
                String currentUserID = "67ht67ty9hi485g94u5hi";
                return new CursorLoader(
                        this.mUserView.getContext(),
                        SportteamContract.UserEntry.CONTENT_USER_URI,
                        SportteamContract.UserEntry.USER_COLUMNS,
                        SportteamContract.UserEntry.USER_ID + " = ?",
                        new String[]{currentUserID},
                        null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            showUser(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
            showUser(null);
    }

    private void showUser(Cursor data) {
        if(data != null && data.moveToFirst()) {
            mUserView.showUserImage(data.getString(SportteamContract.UserEntry.COLUMN_PHOTO));
            mUserView.showUserName(data.getString(SportteamContract.UserEntry.COLUMN_NAME));
            mUserView.showUserCity(data.getString(SportteamContract.UserEntry.COLUMN_CITY));
            int age = data.getInt(SportteamContract.UserEntry.COLUMN_AGE);
            mUserView.showUserAge(String.format(Locale.getDefault(), "%2d", age));
        }


    }
}
