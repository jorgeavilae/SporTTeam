package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.alarms.alarmdetail.DetailAlarmFragment;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.mainactivities.AlarmsActivity;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class NewAlarmPresenter implements NewAlarmContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = NewAlarmPresenter.class.getSimpleName();

    NewAlarmContract.View mNewAlarmView;

    public NewAlarmPresenter(NewAlarmContract.View view){
        this.mNewAlarmView = view;
    }

    @Override
    public void addAlarm(String alarmId, String sport, String field, String city, String dateFrom, String dateTo,
                         String totalFrom, String totalTo, String emptyFrom, String emptyTo) {
        Alarm a = new Alarm();
        a.setmId(alarmId);

        // An alarm is valid if sport and city are necessarily set
        if (isValidSport(sport))
            a.setmSport(sport);
        else {
            Toast.makeText(mNewAlarmView.getActivityContext(), "Error en el deporte", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(city))
            a.setmCity(city);
        else {
            Toast.makeText(mNewAlarmView.getActivityContext(), "Error en la ciudad", Toast.LENGTH_SHORT).show();
            return;
        }

        // field could be null
        if (isValidField(field, sport))
            a.setmField(field);
        else {
            Toast.makeText(mNewAlarmView.getActivityContext(), "Error en el campo", Toast.LENGTH_SHORT).show();
            return;
        }

        // dateFrom must be at least today and dateTo should be greater than dateFrom or null
        if (isDateCorrect(dateFrom, dateTo)) {
            a.setmDateFrom(UtilesTime.stringDateToMillis(dateFrom));
            a.setmDateTo(UtilesTime.stringDateToMillis(dateTo));
        } else {
            Toast.makeText(mNewAlarmView.getActivityContext(), "Error en las fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        // totalFrom could be null and totalTo should be greater than totalFrom or null
        if (isTotalPlayersCorrect(totalFrom, totalTo)) {
            if (!TextUtils.isEmpty(totalFrom)) a.setmTotalPlayersFrom(Long.valueOf(totalFrom)); else a.setmTotalPlayersFrom(null);
            if (!TextUtils.isEmpty(totalTo)) a.setmTotalPlayersTo(Long.valueOf(totalTo)); else a.setmTotalPlayersTo(null);
        } else {
            Toast.makeText(mNewAlarmView.getActivityContext(), "Error en los puestos totales", Toast.LENGTH_SHORT).show();
            return;
        }

        // emptyFrom must be at least 1 and emptyTo should be greater than emptyFrom or null
        if (isEmptyPlayersCorrect(emptyFrom, emptyTo)) {
            a.setmEmptyPlayersFrom(Long.valueOf(emptyFrom));
            if (!TextUtils.isEmpty(emptyTo)) a.setmEmptyPlayersTo(Long.valueOf(emptyTo)); else a.setmEmptyPlayersTo(null);
        } else {
            Toast.makeText(mNewAlarmView.getActivityContext(), "Error en los puestos restantes", Toast.LENGTH_SHORT).show();
            return;
        }

        long totalPlayersFrom = (a.getmTotalPlayersFrom()!=null ? a.getmTotalPlayersFrom():-1);
        long emptyPlayersTo = (a.getmEmptyPlayersTo()!=null ? a.getmEmptyPlayersTo():-1);
        if (totalPlayersFrom < emptyPlayersTo) {
            Toast.makeText(mNewAlarmView.getActivityContext(), "Error en los puestos totales/restantes", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "addAlarm: "+a);
        FirebaseActions.addAlarm(a, FirebaseAuth.getInstance().getCurrentUser().getUid());
        ((AlarmsActivity)mNewAlarmView.getActivityContext()).newAlarmFieldSelected = null;
        ((AppCompatActivity)mNewAlarmView.getActivityContext()).onBackPressed();
    }

    private boolean isValidSport(String sport) {
        if (!TextUtils.isEmpty(sport)) {
            // If R.array.sport_id contains this sport
            String[] arraySports = mNewAlarmView.getActivityContext().getResources().getStringArray(R.array.sport_id);
            for (String sportArr : arraySports)
                if (sport.equals(sportArr)) return true;
        }
        return false;
    }

    private boolean isValidField(String field, String sport) {
        if (!TextUtils.isEmpty(field)) {
            // Check if the sport doesn't need a field
            String[] arraySports = mNewAlarmView.getActivityContext().getResources().getStringArray(R.array.sport_id);
            if (sport.equals(arraySports[0]) || sport.equals(arraySports[1]))
                return /* todo isValidAddress ?? */ true;

            // Query database for the fieldId and checks if this sport exists for that field
            Cursor c = SportteamLoader.simpleQueryFieldId(mNewAlarmView.getActivityContext(), field);
            try {
                while (c.moveToNext())
                    if (c.getString(SportteamContract.FieldEntry.COLUMN_SPORT).equals(sport)) {
                        c.close();
                        return true;
                    }
            } finally {
                c.close();
            }
            return false;
        }
        return true; //Could be null
    }

    private boolean isDateCorrect(String dateFrom, String dateTo) {
        long dateFromMillis = 0, dateToMillis = 0;
        if (!TextUtils.isEmpty(dateFrom))
            dateFromMillis = UtilesTime.stringDateToMillis(dateFrom);
        if (!TextUtils.isEmpty(dateTo))
            dateToMillis = UtilesTime.stringDateToMillis(dateTo);

        if (dateFromMillis > 0)
            if (dateToMillis > 0)
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
        return !TextUtils.isEmpty(emptyFrom) && Integer.valueOf(emptyFrom) > 0 &&
                (TextUtils.isEmpty(emptyTo) || Integer.valueOf(emptyFrom) <= Integer.valueOf(emptyTo));

    }

    @Override
    public void openAlarm(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewAlarmFragment.BUNDLE_ALARM_ID)) {
            loaderManager.initLoader(SportteamLoader.LOADER_ALARM_ID, b, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String alarmId = args.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
        switch (id) {
            case SportteamLoader.LOADER_ALARM_ID:
                return SportteamLoader
                        .cursorLoaderOneAlarm(mNewAlarmView.getActivityContext(), alarmId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        showAlarmDetails(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        showAlarmDetails(null);
    }

    private void showAlarmDetails(Cursor data) {
        Alarm a = UtilesContentProvider.cursorToSingleAlarm(data);
        if (a != null) {
            mNewAlarmView.showAlarmSport(a.getmSport());
            mNewAlarmView.showAlarmField(a.getmField());
            mNewAlarmView.showAlarmCity(a.getmCity());
            mNewAlarmView.showAlarmDate(a.getmDateFrom(), a.getmDateTo());
            mNewAlarmView.showAlarmTotalPlayers(a.getmTotalPlayersFrom(), a.getmTotalPlayersTo());
            mNewAlarmView.showAlarmEmptyPlayers(a.getmEmptyPlayersFrom(), a.getmEmptyPlayersTo());
        } else {
            mNewAlarmView.clearUI();
        }
    }
}
