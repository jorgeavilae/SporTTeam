package com.usal.jorgeav.sportapp.events.addevent;

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
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.mainactivities.EventsActivity;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.HashMap;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class NewEventPresenter implements NewEventContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = NewEventPresenter.class.getSimpleName();

    NewEventContract.View mNewEventView;

    public NewEventPresenter(NewEventContract.View view){
        this.mNewEventView = view;
    }

    @Override
    public void addEvent(String id, String sport, String field, LatLng coord, String name, String city,
                         String date, String time, String total, String empty,
                         HashMap<String, Boolean> participants) {
        String myUid = Utiles.getCurrentUserId();
        if (isValidSport(sport) && isValidField(city, field, sport, coord) && isValidName(name)
                && isValidOwner(myUid) && isDateTimeCorrect(date, time) && isPlayersCorrect(total, empty)) {
            long dateMillis = UtilesTime.stringDateToMillis(date);
            long timeMillis = UtilesTime.stringTimeToMillis(time);
            Event event = new Event(
                    id, sport, field, coord, name, city, dateMillis + timeMillis, myUid,
                    Integer.valueOf(total), Integer.valueOf(empty), participants, null); // TODO: 13/07/2017 simulatedParticipants se pierden on edits

            Log.d(TAG, "addEvent: "+event);
            if(TextUtils.isEmpty(event.getEvent_id()))
                FirebaseActions.addEvent(event);
            else
                FirebaseActions.editEvent(event);
            ((EventsActivity)mNewEventView.getActivityContext()).newEventFieldSelected = null;
            ((EventsActivity)mNewEventView.getActivityContext()).newEventCitySelectedName = null;
            ((EventsActivity)mNewEventView.getActivityContext()).newEventFieldSelectedCoord = null;
            ((AppCompatActivity)mNewEventView.getActivityContext()).onBackPressed();
        } else
            Toast.makeText(mNewEventView.getActivityContext(), "Error: algun campo vacio", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidSport(String sport) {
        // If R.array.sport_id contains this sport
        String[] arraySports = mNewEventView.getActivityContext().getResources().getStringArray(R.array.sport_id);
        for (String sportArr : arraySports)
            if (sport.equals(sportArr)) return true;
        Log.e(TAG, "isValidSport: not valid");
        return false;
    }

    private boolean isValidField(String city, String field, String sport, LatLng coordinates) {
        // Check if the sport doesn't need a field
        String[] arraySports = mNewEventView.getActivityContext().getResources().getStringArray(R.array.sport_id);
        if (sport.equals(arraySports[0]) || sport.equals(arraySports[1])) return true;

        // Query database for the fieldId and checks if this sport exists
        // TODO: 18/07/2017 utiles.getfieldfromCP ,, check sport, city and coord
        Cursor c = SportteamLoader.simpleQueryFieldId(mNewEventView.getActivityContext(), field);
        if (c != null)
            try {
                if (c.getCount() > 0)
                    while (c.moveToFirst())
                        if (c.getString(SportteamContract.FieldEntry.COLUMN_SPORT).equals(sport)
                                && c.getString(SportteamContract.FieldEntry.COLUMN_CITY).equals(city)) {
                            c.close(); return true;
                        }
            } finally { c.close(); }
        Log.e(TAG, "isValidField: not valid");
        return false;
    }

    private boolean isValidName(String name) {
        if (!TextUtils.isEmpty(name)) return true;
        Log.e(TAG, "isValidName: not valid");
        return false;
    }

    private boolean isValidOwner(String uid) {
        if (!TextUtils.isEmpty(uid)) return true;
        Log.e(TAG, "isValidOwner: not valid");
        return false;
    }

    private boolean isDateTimeCorrect(String date, String time) {
        long dateMillis = UtilesTime.stringDateToMillis(date);
        long timeMillis = UtilesTime.stringTimeToMillis(time);
        if (System.currentTimeMillis() < dateMillis+timeMillis) return true;
        Log.e(TAG, "isDateTimeCorrect: incorrect");
        return false;
    }

    private boolean isPlayersCorrect(String total, String empty) {
        if (!TextUtils.isEmpty(total) && !TextUtils.isEmpty(empty))
            if (Integer.valueOf(total) >= Integer.valueOf(empty))
                return true;
        Log.e(TAG, "isPlayersCorrect: incorrect");
        return false;
    }

    @Override
    public void openEvent(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewEventFragment.BUNDLE_EVENT_ID)) {
            loaderManager.initLoader(SportteamLoader.LOADER_EVENT_ID, b, this);
            loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID, b, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String eventId = args.getString(DetailEventFragment.BUNDLE_EVENT_ID);
        switch (id) {
            case SportteamLoader.LOADER_EVENT_ID:
                return SportteamLoader
                        .cursorLoaderOneEvent(mNewEventView.getActivityContext(), eventId);
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                return SportteamLoader
                        .cursorLoaderEventParticipantsNoData(mNewEventView.getActivityContext(), eventId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_EVENT_ID:
                showEventDetails(data);
                break;
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                HashMap<String, Boolean> map = new HashMap<>();
                while(data.moveToNext()) {
                    String userId = data.getString(SportteamContract.EventsParticipationEntry.COLUMN_USER_ID);
                    Boolean participates = data.getInt(SportteamContract.EventsParticipationEntry.COLUMN_PARTICIPATES) == 1;
                    map.put(userId, participates);
                }
                mNewEventView.setParticipants(map);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_EVENT_ID:
                showEventDetails(null);
                break;
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                mNewEventView.setParticipants(null);
                break;
        }
    }

    private void showEventDetails(Cursor data) {
        if (data != null && data.moveToFirst()) {
            mNewEventView.showEventSport(data.getString(SportteamContract.EventEntry.COLUMN_SPORT));
            String fieldId = data.getString(SportteamContract.EventEntry.COLUMN_FIELD);
            String city = data.getString(SportteamContract.EventEntry.COLUMN_CITY);
            double latitude = data.getDouble(SportteamContract.EventEntry.COLUMN_FIELD_LATITUDE);
            double longitude = data.getDouble(SportteamContract.EventEntry.COLUMN_FIELD_LONGITUDE);
            LatLng coordinates = null; if (latitude != 0 && longitude != 0) coordinates = new LatLng(latitude, longitude);
            mNewEventView.showEventField(fieldId, city, coordinates);
            mNewEventView.showEventName(data.getString(SportteamContract.EventEntry.COLUMN_NAME));
            mNewEventView.showEventDate(data.getLong(SportteamContract.EventEntry.COLUMN_DATE));
            mNewEventView.showEventTotalPlayers(data.getInt(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS));
            mNewEventView.showEventEmptyPlayers(data.getInt(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS));
        } else {
            mNewEventView.clearUI();
        }
    }
}
