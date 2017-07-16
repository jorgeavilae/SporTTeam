package com.usal.jorgeav.sportapp.fields.addfield;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

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
//
//    @Override
//    public void addField(String id, String sport, String field, String name, String city,
//                         String date, String time, String total, String empty,
//                         HashMap<String, Boolean> participants) {
//        if (isValidSport(sport) && isValidField(field, sport) && isValidName(name)
//                && isDateTimeCorrect(date, time) && isPlayersCorrect(total, empty)) {
//            long dateMillis = UtilesTime.stringDateToMillis(date);
//            long timeMillis = UtilesTime.stringTimeToMillis(time);
//            Event event = new Event(
//                    id, sport, field, name, city, dateMillis + timeMillis,
//                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                    Integer.valueOf(total), Integer.valueOf(empty), participants, null); // TODO: 13/07/2017 simulatedParticipants se pierden
//
//            Log.d(TAG, "addEvent: "+event);
//            if(TextUtils.isEmpty(event.getEvent_id()))
//                FirebaseActions.addEvent(event);
//            else
//                FirebaseActions.editEvent(event);
//            ((EventsActivity)mNewFieldView.getActivityContext()).newEventFieldSelected = null;
//            ((AppCompatActivity)mNewFieldView.getActivityContext()).onBackPressed();
//        } else
//            Toast.makeText(mNewFieldView.getActivityContext(), "Error: algun campo vacio", Toast.LENGTH_SHORT).show();
//    }
//
//    private boolean isValidSport(String sport) {
//        // If R.array.sport_id contains this sport
//        String[] arraySports = mNewFieldView.getActivityContext().getResources().getStringArray(R.array.sport_id);
//        for (String sportArr : arraySports)
//            if (sport.equals(sportArr)) return true;
//        Log.e(TAG, "isValidSport: not valid");
//        return false;
//    }
//
//    private boolean isValidField(String field, String sport) {
//        // Check if the sport doesn't need a field
//        String[] arraySports = mNewFieldView.getActivityContext().getResources().getStringArray(R.array.sport_id);
//        if (sport.equals(arraySports[0]) || sport.equals(arraySports[1])) return /* todo isValidAddress ?? */ true;
//
//        // Query database for the fieldId and checks if this sport exists
//        Cursor c = SportteamLoader.simpleQueryFieldId(mNewFieldView.getActivityContext(), field);
//        try {
//            while (c.moveToNext())
//                if (c.getString(SportteamContract.FieldEntry.COLUMN_SPORT).equals(sport)) {
//                    c.close(); return true;
//                }
//        } finally { c.close(); }
//        Log.e(TAG, "isValidField: not valid");
//        return false;
//    }
//
//    private boolean isValidName(String name) {
//        if (!TextUtils.isEmpty(name)) return true;
//        Log.e(TAG, "isValidName: not valid");
//        return false;
//    }
//
//    private boolean isDateTimeCorrect(String date, String time) {
//        long dateMillis = UtilesTime.stringDateToMillis(date);
//        long timeMillis = UtilesTime.stringTimeToMillis(time);
//        if (System.currentTimeMillis() < dateMillis+timeMillis) return true;
//        Log.e(TAG, "isDateTimeCorrect: incorrect");
//        return false;
//    }
//
//    private boolean isPlayersCorrect(String total, String empty) {
//        if (!TextUtils.isEmpty(total) && !TextUtils.isEmpty(empty))
//            if (Integer.valueOf(total) >= Integer.valueOf(empty))
//                return true;
//        Log.e(TAG, "isPlayersCorrect: incorrect");
//        return false;
//    }

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
            mNewFieldView.showFieldOpenTime(data.getLong(SportteamContract.FieldEntry.COLUMN_OPENING_TIME));
            mNewFieldView.showFieldCloseTime(data.getLong(SportteamContract.FieldEntry.COLUMN_CLOSING_TIME));
            mNewFieldView.showFieldRate(data.getFloat(SportteamContract.FieldEntry.COLUMN_PUNCTUATION));
        } else {
            mNewFieldView.clearUI();
        }
    }
}
