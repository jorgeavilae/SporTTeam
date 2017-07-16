package com.usal.jorgeav.sportapp.fields.addfield;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.mainactivities.FieldsActivity;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class NewFieldPresenter implements NewFieldContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = NewFieldPresenter.class.getSimpleName();

    NewFieldContract.View mNewFieldView;

    public NewFieldPresenter(NewFieldContract.View view){
        this.mNewFieldView = view;
    }

    @Override
    public void loadNearbyFields(LoaderManager loaderManager, Bundle b) {
        String city = UtilesPreferences.getCurrentUserCity(mNewFieldView.getActivityContext());

        if (city != null) {
            FirebaseSync.loadFieldsFromCity(city);
            loaderManager.initLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY, b, this);
        }
    }

    @Override
    public void addField(String id, String name, String sport, String address,
                         LatLng coords, String city, float rate, int votes,
                         String openTime, String closeTime, String userId) {
        if (isValidSport(sport) && isValidAddress(address, city) && isValidName(name)
                && isValidCoords(coords) && isTimesCorrect(openTime, closeTime)
                && isPunctuationValid(rate, votes)&& isValidCreator(userId)) {
            long openMillis = UtilesTime.stringTimeToMillis(openTime);
            long closeMillis = UtilesTime.stringTimeToMillis(closeTime);
            Field field = new Field(id, name, sport, address, coords, city,
                    rate, votes, openMillis, closeMillis, userId);

            Log.d(TAG, "addField: "+field);
            FirebaseActions.addField(field);
            ((FieldsActivity)mNewFieldView.getActivityContext()).mPlaceSelected = null;
            ((AppCompatActivity)mNewFieldView.getActivityContext()).onBackPressed();
        } else
            Toast.makeText(mNewFieldView.getActivityContext(), "Error: algun campo vacio", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidSport(String sport) {
        // If R.array.sport_id contains this sport
        String[] arraySports = mNewFieldView.getActivityContext().getResources().getStringArray(R.array.sport_id);
        for (String sportArr : arraySports)
            if (sport.equals(sportArr)) return true;
        Log.e(TAG, "isValidSport: not valid");
        return false;
    }

    private boolean isValidAddress(String address, String city) {
        if (!TextUtils.isEmpty(address) && !TextUtils.isEmpty(city)
                && address.contains(city)) return true;
        Log.e(TAG, "isValidField: not valid");
        return false;
    }

    private boolean isValidName(String name) {
        if (!TextUtils.isEmpty(name)) return true;
        Log.e(TAG, "isValidName: not valid");
        return false;
    }

    private boolean isValidCreator(String userId) {
        if (!TextUtils.isEmpty(userId)) return true;
        Log.e(TAG, "isValidName: not valid");
        return false;
    }

    private boolean isValidCoords(LatLng coords) {
        if (coords != null && coords.latitude != 0 && coords.longitude != 0)
            return true;
        Log.e(TAG, "isValidCoords: not valid");
        return false;
    }

    private boolean isTimesCorrect(String open, String close) {
        if (!TextUtils.isEmpty(open) && !TextUtils.isEmpty(close)) {
            long openMillis = UtilesTime.stringTimeToMillis(open);
            long closeMillis = UtilesTime.stringTimeToMillis(close);
            if (openMillis <= closeMillis) return true;
        }
        Log.e(TAG, "isTimesCorrect: incorrect");
        return false;
    }

    private boolean isPunctuationValid(float rate, int votes) {
        if (rate >= 0 && rate <= 5 && votes >= 0) return true;
        Log.e(TAG, "isPunctuationValid: incorrect");
        return false;
    }

    @Override
    public void openField(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewFieldFragment.BUNDLE_FIELD_ID)
                && b.containsKey(NewFieldFragment.BUNDLE_SPORT_ID)) {
            loaderManager.initLoader(SportteamLoader.LOADER_FIELD_ID, b, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_FIELDS_FROM_CITY:
                String city = UtilesPreferences.getCurrentUserCity(mNewFieldView.getActivityContext());
                if (city != null)
                    return SportteamLoader
                            .cursorLoaderFieldsFromCity(mNewFieldView.getActivityContext(), city);
            case SportteamLoader.LOADER_FIELD_ID:
                String fieldId = args.getString(NewFieldFragment.BUNDLE_FIELD_ID);
                String sportId = args.getString(NewFieldFragment.BUNDLE_SPORT_ID);
                return SportteamLoader
                        .cursorLoaderOneField(mNewFieldView.getActivityContext(), fieldId, sportId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_FIELDS_FROM_CITY:
                ArrayList<Field> dataList = UtilesContentProvider.cursorToMultipleField(data);
                mNewFieldView.retrieveFields(dataList);
                break;
            case SportteamLoader.LOADER_FIELD_ID:
                showFieldDetail(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_EVENT_ID:
                showFieldDetail(null);
                break;
            case SportteamLoader.LOADER_FIELDS_FROM_CITY:
                mNewFieldView.retrieveFields(null);
                break;
        }
    }

    private void showFieldDetail(Cursor data) {
        if (data != null && data.moveToFirst()) {
            mNewFieldView.showFieldSport(data.getString(SportteamContract.FieldEntry.COLUMN_SPORT));
            String address = data.getString(SportteamContract.FieldEntry.COLUMN_ADDRESS);
            String city = data.getString(SportteamContract.FieldEntry.COLUMN_CITY);
            double lat = data.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LATITUDE);
            double lng = data.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LONGITUDE);
            LatLng coords = null; if (lat != 0 && lng != 0) coords = new LatLng(lat, lng);
            mNewFieldView.showFieldPlace(address, city, coords);
            mNewFieldView.showFieldName(data.getString(SportteamContract.FieldEntry.COLUMN_NAME));
            long openTime = data.getLong(SportteamContract.FieldEntry.COLUMN_OPENING_TIME);
            long closeTime = data.getLong(SportteamContract.FieldEntry.COLUMN_CLOSING_TIME);
            mNewFieldView.showFieldTimes(openTime, closeTime);
            float rate = data.getFloat(SportteamContract.FieldEntry.COLUMN_PUNCTUATION);
            int votes = data.getInt(SportteamContract.FieldEntry.COLUMN_VOTES);
            mNewFieldView.showFieldRate(rate, votes);
        } else {
            mNewFieldView.clearUI();
        }
    }
}
