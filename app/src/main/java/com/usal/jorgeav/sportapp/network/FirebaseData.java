package com.usal.jorgeav.sportapp.network;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesDataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jorge Avila on 14/06/2017.
 */

public class FirebaseData {
    public static final String TAG = FirebaseData.class.getSimpleName();

    private static HashMap<DatabaseReference, ChildEventListener> listenerMap = new HashMap<>();

    // TODO: 16/06/2017 Las sincronizaciones que no se guarda el listener tienen que borrarse cuando se actualicen
    public static void syncFirebaseDatabase() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null && listenerMap.isEmpty()) {
            //TODO Cargar Mis datos y deportes (una vez y reload cuando lo cambie)
            loadAProfile(FirebaseAuth.getInstance().getCurrentUser().getUid());
            loadFieldsFromCity(Utiles.getCurrentCity(MyApplication.getAppContext(), FirebaseAuth.getInstance().getCurrentUser().getUid()));
            //TODO Cargar Lista de mis amigos (cuando cambie) y Usuario (una vez)
            loadUsersFromFriends();
            //TODO Cargar Usuarios de mis peticiones de amistad enviadas (cuando cambie)
            loadUsersFromFriendsRequestsSent();
            //TODO Cargar Usuarios de mis peticiones de amistad recibidas (cuando cambie)
            loadUsersFromFriendsRequestsReceived();
            //TODO Cargar Eventos de mis eventos creados (cuando cambie)
            loadEventsFromMyOwnEvents();
            //TODO Cargar Eventos de mis eventos a los que asisto (cuando cambie)
            loadEventsFromEventsParticipation();
            //TODO Cargar Eventos de mis invitaciones a eventos (cuando cambie)
            loadEventsFromInvitationsReceived();
            //TODO Cargar Eventos de mis peticiones de asistencia a eventos enviadas (cuando cambie)
            loadEventsFromEventsRequests();
            //TODO Cargar mis Alarmas (una vez)
            loadAlarmsFromMyAlarms();
        }
    }
    public static void detachListeners() {
        for (Map.Entry<DatabaseReference, ChildEventListener> entry : listenerMap.entrySet()) {
            Log.d(TAG, "detachListeners: ref "+entry.getKey());
            entry.getKey().removeEventListener(entry.getValue());
        }
        listenerMap.clear();
    }

    private static void loadUsersFromFriends() {
        String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.FRIENDS);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    loadAProfile(dataSnapshot.getKey());
                    String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ContentValues cvData = UtilesDataSnapshot.dataSnapshotFriendToContentValues(dataSnapshot, myUserID);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI, cvData);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                loadAProfile(dataSnapshot.getKey());
                String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ContentValues cvData = UtilesDataSnapshot.dataSnapshotFriendToContentValues(dataSnapshot, myUserID);
                MyApplication.getAppContext().getContentResolver()
                        .insert(SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI, cvData);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String userId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI,
                        SportteamContract.FriendsEntry.MY_USER_ID + " = ? AND "
                                + SportteamContract.FriendsEntry.USER_ID + " = ? ",
                        new String[]{myUserID, userId});

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myUserRef.addChildEventListener(childEventListener);
        listenerMap.put(myUserRef, childEventListener);
    }
    private static void loadUsersFromFriendsRequestsSent() {
        String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAProfile(dataSnapshot.getKey());

                    String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ContentValues cvData = UtilesDataSnapshot.dataSnapshotFriendRequestToContentValues(dataSnapshot, myUserID, true);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI, cvData);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String receiverId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                        SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ? AND "
                                + SportteamContract.FriendRequestEntry.SENDER_ID + " = ? ",
                        new String[]{receiverId, senderId});
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myUserRef.addChildEventListener(childEventListener);
        listenerMap.put(myUserRef, childEventListener);
    }
    private static void loadUsersFromFriendsRequestsReceived() {
        String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAProfile(dataSnapshot.getKey());

                    Log.d(TAG, "loadUsersFromFriendsRequestsReceived:onChildAdded: "+dataSnapshot.getKey());
                    String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ContentValues cvData = UtilesDataSnapshot.dataSnapshotFriendRequestToContentValues(dataSnapshot, myUserID, false);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI, cvData);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String receiverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String senderId = dataSnapshot.getKey();
                Log.d(TAG, "onChildRemoved: sender "+senderId);
                Log.d(TAG, "onChildRemoved: receiver "+receiverId);
                MyApplication.getAppContext().getContentResolver()
                        .delete(SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                                SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ? AND "
                                        + SportteamContract.FriendRequestEntry.SENDER_ID + " = ? ",
                                new String[]{receiverId, senderId});

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myUserRef.addChildEventListener(childEventListener);
        listenerMap.put(myUserRef, childEventListener);
    }

    static void loadEventsFromMyOwnEvents() {
        String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.EVENTS_CREATED);

        myUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.exists()) {
                        loadAnEvent(data.getKey());
                        //TODO cargar Usuarios que quieren entrar a este evento USER_REQUESTS
                        loadUsersFromUserRequests(data.getKey());
                        //TODO cargar Usuarios que no contestaron a invitaciones para este evento INVITATIONS
                        loadUsersFromInvitationsSent(data.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        No es necesario porque se actualiza manualmente cuando se crea un evento nuevo
//        listenerMap.put(myUserRef, childEventListener);
    }
    private static void loadUsersFromUserRequests(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(key + "/" + FirebaseDBContract.Event.USER_REQUESTS);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAProfile(dataSnapshot.getKey());

                    String eventId = Uri.parse(dataSnapshot.getRef().getParent().getParent().toString()).getLastPathSegment();
                    ContentValues cvData = UtilesDataSnapshot
                            .dataSnapshotEventsRequestsToContentValues(dataSnapshot, eventId, false);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI, cvData);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String eventId = Uri.parse(dataSnapshot.getRef().getParent().getParent().toString()).getLastPathSegment();
                String senderId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI,
                        SportteamContract.EventRequestsEntry.EVENT_ID + " = ? AND "
                                +SportteamContract.EventRequestsEntry.SENDER_ID + " = ? ",
                        new String[]{eventId, senderId});
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myUserRef.addChildEventListener(childEventListener);
        listenerMap.put(myUserRef, childEventListener);
    }
    private static void loadUsersFromInvitationsSent(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(key + "/" + FirebaseDBContract.Event.INVITATIONS);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAProfile(dataSnapshot.getKey());

                    String eventId = Uri.parse(dataSnapshot.getRef().getParent().getParent().toString()).getLastPathSegment();
                    ContentValues cvData = UtilesDataSnapshot
                            .dataSnapshotEventInvitationsToContentValues(dataSnapshot, eventId, false);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI, cvData);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String eventId = Uri.parse(dataSnapshot.getRef().getParent().getParent().toString()).getLastPathSegment();
                String userId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI,
                        SportteamContract.EventsInvitationEntry.EVENT_ID + " = ? AND "
                                +SportteamContract.EventsInvitationEntry.USER_ID + " = ? ",
                        new String[]{eventId, userId});
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myUserRef.addChildEventListener(childEventListener);
        listenerMap.put(myUserRef, childEventListener);
    }

    private static void loadEventsFromEventsParticipation() {
        String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAnEvent(dataSnapshot.getKey());

                    String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ContentValues cvData = UtilesDataSnapshot
                            .dataSnapshotEventsParticipationToContentValues(dataSnapshot, myUserID, true);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI, cvData);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String eventId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                        SportteamContract.EventsParticipationEntry.EVENT_ID + " = ? AND "
                                + SportteamContract.EventsParticipationEntry.USER_ID + " = ? ",
                        new String[]{eventId, myUserID});

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myUserRef.addChildEventListener(childEventListener);
        listenerMap.put(myUserRef, childEventListener);
    }
    private static void loadEventsFromInvitationsReceived() {
        String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAnEvent(dataSnapshot.getKey());

                    String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ContentValues cvData = UtilesDataSnapshot
                            .dataSnapshotEventInvitationsToContentValues(dataSnapshot, myUserID, true);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI, cvData);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String eventId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI,
                        SportteamContract.EventsInvitationEntry.EVENT_ID + " = ? AND "
                                +SportteamContract.EventsInvitationEntry.USER_ID + " = ? ",
                        new String[]{eventId, myUserID});
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myUserRef.addChildEventListener(childEventListener);
        listenerMap.put(myUserRef, childEventListener);
    }
    private static void loadEventsFromEventsRequests() {
        String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.EVENTS_REQUESTS);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    loadAnEvent(dataSnapshot.getKey());

                    String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ContentValues cvData = UtilesDataSnapshot
                            .dataSnapshotEventsRequestsToContentValues(dataSnapshot, myUserID, true);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI, cvData);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String eventId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI,
                        SportteamContract.EventRequestsEntry.EVENT_ID + " = ? AND "
                                +SportteamContract.EventRequestsEntry.SENDER_ID + " = ? ",
                        new String[]{eventId, myUserID});

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myUserRef.addChildEventListener(childEventListener);
        listenerMap.put(myUserRef, childEventListener);
    }
    
    static void loadAlarmsFromMyAlarms() {
        String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.ALARMS);

        myUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.exists()) {
                        Alarm a = UtilesDataSnapshot.dataSnapshotToAlarm(data);
                        ContentValues cv = UtilesDataSnapshot.alarmToContentValues(a);
                        MyApplication.getAppContext().getContentResolver()
                                .insert(SportteamContract.AlarmEntry.CONTENT_ALARM_URI, cv);
                        loadAField(a.getmField());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        No es necesario porque se actualiza manualmente cuando se crea una alarma nueva
//        listenerMap.put(myUserRef, childEventListener);
    }

    public static void loadAProfile(String userID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User anUser = UtilesDataSnapshot.dataSnapshotToUser(dataSnapshot);

                            ContentValues cvData = UtilesDataSnapshot.dataUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                            List<ContentValues> cvSports = UtilesDataSnapshot.sportUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .delete(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            SportteamContract.UserSportEntry.USER_ID + " = ? ",
                                            new String[]{anUser.getmId()});
                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            cvSports.toArray(new ContentValues[cvSports.size()]));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
    public static void loadAnAlarm(String alarmId) {
        String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.ALARMS);

        myUserRef.child(alarmId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Alarm a = UtilesDataSnapshot.dataSnapshotToAlarm(dataSnapshot);
                            ContentValues cv = UtilesDataSnapshot.alarmToContentValues(a);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.AlarmEntry.CONTENT_ALARM_URI, cv);
                            loadAField(a.getmField());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    public static void loadAnEvent(String eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);

        myUserRef.child(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Event e = UtilesDataSnapshot.dataSnapshotToEvent(dataSnapshot);
                            ContentValues cv = UtilesDataSnapshot.eventToContentValues(e);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                            loadAField(e.getmField());
                            //TODO cargar Usuarios que participan en este evento PARTICIPANTS
                            loadUsersFromParticipants(dataSnapshot.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    public static void loadAField(String fieldId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_FIELDS);

        myUserRef.child(fieldId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList<ContentValues> cvArray = new ArrayList<ContentValues>();
                            List<Field> fields = UtilesDataSnapshot.dataSnapshotToFieldList(dataSnapshot);
                            cvArray.addAll(UtilesDataSnapshot.fieldsArrayToContentValues(fields));

                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                                            cvArray.toArray(new ContentValues[cvArray.size()]));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    private static void loadUsersFromParticipants(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventsRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);

        eventsRef.child(key + "/" + FirebaseDBContract.Event.PARTICIPANTS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String eventId = Uri.parse(dataSnapshot.getRef().getParent().getParent().toString()).getLastPathSegment();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if(data.exists()) {
                                loadAProfile(data.getKey());

                                ContentValues cvData = UtilesDataSnapshot
                                        .dataSnapshotEventsParticipationToContentValues(data, eventId, false);
                                MyApplication.getAppContext().getContentResolver()
                                        .insert(SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI, cvData);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void loadFieldsFromCity(String city) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fieldsRef = database.getReference(FirebaseDBContract.TABLE_FIELDS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Field.CITY;

        fieldsRef.orderByChild(filter).equalTo(city)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList<ContentValues> cvArray = new ArrayList<ContentValues>();
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                List<Field> fields = UtilesDataSnapshot.dataSnapshotToFieldList(data);
                                cvArray.addAll(UtilesDataSnapshot.fieldsArrayToContentValues(fields));
                            }
                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                                            cvArray.toArray(new ContentValues[cvArray.size()]));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    public static void loadEventsFromCity(String city) {
        // TODO: 16/06/2017 comparar evento con alarmas
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventsRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.CITY;

        eventsRef.orderByChild(filter).equalTo(city)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Event e = UtilesDataSnapshot.dataSnapshotToEvent(data);
                                ContentValues cv = UtilesDataSnapshot.eventToContentValues(e);
                                MyApplication.getAppContext().getContentResolver()
                                        .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                                loadAField(e.getmField());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    public static void loadUsersFromCity(String city) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference(FirebaseDBContract.TABLE_USERS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.CITY;

        usersRef.orderByChild(filter).equalTo(city)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                User anUser = UtilesDataSnapshot.dataSnapshotToUser(data);

                                ContentValues cvData = UtilesDataSnapshot.dataUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                                List<ContentValues> cvSports = UtilesDataSnapshot.sportUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                                cvSports.toArray(new ContentValues[cvSports.size()]));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void loadUsersWithName(String username) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference(FirebaseDBContract.TABLE_USERS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.ALIAS;

        /* https://stackoverflow.com/a/40633692/4235666
         * https://firebase.google.com/docs/database/admin/retrieve-data */
        usersRef.orderByChild(filter).startAt(username).endAt(username+"\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                User anUser = UtilesDataSnapshot.dataSnapshotToUser(data);
                                ContentValues cvData = UtilesDataSnapshot.dataUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                                List<ContentValues> cvSports = UtilesDataSnapshot.sportUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                                cvSports.toArray(new ContentValues[cvSports.size()]));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    /* Esta llamada no es necesaria porque siempre va precedida de loadEventsFromCity
     * Esta llamada deberia ser loadEventsFromCityWithSport pero no se puede hacer en Firebase
     * Por ello para cargar eventos de una ciudad con un deporte se buscan en el Content Provider
     */
//    public static void loadEventsWithSport(String sportId) {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference eventsRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);
//        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.SPORT;
//
//        eventsRef.orderByChild(filter).equalTo(sportId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            for (DataSnapshot data : dataSnapshot.getChildren()) {
//                                Event e = UtilesDataSnapshot.dataSnapshotToEvent(data);
//                                ContentValues cv = UtilesDataSnapshot.eventToContentValues(e);
//                                MyApplication.getAppContext().getContentResolver()
//                                        .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
//                                loadAField(e.getmField());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//    }
}
