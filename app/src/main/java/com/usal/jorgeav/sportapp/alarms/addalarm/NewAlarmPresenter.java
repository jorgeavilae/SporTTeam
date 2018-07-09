package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.alarms.alarmdetail.DetailAlarmFragment;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.mainactivities.AlarmsActivity;
import com.usal.jorgeav.sportapp.network.firebase.actions.AlarmFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;

class NewAlarmPresenter implements NewAlarmContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = NewAlarmPresenter.class.getSimpleName();

    private NewAlarmContract.View mNewAlarmView;

    NewAlarmPresenter(NewAlarmContract.View view){
        this.mNewAlarmView = view;
    }

    @Override
    public void addAlarm(String alarmId, String sport, String field, String city, String dateFrom, String dateTo,
                         String totalFrom, String totalTo, String emptyFrom, String emptyTo) {

        // Parse emptyPlayers string if needed (empty == "Infinite")
        if (mNewAlarmView.getActivityContext().getString(R.string.infinite).equals(emptyFrom))
            emptyFrom = "0";
        if (mNewAlarmView.getActivityContext().getString(R.string.infinite).equals(emptyTo))
            emptyTo = "0";

        Alarm a = new Alarm();
        a.setId(alarmId);

        // An alarm is valid if sport and city are necessarily set
        if (isValidSport(sport))
            a.setSport_id(sport);
        else {
            Toast.makeText(mNewAlarmView.getActivityContext(), R.string.toast_sport_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        // field could be null, but not city
        if (city == null || TextUtils.isEmpty(city))
            city = UtilesPreferences.getCurrentUserCity(mNewAlarmView.getActivityContext());

        if (isValidField(city, field, sport)) {
            a.setField_id(field);
            a.setCity(city);
        } else {
            Toast.makeText(mNewAlarmView.getActivityContext(), R.string.toast_place_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        // dateFrom must be at least today and dateTo should be greater than dateFrom or null
        if (isDateCorrect(dateFrom, dateTo)) {
            a.setDate_from(UtilesTime.stringDateToMillis(dateFrom));
            a.setDate_to(UtilesTime.stringDateToMillis(dateTo));
        } else {
            Toast.makeText(mNewAlarmView.getActivityContext(), R.string.toast_date_period_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        // totalFrom could be null and totalTo should be greater than totalFrom or null
        if (isTotalPlayersCorrect(totalFrom, totalTo)) {
            if (!TextUtils.isEmpty(totalFrom)) a.setTotal_players_from(Long.valueOf(totalFrom)); else a.setTotal_players_from(null);
            if (!TextUtils.isEmpty(totalTo)) a.setTotal_players_to(Long.valueOf(totalTo)); else a.setTotal_players_to(null);
        } else {
            Toast.makeText(mNewAlarmView.getActivityContext(), R.string.toast_total_player_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        // emptyFrom must be at least 1 and emptyTo should be greater than emptyFrom or null
        if (emptyFrom == null || TextUtils.isEmpty(emptyFrom)) emptyFrom = "1";
        if (isEmptyPlayersCorrect(emptyFrom, emptyTo)) {
            a.setEmpty_players_from(Long.valueOf(emptyFrom));
            if (!TextUtils.isEmpty(emptyTo)) a.setEmpty_players_to(Long.valueOf(emptyTo)); else a.setEmpty_players_to(null);
        } else {
            Toast.makeText(mNewAlarmView.getActivityContext(), R.string.toast_empty_players_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        long totalPlayersFrom = (a.getTotal_players_from()!=null ? a.getTotal_players_from():-1);
        long emptyPlayersTo = (a.getEmpty_players_to()!=null ? a.getEmpty_players_to():-1);
        if (totalPlayersFrom < emptyPlayersTo && emptyPlayersTo > 0) {
            Toast.makeText(mNewAlarmView.getActivityContext(), R.string.toast_players_relation_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        String myUserId = Utiles.getCurrentUserId();
        if (myUserId == null || TextUtils.isEmpty(myUserId)) return;
        AlarmFirebaseActions.addAlarm(a, myUserId);
        NotificationsFirebaseActions.checkOneAlarmAndNotify(a);
        ((AlarmsActivity)mNewAlarmView.getActivityContext()).mFieldId = null;
        ((AlarmsActivity)mNewAlarmView.getActivityContext()).mCity = null;
        ((AlarmsActivity)mNewAlarmView.getActivityContext()).mCoord = null;
        ((BaseFragment)mNewAlarmView).resetBackStack();
    }

    private boolean isValidSport(String sport) {
        if (!TextUtils.isEmpty(sport)) {
            // If R.array.sport_id contains this sport
            String[] arraySports = mNewAlarmView.getActivityContext().getResources().getStringArray(R.array.sport_id_values);
            for (String sportArr : arraySports)
                if (sport.equals(sportArr)) return true;
        }
        return false;
    }

    private boolean isValidField(String city, String fieldId, String sportId) {
        if (city != null && !TextUtils.isEmpty(city)) {
            // Check if the sport doesn't need a field
            String[] arraySports = mNewAlarmView.getActivityContext().getResources().getStringArray(R.array.sport_id_values);
            if (sportId.equals(arraySports[0]) || sportId.equals(arraySports[1]))
                return true;

            if (fieldId != null && !TextUtils.isEmpty(fieldId)) {
                // Query database for the fieldId and checks if this sport exists
                Field field = UtilesContentProvider.getFieldFromContentProvider(fieldId);

                if (field != null && field.getCity().equals(city)
                        && field.containsSportCourt(sportId)) return true;
                else {
                    Log.e(TAG, "isValidField: not valid");
                    return false;
                }
            } else
                return true; //Could be null
        }
        Log.e(TAG, "isValidField: city not valid");
        return false;
    }

    private boolean isDateCorrect(String dateFrom, String dateTo) {
        Long dateFromMillis = null, dateToMillis = null;
        if (!TextUtils.isEmpty(dateFrom))
            dateFromMillis = UtilesTime.stringDateToMillis(dateFrom);
        if (!TextUtils.isEmpty(dateTo))
            dateToMillis = UtilesTime.stringDateToMillis(dateTo);

        if (dateFromMillis != null && dateFromMillis > 0)
            if (dateToMillis != null && dateToMillis > 0)
                return (DateUtils.isToday(dateFromMillis) || System.currentTimeMillis() < dateFromMillis)
                                && dateFromMillis <= dateToMillis;
            else
                return DateUtils.isToday(dateFromMillis) || System.currentTimeMillis() < dateFromMillis;
        return false;
    }

    private boolean isTotalPlayersCorrect(String totalFrom, String totalTo) {
        if (TextUtils.isEmpty(totalFrom))
            return TextUtils.isEmpty(totalTo);
        return !TextUtils.isEmpty(totalTo) && Integer.valueOf(totalFrom) <= Integer.valueOf(totalTo);
    }

    private boolean isEmptyPlayersCorrect(String emptyFrom, String emptyTo) {
        return !TextUtils.isEmpty(emptyFrom) && Integer.valueOf(emptyFrom) >= 0 &&
                (TextUtils.isEmpty(emptyTo) || Integer.valueOf(emptyFrom) <= Integer.valueOf(emptyTo));

    }

    @Override
    public void openAlarm(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewAlarmFragment.BUNDLE_ALARM_ID)) {
            loaderManager.initLoader(SportteamLoader.LOADER_ALARM_ID, b, this);
        }
    }

    @Override
    public void loadFields(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewAlarmFragment.BUNDLE_SPORT_SELECTED_ID))
            loaderManager.initLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT, b, this);
    }

    @Override
    public void stopLoadFields(LoaderManager loaderManager) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String alarmId = args.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
        switch (id) {
            case SportteamLoader.LOADER_ALARM_ID:
                return SportteamLoader
                        .cursorLoaderOneAlarm(mNewAlarmView.getActivityContext(), alarmId);
            case SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT:
                String city = UtilesPreferences.getCurrentUserCity(mNewAlarmView.getActivityContext());
                String sportId = args.getString(NewAlarmFragment.BUNDLE_SPORT_SELECTED_ID);
                return SportteamLoader
                        .cursorLoaderFieldsFromCityWithSport(mNewAlarmView.getActivityContext(), city, sportId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_ALARM_ID:
                showAlarmDetails(data);
                break;
            case SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT:
                ArrayList<Field> dataList = UtilesContentProvider.cursorToMultipleField(data);
                mNewAlarmView.retrieveFields(dataList);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_ALARM_ID:
                showAlarmDetails(null);
                break;
            case SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT:
                mNewAlarmView.retrieveFields(null);
                break;
        }
    }

    private void showAlarmDetails(Cursor data) {
        Alarm a = UtilesContentProvider.cursorToSingleAlarm(data);
        if (a != null) {
            mNewAlarmView.showAlarmSport(a.getSport_id());
            mNewAlarmView.showAlarmField(a.getField_id(), a.getCity());
            mNewAlarmView.showAlarmDate(a.getDate_from(), a.getDate_to());
            mNewAlarmView.showAlarmTotalPlayers(a.getTotal_players_from(), a.getTotal_players_to());
            mNewAlarmView.showAlarmEmptyPlayers(a.getEmpty_players_from(), a.getEmpty_players_to());
        } else {
            mNewAlarmView.clearUI();
        }
    }
}
