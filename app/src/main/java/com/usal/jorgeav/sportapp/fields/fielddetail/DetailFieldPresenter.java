package com.usal.jorgeav.sportapp.fields.fielddetail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;

class DetailFieldPresenter implements DetailFieldContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = DetailFieldPresenter.class.getSimpleName();

    private DetailFieldContract.View mView;

    DetailFieldPresenter(@NonNull DetailFieldContract.View view) {
        this.mView = view;
    }

    @Override
    public void voteSportInField(String fieldId, String sportId, float rating) {
        if (fieldId != null && !TextUtils.isEmpty(fieldId)
                && sportId != null && !TextUtils.isEmpty(sportId)
                && rating > 0 && rating <= 5) {
            FirebaseActions.voteField(fieldId, sportId, rating);
        }
    }

    @Override
    public void deleteField(final String fieldId) {
        if (fieldId != null && !TextUtils.isEmpty(fieldId)) {
            FirebaseActions.getFieldNextEventsReferenceWithId(fieldId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(mView.getActivityContext(),
                                        R.string.toast_msg_error_there_is_next_events,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                FirebaseActions.deleteField(fieldId);
                                ((BaseFragment) mView).resetBackStack();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }

    @Override
    public void openField(LoaderManager loaderManager, Bundle b) {
        String fieldId = b.getString(DetailFieldFragment.BUNDLE_FIELD_ID);
        if (fieldId != null) FirebaseSync.loadAField(fieldId);
        loaderManager.initLoader(SportteamLoader.LOADER_FIELD_ID, b, this);
        loaderManager.initLoader(SportteamLoader.LOADER_FIELD_SPORTS_ID, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String fieldId = args.getString(DetailFieldFragment.BUNDLE_FIELD_ID);
        switch (id) {
            case SportteamLoader.LOADER_FIELD_ID:
                return SportteamLoader
                        .cursorLoaderOneField(mView.getActivityContext(), fieldId);
            case SportteamLoader.LOADER_FIELD_SPORTS_ID:
                return SportteamLoader
                        .cursorLoaderFieldSports(mView.getActivityContext(), fieldId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_FIELD_ID:
                showFieldDetails(data);
                break;
            case SportteamLoader.LOADER_FIELD_SPORTS_ID:
                mView.showSportCourts(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_FIELD_ID:
                showFieldDetails(null);
                break;
            case SportteamLoader.LOADER_FIELD_SPORTS_ID:
                mView.showSportCourts(null);
                break;
        }
    }

    private void showFieldDetails(Cursor data) {
        if (data != null && data.moveToFirst()) {
            mView.showFieldName(data.getString(SportteamContract.FieldEntry.COLUMN_NAME));

            String address = data.getString(SportteamContract.FieldEntry.COLUMN_ADDRESS);
            String city = data.getString(SportteamContract.FieldEntry.COLUMN_CITY);
            double latitude = data.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LATITUDE);
            double longitude = data.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LONGITUDE);
            LatLng coordinates = null; if (latitude != 0 && longitude != 0) coordinates = new LatLng(latitude, longitude);
            mView.showFieldPlace(address, city, coordinates);

            long open = data.getLong(SportteamContract.FieldEntry.COLUMN_OPENING_TIME);
            long close = data.getLong(SportteamContract.FieldEntry.COLUMN_CLOSING_TIME);
            mView.showFieldTimes(open, close);
            mView.showFieldCreator(data.getString(SportteamContract.FieldEntry.COLUMN_CREATOR));
        } else {
            mView.clearUI();
        }
    }
}
