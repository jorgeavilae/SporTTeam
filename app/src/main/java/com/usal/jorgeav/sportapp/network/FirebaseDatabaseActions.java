package com.usal.jorgeav.sportapp.network;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.MainActivity;
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

    public static void loadMyProfile(final Context context) {
        final String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(myUserID + "/" + FirebaseDBContract.DATA)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    MainActivity.myLoggedUser = Utiles.datasnapshotToUser(dataSnapshot, myUserID);
                    ContentValues cv = Utiles.userToContentValues(MainActivity.myLoggedUser);
                    context.getContentResolver()
                            .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cv);

                    loadFieldsFromCity(context, MainActivity.myLoggedUser.getmCity()); //Intalaciones: una vez
                    loadEventsFromCity(context, MainActivity.myLoggedUser.getmCity()); //Eventos de mi ciudad: cuando cambie (para la alarma)
                } else {
                    new Exception
                            ("User with UID: " + myUserID + " does not exists")
                            .printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        On callback: load friends, events, and get notifications
//        FirebaseDatabaseActions.loadFields(getApplicationContext()); //Intalaciones: una vez
//        FirebaseDatabaseActions.loadEvents(getApplicationContext()); //Eventos de mi ciudad: cuando cambie (para la alarma)
    }

    public static void loadFieldsFromCity(final Context context, String city) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fieldsRef = database.getReference(FirebaseDBContract.TABLE_FIELDS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Field.CITY;

        fieldsRef.orderByChild(filter).equalTo(city).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void loadEventsFromCity(final Context context, String city) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventsRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.CITY;

        Log.d(TAG, filter+"="+city);
        eventsRef.orderByChild(filter).equalTo(city).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, dataSnapshot.toString());
                Log.d(TAG, dataSnapshot.exists()+"");
                if (dataSnapshot.exists()) {
                    Event event = Utiles.datasnapshotToEvent(dataSnapshot);
                    ContentValues cv = Utiles.eventToContentValues(event);
                    Log.d(TAG, context.getContentResolver()
                            .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv).toString());
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
