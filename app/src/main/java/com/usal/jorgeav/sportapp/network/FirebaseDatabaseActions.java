package com.usal.jorgeav.sportapp.network;

import android.content.ContentValues;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.User;
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

        //Cargar Mis datos y deportes (cuando cambie)
        myUserRef.child(myUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User myLoggedUser = Utiles.datasnapshotToUser(dataSnapshot, myUserID);
                    ContentValues cvData = Utiles.dataUserToContentValues(myLoggedUser);
                    context.getContentResolver()
                            .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                    List<ContentValues> cvSports = Utiles.sportUserToContentValues(myLoggedUser);
                    context.getContentResolver()
                            .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                    cvSports.toArray(new ContentValues[cvSports.size()]));

                    //Cargar Instalaciones de mi ciudad (una vez) inmutable)
                    loadFieldsFromCity(context, myLoggedUser.getmCity());
                    //Cargar Eventos de mi ciudad (cuando cambie) para alarma)
                    loadEventsFromCity(context, myLoggedUser.getmCity());
                } else {
                    new Exception("User with UID: " + myUserID + " does not exists").printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //TODO Cargar Usuarios de mis amigos (cuando cambie)
        loadUsersFromFriends(context);
        //TODO Cargar Usuarios de mis peticiones de amistad (cuando cambie)
        loadUsersFromFriendsRequests(context);
        //TODO Cargar Eventos de mis eventos creados (cuando cambie)
        loadEventsFromMyOwnEvents(context);
        //TODO Cargar Eventos de mis eventos a los que asisto (cuando cambie)
        loadEventsFromMyNextEvents(context);
        //TODO Cargar Eventos de mis invitaciones a eventos (cuando cambie)
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

        eventsRef.orderByChild(filter).equalTo(city).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    ContentValues cv = Utiles.eventToContentValues(Utiles.datasnapshotToEvent(dataSnapshot));
                    context.getContentResolver()
                            .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
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

    private static void loadUsersFromFriendsRequests(final Context context) {
        final String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(myUserID + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.exists()) {
                            loadOtherProfile(context, dataSnapshot.getKey());

                            ContentValues cvData = Utiles.datasnapshotFriendRequestToContentValues(dataSnapshot, myUserID);
                            context.getContentResolver()
                                    .insert(SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI, cvData);
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

    private static void loadUsersFromFriends(final Context context) {
        final String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(myUserID + "/" + FirebaseDBContract.User.FRIENDS)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            loadOtherProfile(context, dataSnapshot.getKey());
                            ContentValues cvData = Utiles.datasnapshotFriendToContentValues(dataSnapshot, myUserID);
                            context.getContentResolver()
                                    .insert(SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI, cvData);
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

    private static void loadOtherProfile(final Context context, final String userID) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User anUser = Utiles.datasnapshotToUser(dataSnapshot, userID);
                            ContentValues cvData = Utiles.dataUserToContentValues(anUser);
                            context.getContentResolver()
                                    .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                            List<ContentValues> cvSports = Utiles.sportUserToContentValues(anUser);
                            context.getContentResolver()
                                    .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            cvSports.toArray(new ContentValues[cvSports.size()]));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private static void loadEventsFromMyOwnEvents(final Context context) {
        final String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(myUserID + "/" + FirebaseDBContract.User.EVENTS_CREATED)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.exists()) {
                            loadAnEvent(context, dataSnapshot.getKey());
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

    private static void loadEventsFromMyNextEvents(final Context context) {
        final String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(myUserID + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.exists()) {
                            loadAnEvent(context, dataSnapshot.getKey());

                            ContentValues cvData = Utiles
                                    .datasnapshotEventsParticipationToContentValues(dataSnapshot, myUserID);
                            context.getContentResolver()
                                    .insert(SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI, cvData);
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

    private static void loadAnEvent(final Context context, String key) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);

        myUserRef.child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ContentValues cv = Utiles.eventToContentValues(Utiles.datasnapshotToEvent(dataSnapshot));
                            context.getContentResolver()
                                    .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
