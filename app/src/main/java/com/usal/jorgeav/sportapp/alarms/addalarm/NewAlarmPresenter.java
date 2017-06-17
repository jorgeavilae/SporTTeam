package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.Alarm;
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
        if (isValidSport(sport) && isValidField(field) && isDateCorrect(dateFrom, dateTo)
                && isPlayersCorrect(totalFrom, totalTo, emptyFrom, emptyTo)) {
            long dateFromMillis = UtilesTime.stringDateToMillis(dateFrom);
            long dateToMillis = UtilesTime.stringDateToMillis(dateTo);
            Alarm alarm = new Alarm("", sport, field, city, dateFromMillis, dateToMillis,
                    Integer.valueOf(totalFrom), Integer.valueOf(totalTo),
                    Integer.valueOf(emptyFrom), Integer.valueOf(emptyTo));

            FirebaseActions.addAlarm(alarm, FirebaseAuth.getInstance().getCurrentUser().getUid());

            ((AppCompatActivity)mNewAlarmView.getActivityContext()).onBackPressed();
        } else {
            Log.e(TAG, "addAlarm: isValidSport "+isValidSport(sport));
            Log.e(TAG, "addAlarm: isValidField "+isValidField(field));
            Log.e(TAG, "addAlarm: isDateCorrect "+isDateCorrect(dateFrom, dateTo));
            Log.e(TAG, "addAlarm: isPlayersCorrect "+isPlayersCorrect(totalFrom, totalTo, emptyFrom, emptyTo));
            Toast.makeText(mNewAlarmView.getActivityContext(), "Error: algun campo vacio", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidSport(String sport) {
        //TODO
        return true;
    }

    private boolean isValidField(String field) {
        //TODO
        return !TextUtils.isEmpty(field);
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
