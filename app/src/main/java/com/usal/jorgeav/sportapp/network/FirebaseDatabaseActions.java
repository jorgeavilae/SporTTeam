package com.usal.jorgeav.sportapp.network;

import android.content.ContentValues;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorge Avila on 18/05/2017.
 */

public class FirebaseDatabaseActions {
    public static final String TAG = FirebaseDatabaseActions.class.getSimpleName();

    public static void loadEvents(final Context context/*, query arguments*/) {
        String myCity = FirebaseDBContract.MyCity;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventsRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.CITY;

        eventsRef.orderByChild(filter).equalTo(myCity).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<ContentValues> cvArray = new ArrayList<ContentValues>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Event event = Utiles.datasnapshotToEvent(data);
                        cvArray.add(Utiles.eventToContentValues(event));
                    }
                    context.getContentResolver()
                            .bulkInsert(SportteamContract.EventEntry.CONTENT_EVENT_URI,
                                    cvArray.toArray(new ContentValues[cvArray.size()]));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void loadFields(final Context context) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fieldsRef = database.getReference(FirebaseDBContract.TABLE_FIELDS);

        fieldsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<ContentValues> cvArray = new ArrayList<ContentValues>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        List<Field> fields = Utiles.datasnapshotToField(data);
                        cvArray.addAll(Utiles.fieldsArrayToContentValues(fields));
                    }
                    context.getContentResolver()
                            .bulkInsert(SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                                    cvArray.toArray(new ContentValues[cvArray.size()]));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void loadProfile(Context context) {
//        //With FirebaseUser.getUID()
//        Log.d(TAG, "loadMiProfile (Network Call)");
//        //On callback: load friends, events, and get notifications
//
//        //And with the data...
//        String mId = "67ht67ty9hi485g94u5hi";
//        String mEmail = "email@email.com";
//        String mName = "Nombre Apellido";
//        String mCity = "Ciudad, Pais";
//        int mAge = 30;
//        String mPhotoUrl = "cadena de texto que es una url";
//
//        ContentValues cv = new ContentValues();
//        cv.put(SportteamContract.UserEntry.USER_ID, mId);
//        cv.put(SportteamContract.UserEntry.EMAIL, mEmail);
//        cv.put(SportteamContract.UserEntry.NAME, mName);
//        cv.put(SportteamContract.UserEntry.CITY, mCity);
//        cv.put(SportteamContract.UserEntry.AGE, mAge);
//        cv.put(SportteamContract.UserEntry.PHOTO, mPhotoUrl);
//
//        context.getContentResolver()
//                .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cv);
    }
}
