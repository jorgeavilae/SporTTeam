package com.usal.jorgeav.sportapp.events.addevent;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.mainactivities.EventsActivity;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.HashMap;

class NewEventPresenter implements NewEventContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = NewEventPresenter.class.getSimpleName();

    private NewEventContract.View mNewEventView;

    NewEventPresenter(NewEventContract.View view){
        this.mNewEventView = view;
    }

    @Override
    public void addEvent(String id, String sport, String field, String address, LatLng coord, String name, String city,
                         String date, String time, String total, String empty,
                         HashMap<String, Boolean> participants,
                         HashMap<String, SimulatedUser> simulatedParticipants) {
        String myUid = Utiles.getCurrentUserId();
        Long dateMillis = UtilesTime.stringDateToMillis(date);
        Long timeMillis = UtilesTime.stringTimeToMillis(time);

        if (isValidSport(sport) && isValidField(field, address, sport, city, coord) && isValidName(name)
                && isValidOwner(myUid) && isDateTimeCorrect(dateMillis, timeMillis) && isPlayersCorrect(total, empty)) {

            Event event = new Event(id, sport, field, address, coord, name, city,
                    dateMillis + timeMillis, myUid, Long.valueOf(total), Long.valueOf(empty),
                    participants, simulatedParticipants);

            Log.d(TAG, "addEvent: "+event);
            if(TextUtils.isEmpty(event.getEvent_id()))
                FirebaseActions.addEvent(event);
            else
                FirebaseActions.editEvent(event);

            ((EventsActivity)mNewEventView.getActivityContext()).mFieldId = null;
            ((EventsActivity)mNewEventView.getActivityContext()).mAddress = null;
            ((EventsActivity)mNewEventView.getActivityContext()).mCity = null;
            ((EventsActivity)mNewEventView.getActivityContext()).mCoord = null;
            mNewEventView.getThis().resetBackStack();
        } else
            Toast.makeText(mNewEventView.getActivityContext(), R.string.toast_invalid_arg, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidSport(String sport) {
        // If R.array.sport_id contains this sport
        String[] arraySports = mNewEventView.getActivityContext().getResources().getStringArray(R.array.sport_id_values);
        for (String sportArr : arraySports)
            if (sport.equals(sportArr)) return true;
        Log.e(TAG, "isValidSport: not valid");
        return false;
    }

    private boolean isValidField(String fieldId, String address, String sportId, String city, LatLng coordinates) {
        // Check if the sport doesn't need a field
        if (!Utiles.sportNeedsField(sportId)) return true;

        // Query database for the fieldId and checks if this sport exists
        Field field = UtilesContentProvider.getFieldFromContentProvider(fieldId);

        if (field != null
                && field.getAddress().equals(address)
                && field.getCity().equals(city)
                && field.getCoord_latitude() == coordinates.latitude
                && field.getCoord_longitude() == coordinates.longitude
                && field.containsSportCourt(sportId))
            return true;
        else {
            Log.e(TAG, "isValidField: not valid");
            return false;
        }
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

    private boolean isDateTimeCorrect(Long date, Long time) {
        if (date != null && time != null && System.currentTimeMillis() < date + time) return true;
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
            loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID, b, this);
            loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID, b, this);
        }
    }

    @Override
    public void loadFields(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewEventFragment.BUNDLE_SPORT_SELECTED_ID))
            loaderManager.initLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT, b, this);
    }

    @Override
    public void stopLoadFields(LoaderManager loaderManager) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String eventId, city, sportId;
        switch (id) {
            case SportteamLoader.LOADER_EVENT_ID:
                eventId = args.getString(NewEventFragment.BUNDLE_EVENT_ID);
                return SportteamLoader
                        .cursorLoaderOneEvent(mNewEventView.getActivityContext(), eventId);
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                eventId = args.getString(NewEventFragment.BUNDLE_EVENT_ID);
                return SportteamLoader
                        .cursorLoaderEventParticipantsNoData(mNewEventView.getActivityContext(), eventId);
            case SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID:
                eventId = args.getString(NewEventFragment.BUNDLE_EVENT_ID);
                return SportteamLoader
                        .cursorLoaderEventSimulatedParticipants(mNewEventView.getActivityContext(), eventId);
            case SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT:
                city = UtilesPreferences.getCurrentUserCity(mNewEventView.getActivityContext());
                sportId = args.getString(NewEventFragment.BUNDLE_SPORT_SELECTED_ID);
                return SportteamLoader
                        .cursorLoaderFieldsFromCityWithSport(mNewEventView.getActivityContext(), city, sportId);
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
                mNewEventView.setParticipants(UtilesContentProvider.cursorToMultipleParticipants(data));
                break;
            case SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID:
                mNewEventView.setSimulatedParticipants(UtilesContentProvider.cursorToMultipleSimulatedParticipants(data));
                break;
            case SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT:
                ArrayList<Field> dataList = UtilesContentProvider.cursorToMultipleField(data);
                mNewEventView.retrieveFields(dataList);
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
            case SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID:
                mNewEventView.setSimulatedParticipants(null);
                break;
        }
    }

    private void showEventDetails(Cursor data) {
        if (data != null && data.moveToFirst()) {
            String fieldId = data.getString(SportteamContract.EventEntry.COLUMN_FIELD);
            String address = data.getString(SportteamContract.EventEntry.COLUMN_ADDRESS);
            String city = data.getString(SportteamContract.EventEntry.COLUMN_CITY);
            double latitude = data.getDouble(SportteamContract.EventEntry.COLUMN_FIELD_LATITUDE);
            double longitude = data.getDouble(SportteamContract.EventEntry.COLUMN_FIELD_LONGITUDE);
            LatLng coordinates = null; if (latitude != 0 && longitude != 0) coordinates = new LatLng(latitude, longitude);
            mNewEventView.showEventField(fieldId, address, city, coordinates);

            mNewEventView.showEventSport(data.getString(SportteamContract.EventEntry.COLUMN_SPORT));
            mNewEventView.showEventName(data.getString(SportteamContract.EventEntry.COLUMN_NAME));
            mNewEventView.showEventDate(data.getLong(SportteamContract.EventEntry.COLUMN_DATE));
            mNewEventView.showEventTotalPlayers(data.getInt(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS));
            mNewEventView.showEventEmptyPlayers(data.getInt(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS));
        } else {
            mNewEventView.clearUI();
        }
    }
}
