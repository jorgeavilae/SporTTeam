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
import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jorge Avila on 18/05/2017.
 */

public class FirebaseDatabaseActions {
    public static final String TAG = FirebaseDatabaseActions.class.getSimpleName();

    private static HashMap<DatabaseReference, ChildEventListener> listenerMap = new HashMap<>();

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
                    ContentValues cvData = Utiles.datasnapshotFriendToContentValues(dataSnapshot, myUserID);
                    MyApplication.getAppContext().getContentResolver()
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
                            ContentValues cvData = Utiles.datasnapshotFriendRequestToContentValues(dataSnapshot, myUserID, true);
                            MyApplication.getAppContext().getContentResolver()
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
                    ContentValues cvData = Utiles.datasnapshotFriendRequestToContentValues(dataSnapshot, myUserID, false);
                    MyApplication.getAppContext().getContentResolver()
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
        };
        myUserRef.addChildEventListener(childEventListener);
        listenerMap.put(myUserRef, childEventListener);
    }

    private static void loadEventsFromMyOwnEvents() {
        String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.EVENTS_CREATED);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAnEvent(dataSnapshot.getKey());

                    //TODO cargar Usuarios que quieren entrar a este evento USER_REQUESTS
                    loadUsersFromUserRequests(dataSnapshot.getKey());
                    //TODO cargar Usuarios que no contestaron a invitaciones para este evento INVITATIONS
                    loadUsersFromInvitationsSent(dataSnapshot.getKey());
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
        };
        myUserRef.addChildEventListener(childEventListener);
