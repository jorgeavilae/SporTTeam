package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.FirebaseActions;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class NewAlarmPresenter implements NewAlarmContract.Presenter {
    private static final String TAG = NewAlarmPresenter.class.getSimpleName();

    NewAlarmContract.View mNewAlarmView;

    public NewAlarmPresenter(NewAlarmContract.View view){
        this.mNewAlarmView = view;
    }

    @Override
    public void addAlarm(String sport, String field, String city, String dateFrom, String dateTo,
                         String totalFrom, String totalTo, String emptyFrom, String emptyTo) {
        if (isValidSport(sport) && isValidField(field, sport) && isDateCorrect(dateFrom, dateTo)
                && isPlayersCorrect(totalFrom, totalTo, emptyFrom, emptyTo)) {
            long dateFromMillis = UtilesTime.stringDateToMillis(dateFrom);
            long dateToMillis = UtilesTime.stringDateToMillis(dateTo);
            Alarm alarm = new Alarm("", sport, field, city, dateFromMillis, dateToMillis,
                    Integer.valueOf(totalFrom), Integer.valueOf(totalTo),
                    Integer.valueOf(emptyFrom), Integer.valueOf(emptyTo));

            FirebaseActions.addAlarm(alarm, FirebaseAuth.getInstance().getCurrentUser().getUid());

            ((AppCompatActivity)mNewAlarmView.getActivityContext()).onBackPressed();
        } else
            Toast.makeText(mNewAlarmView.getActivityContext(), "Error: algun campo vacio", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidSport(String sport) {
        // If R.array.sport_id contains this sport
        String[] arraySports = mNewAlarmView.getActivityContext().getResources().getStringArray(R.array.sport_id);
        for (String sportArr : arraySports)
            if (sport.equals(sportArr)) return true;
        return false;
    }

    private boolean isValidField(String field, String sport) {
        // Check if the sport doesn't need a field
        String[] arraySports = mNewAlarmView.getActivityContext().getResources().getStringArray(R.array.sport_id);
        if (sport.equals(arraySports[0]) || sport.equals(arraySports[1])) return /* todo isValidAddress ?? */ true;

        // Query database for the fieldId and checks if this sport exists for that field
        Cursor c = SportteamLoader.simpleQueryFieldId(mNewAlarmView.getActivityContext(), field);
        try {
            while (c.moveToNext())
                if (c.getString(SportteamContract.FieldEntry.COLUMN_SPORT).equals(sport)) {
                    c.close(); return true;
                }
        } finally { c.close(); }
        return false;
    }

    private boolean isDateCorrect(String dateFrom, String dateTo) {
        long dateFromMillis = UtilesTime.stringDateToMillis(dateFrom);
        long dateToMillis = UtilesTime.stringDateToMillis(dateTo);
        return System.currentTimeMillis() < dateFromMillis && dateFromMillis < dateToMillis;
    }

    private boolean isPlayersCorrect(String totalFrom, String totalTo, String emptyFrom, String emptyTo) {
        if (!TextUtils.isEmpty(totalFrom) && !TextUtils.isEmpty(totalTo)
                && !TextUtils.isEmpty(emptyFrom) && !TextUtils.isEmpty(emptyTo))
            return Integer.valueOf(totalFrom) <= Integer.valueOf(totalTo)
                    && Integer.valueOf(emptyFrom) <= Integer.valueOf(emptyTo)
                    && Integer.valueOf(totalFrom) >= Integer.valueOf(emptyTo);
        return false;
    }
}
