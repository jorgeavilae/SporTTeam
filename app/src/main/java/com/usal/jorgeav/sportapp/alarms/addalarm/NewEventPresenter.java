package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.network.FirebaseActions;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class NewEventPresenter implements NewEventContract.Presenter {

    NewEventContract.View mNewEventView;

    public NewEventPresenter(NewEventContract.View view){
        this.mNewEventView = view;
    }

    @Override
    public void addEvent(String sport, String field, String city, String date, String time, String total, String empty) {
        if (isValidSport(sport) && isValidField(field) && isDateTimeCorrect(date, time) && isPlayersCorrect(total, empty)) {
            long dateMillis = UtilesTime.stringDateToMillis(date);
            long timeMillis = UtilesTime.stringTimeToMillis(time);
            Event event = new Event(
                    "", sport, field, city, dateMillis + timeMillis,
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    Integer.valueOf(total), Integer.valueOf(total));

            FirebaseActions.addEvent(event);

            ((AppCompatActivity)mNewEventView.getActivityContext()).onBackPressed();
        } else
            Toast.makeText(mNewEventView.getActivityContext(), "Error: algun campo vacio", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidSport(String sport) {
        //TODO
        return true;
    }

    private boolean isValidField(String field) {
        //TODO
        return !TextUtils.isEmpty(field);
    }

    private boolean isDateTimeCorrect(String date, String time) {
        long dateMillis = UtilesTime.stringDateToMillis(date);
        long timeMillis = UtilesTime.stringTimeToMillis(time);
        return System.currentTimeMillis() < dateMillis+timeMillis;
    }

    private boolean isPlayersCorrect(String total, String empty) {
        if (!TextUtils.isEmpty(total) && !TextUtils.isEmpty(empty))
            return Integer.valueOf(total) >= Integer.valueOf(empty);
        return false;
    }
}
