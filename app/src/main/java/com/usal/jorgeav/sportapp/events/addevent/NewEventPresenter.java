package com.usal.jorgeav.sportapp.events.addevent;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.network.FirebaseDBContract;

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
            long dateMillis = Utiles.stringDateToMillis(date);
            long timeMillis = Utiles.stringTimeToMillis(time);
            Event event = new Event(
                    "", sport, field, city, dateMillis + timeMillis,
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    Integer.valueOf(total), Integer.valueOf(total));

            DatabaseReference eventsRef = FirebaseDatabase.getInstance()
                    .getReference(FirebaseDBContract.TABLE_EVENTS);
            DatabaseReference myUserEventCreatedRef = FirebaseDatabase.getInstance()
                    .getReference(FirebaseDBContract.TABLE_USERS)
                    .child(event.getmOwner())
                    .child(FirebaseDBContract.User.EVENTS_CREATED);
            DatabaseReference fieldsNextEventsRef = FirebaseDatabase.getInstance()
                    .getReference(FirebaseDBContract.TABLE_FIELDS)
                    .child(event.getmField())
                    .child(FirebaseDBContract.Field.NEXT_EVENTS);

            long currentTime = System.currentTimeMillis();
            event.setmId(eventsRef.push().getKey());

            eventsRef.child(event.getmId()).setValue(event.toMap());
            myUserEventCreatedRef.child(event.getmId()).setValue(currentTime);
            fieldsNextEventsRef.child(event.getmId()).setValue(currentTime);

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
        long dateMillis = Utiles.stringDateToMillis(date);
        long timeMillis = Utiles.stringTimeToMillis(time);
        return System.currentTimeMillis() < dateMillis+timeMillis;
    }

    private boolean isPlayersCorrect(String total, String empty) {
        if (!TextUtils.isEmpty(total) && !TextUtils.isEmpty(empty))
            return Integer.valueOf(total) >= Integer.valueOf(empty);
        return false;
    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return null;
    }
}
