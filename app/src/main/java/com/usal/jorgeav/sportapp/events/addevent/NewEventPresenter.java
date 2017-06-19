package com.usal.jorgeav.sportapp.events.addevent;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.FirebaseActions;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.HashMap;

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
        if (isValidSport(sport) && isValidField(field, sport) && isDateTimeCorrect(date, time) && isPlayersCorrect(total, empty)) {
            long dateMillis = UtilesTime.stringDateToMillis(date);
            long timeMillis = UtilesTime.stringTimeToMillis(time);
            Event event = new Event(
                    "", sport, field, city, dateMillis + timeMillis,
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    Integer.valueOf(total), Integer.valueOf(total), new HashMap<String, Boolean>());

            FirebaseActions.addEvent(event);

            ((AppCompatActivity)mNewEventView.getActivityContext()).onBackPressed();
        } else
            Toast.makeText(mNewEventView.getActivityContext(), "Error: algun campo vacio", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidSport(String sport) {
        // If R.array.sport_id contains this sport
        String[] arraySports = mNewEventView.getActivityContext().getResources().getStringArray(R.array.sport_id);
        for (String sportArr : arraySports)
            if (sport.equals(sportArr)) return true;
        return false;
    }

    private boolean isValidField(String field, String sport) {
        // Check if the sport doesn't need a field
        String[] arraySports = mNewEventView.getActivityContext().getResources().getStringArray(R.array.sport_id);
        if (sport.equals(arraySports[0]) || sport.equals(arraySports[1])) return /* todo isValidAddress ?? */ true;

        // Query database for the fieldId and checks if this sport exists
        Cursor c = SportteamLoader.simpleQueryFieldId(mNewEventView.getActivityContext(), field);
        try {
            while (c.moveToNext())
                if (c.getString(SportteamContract.FieldEntry.COLUMN_SPORT).equals(sport)) {
                    c.close(); return true;
                }
        } finally { c.close(); }
        return false;
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
