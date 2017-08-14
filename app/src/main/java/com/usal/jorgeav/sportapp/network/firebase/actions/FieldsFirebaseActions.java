package com.usal.jorgeav.sportapp.network.firebase.actions;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.sync.FieldsFirebaseSync;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jorge Avila on 14/08/2017.
 */

public class FieldsFirebaseActions {
    public static final String TAG = FieldsFirebaseActions.class.getSimpleName();

    // Field
    public static void addField(Field field) {
        //Create fieldId
        DatabaseReference fieldTable = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_FIELDS);
        field.setId(fieldTable.push().getKey());

        //Set Field in Field Table
        String fieldInFieldTable = "/" + FirebaseDBContract.TABLE_FIELDS + "/" + field.getId();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(fieldInFieldTable, field.toMap());

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    public static void updateField(final Field field) {
        //Set Field in Field Table
        DatabaseReference fieldRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(field.getId());
        fieldRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                //Update field
                mutableData.child(FirebaseDBContract.DATA).setValue(field);

                //Update field data in Events
                Map<String, Object> childUpdates = new HashMap<>();
                for (MutableData md : mutableData.child(FirebaseDBContract.Field.NEXT_EVENTS).getChildren()) {
                    //Event reference
                    String eventRef = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + md.getKey() + "/"
                            + FirebaseDBContract.DATA;
                    childUpdates.put(eventRef + "/" + FirebaseDBContract.Event.ADDRESS, field.getAddress());
                    childUpdates.put(eventRef + "/" + FirebaseDBContract.Event.CITY, field.getCity());
                    childUpdates.put(eventRef + "/" + FirebaseDBContract.Event.COORD_LATITUDE, field.getCoord_latitude());
                    childUpdates.put(eventRef + "/" + FirebaseDBContract.Event.COORD_LONGITUDE, field.getCoord_longitude());
                }
                FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    public static void updateFieldSports(String fieldId, Map<String, Object> sportsMap) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(fieldId).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.Field.SPORT).setValue(sportsMap);
    }

    public static void addFieldSport(String fieldId, SportCourt sport) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(fieldId).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.Field.SPORT).child(sport.getSport_id()).setValue(sport.toMap());
    }

    public static DatabaseReference getFieldNextEventsReferenceWithId(String fieldId) {
        //Return reference to NextEvents in Field for checks.
        return FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(fieldId).child(FirebaseDBContract.Field.NEXT_EVENTS);
    }

    public static void deleteField(String fieldId) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_FIELDS)
            .child(fieldId).setValue(null);
    }

    public static void voteField(final String fieldId, String sportId, final float rate) {
        //Add vote to count and recalculate average rating
        DatabaseReference fieldSportRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(fieldId)
                .child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.Field.SPORT)
                .child(sportId);
        fieldSportRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                SportCourt sport = mutableData.getValue(SportCourt.class);
                if (sport == null) return Transaction.success(mutableData);

                double newRating = (sport.getPunctuation()*sport.getVotes() + rate) / (sport.getVotes()+1);
                sport.setVotes(sport.getVotes() + 1);

                // Round to .5 or .0 https://stackoverflow.com/a/23449769/4235666
                sport.setPunctuation(Math.round(newRating * 2) / 2.0);

                mutableData.setValue(sport);

                FieldsFirebaseSync.loadAField(fieldId);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "voteField: onComplete:" + databaseError);
            }
        });
    }
}