//        No es necesario porque se actualiza manualmente cuando se crea un evento nuevo
//        listenerMap.put(myUserRef, childEventListener);
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
                    ContentValues cvData = Utiles
                            .datasnapshotEventsParticipationToContentValues(dataSnapshot, myUserID, true);
                    MyApplication.getAppContext().getContentResolver()
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
                    ContentValues cvData = Utiles
                            .datasnapshotEventInvitationsToContentValues(dataSnapshot, myUserID, true);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI, cvData);
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
                    ContentValues cvData = Utiles
                            .datasnapshotEventsRequestsToContentValues(dataSnapshot, myUserID, true);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI, cvData);
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
        };
        myUserRef.addChildEventListener(childEventListener);
        listenerMap.put(myUserRef, childEventListener);
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
                    ContentValues cvData = Utiles
                            .datasnapshotEventsRequestsToContentValues(dataSnapshot, eventId, false);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI, cvData);
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
                    ContentValues cvData = Utiles
                            .datasnapshotEventInvitationsToContentValues(dataSnapshot, eventId, false);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI, cvData);
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
        };
        myUserRef.addChildEventListener(childEventListener);
        listenerMap.put(myUserRef, childEventListener);
    }

    private static void loadAProfile(String userID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User anUser = Utiles.datasnapshotToUser(dataSnapshot);
                            ContentValues cvData = Utiles.dataUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                            List<ContentValues> cvSports = Utiles.sportUserToContentValues(anUser);
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
    private static void loadAnEvent(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);

        myUserRef.child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Event e = Utiles.datasnapshotToEvent(dataSnapshot);
                            ContentValues cv = Utiles.eventToContentValues(e);
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
    private static void loadAField(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_FIELDS);

        myUserRef.child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList<ContentValues> cvArray = new ArrayList<ContentValues>();
                            List<Field> fields = Utiles.datasnapshotToFieldList(dataSnapshot);
                            cvArray.addAll(Utiles.fieldsArrayToContentValues(fields));

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
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.exists()) {
                            loadAProfile(dataSnapshot.getKey());

                            String eventId = Uri.parse(dataSnapshot.getRef().getParent().getParent().toString()).getLastPathSegment();
                            ContentValues cvData = Utiles
                                    .datasnapshotEventsParticipationToContentValues(dataSnapshot, eventId, false);
                            MyApplication.getAppContext().getContentResolver()
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

    private static void loadFieldsFromCity(String city) {
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
                        List<Field> fields = Utiles.datasnapshotToFieldList(data);
                        cvArray.addAll(Utiles.fieldsArrayToContentValues(fields));
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
    private static void loadEventsFromCity(String city) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventsRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.CITY;

        eventsRef.orderByChild(filter).equalTo(city)
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Event e = Utiles.datasnapshotToEvent(dataSnapshot);
                    ContentValues cv = Utiles.eventToContentValues(e);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                    loadAField(e.getmField());
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
    private static void loadUsersFromCity(String city) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference(FirebaseDBContract.TABLE_USERS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.CITY;

        usersRef.orderByChild(filter).equalTo(city)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            User anUser = Utiles.datasnapshotToUser(dataSnapshot);
                            ContentValues cvData = Utiles.dataUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                            List<ContentValues> cvSports = Utiles.sportUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            cvSports.toArray(new ContentValues[cvSports.size()]));
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

    public static void loadProfilesWithName(String username) {
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
                                User anUser = Utiles.datasnapshotToUser(data);
                                ContentValues cvData = Utiles.dataUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                                List<ContentValues> cvSports = Utiles.sportUserToContentValues(anUser);
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

    //TODO checks if childs exists
    //TODO add fromUid and toUid
    public static void sendFriendRequest(String otherUid) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        long currentTime = System.currentTimeMillis();

        //Set Friend Request Sent in my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(myUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_SENT)
                .child(otherUid).setValue(currentTime);

        //Set Friend Request Received in other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(otherUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED)
                .child(myUid).setValue(currentTime);
    }
    public static void cancelFriendRequest(String otherUid) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Delete Friend Request Sent in my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(myUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_SENT).child(otherUid).removeValue();

        //Delete Friend Request Received in other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(otherUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED).child(myUid).removeValue();
    }
    public static void acceptFriendRequest(String otherUid) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        long currentTime = System.currentTimeMillis();

        //Add Friend to my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(myUid)
                .child(FirebaseDBContract.User.FRIENDS).child(otherUid).setValue(currentTime);

        //Add Friend to other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(otherUid)
                .child(FirebaseDBContract.User.FRIENDS).child(myUid).setValue(currentTime);

        //Delete Friend Request Received in my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(myUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED).child(otherUid).removeValue();

        //Delete Friend Request Sent in other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(otherUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_SENT).child(myUid).removeValue();
    }
    public static void declineFriendRequest(String otherUid) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Delete Friend Request Received in my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(myUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED).child(otherUid).removeValue();

        //Delete Friend Request Sent in other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(otherUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_SENT).child(myUid).removeValue();
    }
    public static void deleteFriend(String otherUid) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Delete Friend to my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(myUid)
                .child(FirebaseDBContract.User.FRIENDS).child(otherUid).removeValue();

        //Delete Friend to other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(otherUid)
                .child(FirebaseDBContract.User.FRIENDS).child(myUid).removeValue();

    }

    //TODO checks if childs exists and empty player and total player counts
    //TODO COMPROBAR SI FUNCIONAN
    public static void sendInvitationToThisEvent(String eventId, String uid) {
        long currentTime = System.currentTimeMillis();

        //Set Invitation Sent in my Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.INVITATIONS)
                .child(uid).setValue(currentTime);

        //Set Invitation Received in other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_INVITATIONS)
                .child(eventId).setValue(currentTime);
    }
    public static void deleteInvitationToThisEvent(String eventId, String uid) {
        //Delete Invitation Sent in my Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.INVITATIONS)
                .child(uid).removeValue();

        //Set Invitation Received in other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_INVITATIONS)
                .child(eventId).removeValue();
    }
    public static void sendEventRequest(String uid, String eventId) {
        long currentTime = System.currentTimeMillis();

        //Set User Request in that Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.USER_REQUESTS)
                .child(uid).setValue(currentTime);

        //Set Event Request in that my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_REQUESTS)
                .child(eventId).setValue(currentTime);
    }
    public static void cancelEventRequest(String uid, String eventId) {
        //Delete User Request in that Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.USER_REQUESTS)
                .child(uid).removeValue();

        //Delete Event Request in that my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_REQUESTS)
                .child(eventId).removeValue();
    }
    public static void acceptEventInvitation(String uid, String eventId) {
        //Add Assistant Event to my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_PARTICIPATION).child(eventId).setValue(true);

        //Add Assistant User to that Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.PARTICIPANTS).child(uid).setValue(true);
        //TODO update empty Players with Transaction

        //Delete Event Invitation Received in my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_INVITATIONS).child(eventId).removeValue();

        //Delete Event Invitation Sent in that Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.INVITATIONS).child(uid).removeValue();
    }
    public static void declineEventInvitation(String uid, String eventId) {
        //Delete Event Invitation Received in my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_INVITATIONS).child(eventId).removeValue();

        //Delete Event Invitation Sent in that Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.INVITATIONS).child(uid).removeValue();
    }
    public static void quitEvent(String uid, String eventId) {
        //Delete Assistant Event to my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_PARTICIPATION).child(eventId).removeValue();

        //Delete Assistant User to that Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.PARTICIPANTS).child(uid).removeValue();
        //TODO update empty Players with Transaction
    }
    public static void acceptUserRequestToThisEvent(String uid, String eventId) {
        //Add Assistant Event to that User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_PARTICIPATION).child(eventId).setValue(true);

        //Add Assistant User to my Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.PARTICIPANTS).child(uid).setValue(true);
        //TODO update empty Players with Transaction

        //Delete Event Request Sent in that User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_REQUESTS).child(eventId).removeValue();

        //Delete Event Request Received in my Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.USER_REQUESTS).child(uid).removeValue();
    }
    public static void declineUserRequestToThisEvent(String uid, String eventId) {
        //Add Not Assistant Event to that User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_PARTICIPATION).child(eventId).setValue(false);

        //Add Not Assistant User to my Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.PARTICIPANTS).child(uid).setValue(false);
        //TODO update empty Players with Transaction

        //Delete Event Request Sent in that User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_REQUESTS).child(eventId).removeValue();

        //Delete Event Request Received in my Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.USER_REQUESTS).child(uid).removeValue();
    }
}
