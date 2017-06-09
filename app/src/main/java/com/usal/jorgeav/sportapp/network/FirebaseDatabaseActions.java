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
import com.usal.jorgeav.sportapp.LoginActivity;
import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.Event;
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

    public static void loadMyProfile(final Context context, final LoginActivity loginActivity) {
        final String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        //TODO Cargar Mis datos y deportes (cuando cambie)
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

                    loginActivity.finishLoadMyProfile();

                    //TODO Cargar Instalaciones de mi ciudad (una vez) inmutable)
                    loadFieldsFromCity(context, myLoggedUser.getmCity());
                    //TODO Cargar Eventos de mi ciudad (cuando cambie) para alarma)
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
        //TODO Cargar Usuarios de mis peticiones de amistad enviadas (cuando cambie)
        loadUsersFromFriendsRequestsSent(context);
        //TODO Cargar Usuarios de mis peticiones de amistad recibidas (cuando cambie)
        loadUsersFromFriendsRequestsReceived(context);
        //TODO Cargar Eventos de mis eventos creados (cuando cambie)
        loadEventsFromMyOwnEvents(context);
        //TODO Cargar Eventos de mis eventos a los que asisto (cuando cambie)
        loadEventsFromEventsParticipation(context);
        //TODO Cargar Eventos de mis invitaciones a eventos (cuando cambie)
        loadEventsFromInvitationsReceived(context);
        //TODO Cargar Eventos de mis peticiones de asistencia a eventos enviadas (cuando cambie)
        loadEventsFromEventsRequests(context);
    }

    private static void loadUsersFromFriendsRequestsSent(final Context context) {
        final String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(myUserID + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.exists()) {
                            loadAProfile(context, dataSnapshot.getKey());

                            ContentValues cvData = Utiles.datasnapshotFriendRequestToContentValues(dataSnapshot, myUserID, true);
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
    private static void loadUsersFromFriendsRequestsReceived(final Context context) {
        final String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(myUserID + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.exists()) {
                            loadAProfile(context, dataSnapshot.getKey());

                            ContentValues cvData = Utiles.datasnapshotFriendRequestToContentValues(dataSnapshot, myUserID, false);
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
                            loadAProfile(context, dataSnapshot.getKey());
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
    private static void loadUsersFromUserRequests(final Context context, final String key) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);

        myUserRef.child(key + "/" + FirebaseDBContract.Event.USER_REQUESTS)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.exists()) {
                            loadAProfile(context, dataSnapshot.getKey());

                            ContentValues cvData = Utiles
                                    .datasnapshotEventsRequestsToContentValues(dataSnapshot, key, false);
                            context.getContentResolver()
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
                });
    }
    private static void loadUsersFromInvitationsSent(final Context context, final String key) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);

        myUserRef.child(key + "/" + FirebaseDBContract.Event.INVITATIONS)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.exists()) {
                            loadAProfile(context, dataSnapshot.getKey());

                            ContentValues cvData = Utiles
                                    .datasnapshotEventInvitationsToContentValues(dataSnapshot, key, false);
                            context.getContentResolver()
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
                });
    }
    private static void loadUsersFromParticipants(final Context context, final String key) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);

        myUserRef.child(key + "/" + FirebaseDBContract.Event.PARTICIPANTS)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.exists()) {
                            loadAProfile(context, dataSnapshot.getKey());

                            ContentValues cvData = Utiles
                                    .datasnapshotEventsParticipationToContentValues(dataSnapshot, key, false);
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

                            //TODO cargar Usuarios que quieren entrar a este evento USER_REQUESTS
                            loadUsersFromUserRequests(context, dataSnapshot.getKey());
                            //TODO cargar Usuarios que no contestaron a invitaciones para este evento INVITATIONS
                            loadUsersFromInvitationsSent(context, dataSnapshot.getKey());
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
    private static void loadEventsFromEventsParticipation(final Context context) {
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
                                    .datasnapshotEventsParticipationToContentValues(dataSnapshot, myUserID, true);
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
    private static void loadEventsFromInvitationsReceived(final Context context) {
        final String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(myUserID + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.exists()) {
                            loadAnEvent(context, dataSnapshot.getKey());

                            ContentValues cvData = Utiles
                                    .datasnapshotEventInvitationsToContentValues(dataSnapshot, myUserID, true);
                            context.getContentResolver()
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
                });
    }
    private static void loadEventsFromEventsRequests(final Context context) {
        final String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(myUserID + "/" + FirebaseDBContract.User.EVENTS_REQUESTS)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            loadAnEvent(context, dataSnapshot.getKey());

                            ContentValues cvData = Utiles
                                    .datasnapshotEventsRequestsToContentValues(dataSnapshot, myUserID, true);
                            context.getContentResolver()
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
                });
    }

    private static void loadAProfile(final Context context, final String userID) {
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
    private static void loadAnEvent(final Context context, String key) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);

        myUserRef.child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Event e = Utiles.datasnapshotToEvent(dataSnapshot);
                            ContentValues cv = Utiles.eventToContentValues(e);
                            context.getContentResolver()
                                    .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                            loadAField(context, e.getmField());
                            //TODO cargar Usuarios que participan en este evento PARTICIPANTS
                            loadUsersFromParticipants(context, dataSnapshot.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    private static void loadAField(final Context context, String key) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_FIELDS);

        myUserRef.child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList<ContentValues> cvArray = new ArrayList<ContentValues>();
                            List<Field> fields = Utiles.datasnapshotToFieldList(dataSnapshot);
                            cvArray.addAll(Utiles.fieldsArrayToContentValues(fields));

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


    private static void loadFieldsFromCity(final Context context, String city) {
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
    private static void loadEventsFromCity(final Context context, String city) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventsRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.CITY;

        eventsRef.orderByChild(filter).equalTo(city)
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Event e = Utiles.datasnapshotToEvent(dataSnapshot);
                    ContentValues cv = Utiles.eventToContentValues(e);
                    context.getContentResolver()
                            .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                    loadAField(context, e.getmField());
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

    public static void loadProfilesWithName(final Context context, String username) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
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
                        User anUser = Utiles.datasnapshotToUser(data, data.getKey());
                        ContentValues cvData = Utiles.dataUserToContentValues(anUser);
                        context.getContentResolver()
                                .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                        List<ContentValues> cvSports = Utiles.sportUserToContentValues(anUser);
                        context.getContentResolver()
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
