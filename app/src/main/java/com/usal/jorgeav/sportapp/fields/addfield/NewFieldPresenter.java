package com.usal.jorgeav.sportapp.fields.addfield;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.usal.jorgeav.sportapp.data.Field;
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

//    @Override
//    public void openField(LoaderManager loaderManager, Bundle b) {
//        if (b != null && b.containsKey(NewFieldFragment.BUNDLE_EVENT_ID)) {
//            loaderManager.initLoader(SportteamLoader.LOADER_EVENT_ID, b, this);
//            loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID, b, this);
//        }
//    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        String eventId = args.getString(DetailEventFragment.BUNDLE_EVENT_ID);
//        switch (id) {
//            case SportteamLoader.LOADER_EVENT_ID:
//                return SportteamLoader
//                        .cursorLoaderOneEvent(mNewEventView.getActivityContext(), eventId);
//            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
//                return SportteamLoader
//                        .cursorLoaderEventParticipantsNoData(mNewEventView.getActivityContext(), eventId);
//        }
//        return null;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        switch (loader.getId()) {
//            case SportteamLoader.LOADER_EVENT_ID:
//                showEventDetails(data);
//                break;
//            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
//                HashMap<String, Boolean> map = new HashMap<>();
//                while(data.moveToNext()) {
//                    String userId = data.getString(SportteamContract.EventsParticipationEntry.COLUMN_USER_ID);
//                    Boolean participates = data.getInt(SportteamContract.EventsParticipationEntry.COLUMN_PARTICIPATES) == 1;
//                    map.put(userId, participates);
//                }
//                mNewEventView.setParticipants(map);
//                break;
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        switch (loader.getId()) {
//            case SportteamLoader.LOADER_EVENT_ID:
//                showEventDetails(null);
//                break;
//            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
//                mNewEventView.setParticipants(null);
//                break;
//        }
//    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_FIELDS_FROM_CITY:
                String city = UtilesPreferences.getCurrentUserCity(mNewFieldView.getActivityContext());
                if (city != null)
                    return SportteamLoader
                            .cursorLoaderFieldsFromCity(mNewFieldView.getActivityContext(), city);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Field> dataList = UtilesContentProvider.cursorToMultipleField(data);
        mNewFieldView.retrieveFields(dataList);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mNewFieldView.retrieveFields(null);
    }

//
//    private void showEventDetails(Cursor data) {
//        if (data != null && data.moveToFirst()) {
//            mNewEventView.showEventSport(data.getString(SportteamContract.EventEntry.COLUMN_SPORT));
//            mNewEventView.showEventPlace(data.getString(SportteamContract.EventEntry.COLUMN_FIELD));
//            mNewEventView.showEventName(data.getString(SportteamContract.EventEntry.COLUMN_NAME));
//            mNewEventView.showEventDate(data.getLong(SportteamContract.EventEntry.COLUMN_DATE));
//            mNewEventView.showEventCity(data.getString(SportteamContract.EventEntry.COLUMN_CITY));
//            mNewEventView.showEventTotalPlayers(data.getInt(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS));
//            mNewEventView.showEventEmptyPlayers(data.getInt(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS));
//        } else {
//            mNewEventView.clearUI();
//        }
//    }
}
