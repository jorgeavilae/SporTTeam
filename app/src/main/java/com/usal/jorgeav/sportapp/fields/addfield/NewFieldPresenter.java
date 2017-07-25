package com.usal.jorgeav.sportapp.fields.addfield;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.mainactivities.FieldsActivity;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.List;

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
    public void addField(String id, String name, String address, LatLng coords, String city,
                         String openTime, String closeTime, String userId, List<SportCourt> sports) {
        Long openMillis = UtilesTime.stringTimeToMillis(openTime);
        Long closeMillis = UtilesTime.stringTimeToMillis(closeTime);

        if (isValidAddress(address, city) && isValidName(name) && isValidCoords(coords)
                && isTimesCorrect(openMillis, closeMillis) && isValidCreator(userId)) {

            //If are equals means "all day" so: from 0:00 to 0:00
            if (openMillis.longValue() == closeMillis.longValue()) {openMillis = 0L; closeMillis = 0L;}

            Field field = new Field(id, name, address, coords.latitude, coords.longitude, city,
                    openMillis, closeMillis, userId, sports);

            Log.d(TAG, "addField: "+field);
            if(TextUtils.isEmpty(field.getId()))
                FirebaseActions.addField(field);
            else
                FirebaseActions.updateField(field);
            ((FieldsActivity)mNewFieldView.getActivityContext()).mFieldId = null;
            ((FieldsActivity)mNewFieldView.getActivityContext()).mAddress = null;
            ((FieldsActivity)mNewFieldView.getActivityContext()).mCity = null;
            ((FieldsActivity)mNewFieldView.getActivityContext()).mCoord = null;
            mNewFieldView.getThis().resetBackStack();
        } else
            Toast.makeText(mNewFieldView.getActivityContext(), "Error: algun campo vacio", Toast.LENGTH_SHORT).show();
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
        Log.e(TAG, "isValidCreator: not valid");
        return false;
    }

    private boolean isValidCoords(LatLng coords) {
        if (coords != null && coords.latitude != 0 && coords.longitude != 0)
            return true;
        Log.e(TAG, "isValidCoords: not valid");
        return false;
    }

    private boolean isTimesCorrect(Long open, Long close) {
        if (open != null && close != null && open <= close) return true;
        Log.e(TAG, "isTimesCorrect: incorrect");
        return false;
    }

    @Override
    public void openField(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewFieldFragment.BUNDLE_FIELD_ID)) {
            loaderManager.initLoader(SportteamLoader.LOADER_FIELD_ID, b, this);
            loaderManager.initLoader(SportteamLoader.LOADER_FIELD_SPORTS_ID, b, this);
        }
    }

    @Override
    public void destroyOpenFieldLoader(LoaderManager loaderManager) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_FIELD_ID);
        loaderManager.destroyLoader(SportteamLoader.LOADER_FIELD_SPORTS_ID);
    }

    @Override
    public void loadNearbyFields(LoaderManager loaderManager, Bundle b) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY);
        String city = UtilesPreferences.getCurrentUserCity(mNewFieldView.getActivityContext());

        if (city != null) {
            FirebaseSync.loadFieldsFromCity(city);
            loaderManager.initLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY, b, this);
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
                return SportteamLoader
                        .cursorLoaderOneField(mNewFieldView.getActivityContext(),
                                args.getString(NewFieldFragment.BUNDLE_FIELD_ID));
            case SportteamLoader.LOADER_FIELD_SPORTS_ID:
                return SportteamLoader
                        .cursorLoaderFieldSports(mNewFieldView.getActivityContext(),
                                args.getString(NewFieldFragment.BUNDLE_FIELD_ID));
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
            case SportteamLoader.LOADER_FIELD_SPORTS_ID:
                ArrayList<SportCourt> sports = new ArrayList<>();
                while(data.moveToNext()) {
                    String sportId = data.getString(SportteamContract.FieldSportEntry.COLUMN_SPORT);
                    Double punctuation = data.getDouble(SportteamContract.FieldSportEntry.COLUMN_PUNCTUATION);
                    Long votes = data.getLong(SportteamContract.FieldSportEntry.COLUMN_VOTES);
                    sports.add(new SportCourt(sportId, punctuation, votes));
                }
                mNewFieldView.setSportCourts(sports);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        //tODO deberia borrar todos los loaderreset?
    }

    private void showFieldDetail(Cursor data) {
        if (data != null && data.moveToFirst()) {
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

            mNewFieldView.showFieldCreator(data.getString(SportteamContract.FieldEntry.COLUMN_CREATOR));
        } else {
            mNewFieldView.clearUI();
        }
    }
}
