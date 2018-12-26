package com.usal.jorgeav.sportapp.network.firebase.actions;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.mainactivities.LoginActivity;
import com.usal.jorgeav.sportapp.network.firebase.AppExecutor;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorValueEventListener;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.sync.UsersFirebaseSync;
import com.usal.jorgeav.sportapp.preferences.SettingsFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Los métodos de esta clase contienen la funcionalidad necesaria para actuar sobre los datos de
 * Firebase Realtime Database. Concretamente, sobre los datos relativos a los usuarios.
 *
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
 * FirebaseDatabase</a>
 */
public class UserFirebaseActions {
    /**
     * Nombre de la clase
     */
    private static final String TAG = UserFirebaseActions.class.getSimpleName();

    /**
     * Invocado para insertar el usuario especificado en la base de datos del servidor.
     * Obtiene una referencia a la rama correspondiente del archivo JSON y establece el usuario
     * con {@link User#toMap()}
     *
     * @param user objeto {@link User} listo para ser añadido
     */
    public static void addUser(User user) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(user.getUid()).child(FirebaseDBContract.DATA).setValue(user.toMap());
    }

    /**
     * Invocado para sustituir los deportes practicados por un usuario en la base de datos del servidor.
     * Obtiene una referencia a la rama correspondiente del archivo JSON y establece los deportes
     * indicados en el parámetro
     *
     * @param myUid     identificador del usuario al que se van a sustituir los deportes
     * @param sportsMap mapa de deportes practicados que van a sustituir a los que hubiera
     *                  anteriormente. La clave es el identificador del deporte y el valor el
     *                  nivel de juego
     */
    public static void updateSports(String myUid, HashMap<String, Double> sportsMap) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUid).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.User.SPORTS_PRACTICED).setValue(sportsMap);
    }

    /**
     * Invocado para sustituir el nombre de un usuario en la base de datos del servidor.
     * Obtiene una referencia a la rama correspondiente del archivo JSON y establece el nombre
     * indicado en el parámetro
     *
     * @param myUid identificador del usuario al que se va a sustituir el nombre
     * @param name  nombre que va a sustituir al que hubiera anteriormente.
     */
    public static void updateUserName(String myUid, String name) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUid).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.User.ALIAS).setValue(name);
    }

    /**
     * Invocado para sustituir la edad de un usuario en la base de datos del servidor.
     * Obtiene una referencia a la rama correspondiente del archivo JSON y establece la edad
     * indicado en el parámetro
     *
     * @param myUid identificador del usuario al que se va a sustituir la edad
     * @param age   edad que va a sustituir a la que hubiera anteriormente.
     */
    public static void updateUserAge(String myUid, Integer age) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUid).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.User.AGE).setValue(age);
    }

    /**
     * Invocado para sustituir la foto de perfil de un usuario en la base de datos del servidor.
     * Obtiene una referencia a la rama correspondiente del archivo JSON y establece la URL al
     * archivo de imagen dentro de Firebase Storage que se establecerá como foto de perfil.
     *
     * @param myUid identificador del usuario al que se va a sustituir la foto de perfil
     * @param photo URL de Firebase Storage que referencia a la nueva foto de perfil.
     */
    public static void updateUserPhoto(String myUid, String photo) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUid).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.User.PROFILE_PICTURE).setValue(photo);
    }

    /**
     * Invocado para sustituir el email de un usuario en la base de datos del servidor.
     * Obtiene una referencia a la rama correspondiente del archivo JSON y establece el email
     * indicado en el parámetro
     *
     * @param myUid identificador del usuario al que se va a sustituir el email
     * @param email email que va a sustituir al que hubiera anteriormente.
     */
    public static void updateUserEmail(String myUid, String email) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUid).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.User.EMAIL).setValue(email);
    }

    /**
     * Invocado para sustituir el token identificativo de la aplicación cliente de Firebase de un
     * usuario en la base de datos del servidor. Obtiene una referencia a la rama correspondiente
     * del archivo JSON y establece el token nuevo indicado en el parámetro. Este token sirve para
     * que Firebase Cloud Messaging encuentre el dispositivo en el que el usuario tiene la sesión
     * iniciada y pueda mandarle las notificaciones.
     *
     * @param myUid identificador del usuario al que se va a sustituir el token
     * @param token token que va a sustituir al que hubiera anteriormente.
     */
    public static void updateUserToken(String myUid, String token) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_TOKENS)
                .child(myUid).setValue(token);
    }

    /**
     * Invocado para borrar el token identificativo de la aplicación cliente de Firebase de un
     * usuario en la base de datos del servidor. Obtiene una referencia a la rama correspondiente
     * del archivo JSON y borra el token indicado en el parámetro. Este token sirve para
     * que Firebase Cloud Messaging encuentre el dispositivo en el que el usuario tiene la sesión
     * iniciada y pueda mandarle las notificaciones.
     *
     * @param myUid identificador del usuario al que se va a borrar el token
     */
    public static void deleteUserToken(String myUid) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_TOKENS)
                .child(myUid).removeValue();
    }

    /**
     * Invocado para sustituir la ciudad de un usuario en la base de datos del servidor. Obtiene
     * una referencia a la rama correspondiente del archivo JSON y establece la ciudad y las
     * coordenadas nuevas indicadas en los parámetros. Después de realizar el cambio, se recarga el
     * perfil del usuario {@link UsersFirebaseSync#loadAProfile(LoginActivity, String, boolean)}
     * con la opción de actualizar las preferencias activada.
     *
     * @param myUid  identificador del usuario actual
     * @param city   ciudad nueva
     * @param coords coordenadas de la ciudad
     */
    public static void updateUserCityAndReload(final String myUid, String city, LatLng coords) {
        //Set City
        String cityRef = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid + "/"
                + FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.CITY;

        //Set Latitude
        String latitudeRef = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid + "/"
                + FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.COORD_LATITUDE;

        //Set Longitude
        String longitudeRef = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid + "/"
                + FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.COORD_LONGITUDE;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(cityRef, city);
        childUpdates.put(latitudeRef, coords.latitude);
        childUpdates.put(longitudeRef, coords.longitude);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            // Passing true makes update sharedPreferences and
                            // perform loadEventsFromCity and loadFieldsFromCity
                            UsersFirebaseSync.loadAProfile(null, myUid, true);
                    }
                });
    }

    /**
     * Invocado para borrar los datos generados por el usuario al utilizar la aplicación:
     * participaciones, partidos creados, invitaciones enviadas y recibidas, amigos, etc.
     * Obtiene una referencia a la rama de cada uno de los otros objetos en los que el usuario está
     * presente por alguno de los motivos anteriores y borra su identificador de ellos.
     * Para finalizar borra todas sus notificaciones y alarmas. Esto supone un reinicio de toda
     * actividad del usuario en el sistema.
     * <p>
     * Por último, se avisa a {@link SettingsFragment} de la finalización de este reinicio de datos
     * y se le indica si debe borrar también los propios datos del usuario y al usuario de Firebase
     * Authentication.
     *
     * @param myUserId         identificador del usuario actual
     * @param settingsFragment referencia al Fragmento de preferencias desde el que se invoca este
     *                         método.
     * @param deleteUser       true si debe borrarse el usuario completamente al finalizar, false si
     *                         sólo es un reinicio de sus datos.
     */
    public static void deleteCurrentUser(String myUserId,
                                         final SettingsFragment settingsFragment,
                                         final boolean deleteUser) {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_USERS).child(myUserId);

        userRef.addListenerForSingleValueEvent(
                new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User myUser = dataSnapshot.child(FirebaseDBContract.DATA).getValue(User.class);
                            if (myUser == null) return;
                            myUser.setUid(dataSnapshot.getKey());

                            //Delete User's token
                            FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_TOKENS)
                                    .child(myUser.getUid()).removeValue();

                            //Delete User in Friends
                            for (DataSnapshot friendUid : dataSnapshot.child(FirebaseDBContract.User.FRIENDS).getChildren()) {
                                Log.i(TAG, "deleteCurrentUser: onDataChangeExecutor: deleteFriend " + friendUid.getKey());
                                FriendsFirebaseActions.deleteFriend(myUser.getUid(), friendUid.getKey());
                            }

                            //Delete User in Friends who Received a friendRequest
                            for (DataSnapshot friendRequestSent : dataSnapshot.child(FirebaseDBContract.User.FRIENDS_REQUESTS_SENT).getChildren()) {
                                Log.i(TAG, "deleteCurrentUser: onDataChangeExecutor: cancelFriendRequest " + friendRequestSent.getKey());
                                FriendsFirebaseActions.cancelFriendRequest(myUser.getUid(), friendRequestSent.getKey());
                            }

                            //Delete User in Friends who Send me a friendRequest
                            for (DataSnapshot friendRequestReceived : dataSnapshot.child(FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED).getChildren()) {
                                Log.i(TAG, "deleteCurrentUser: onDataChangeExecutor: declineFriendRequest " + friendRequestReceived.getKey());
                                FriendsFirebaseActions.declineFriendRequest(myUser.getUid(), friendRequestReceived.getKey());
                            }

                            //Delete Events from User events created
                            for (DataSnapshot eventCreated : dataSnapshot.child(FirebaseDBContract.User.EVENTS_CREATED).getChildren()) {
                                Log.i(TAG, "deleteCurrentUser: onDataChangeExecutor: deleteEvent " + eventCreated.getKey());
                                EventsFirebaseActions.deleteEvent(null, eventCreated.getKey());
                            }

                            //Delete participant from User events participation
                            for (DataSnapshot eventParticipation : dataSnapshot.child(FirebaseDBContract.User.EVENTS_PARTICIPATION).getChildren()) {
                                Log.i(TAG, "deleteCurrentUser: onDataChangeExecutor: quitEvent " + eventParticipation.getKey());
                                EventsFirebaseActions.quitEvent(myUser.getUid(), eventParticipation.getKey(), true);
                            }

                            //Delete Invitation Sent from User event invitations received
                            for (DataSnapshot eventInvitationReceived : dataSnapshot.child(FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED).getChildren()) {
                                Log.i(TAG, "deleteCurrentUser: onDataChangeExecutor: declineEventInvitation " + eventInvitationReceived.getKey());
                                String sender = eventInvitationReceived.child(FirebaseDBContract.Invitation.SENDER).getValue(String.class);
                                InvitationFirebaseActions.declineEventInvitation(myUser.getUid(), eventInvitationReceived.getKey(), sender);
                            }

                            //Delete User in Events with a userRequest from me
                            for (DataSnapshot eventUserRequestsSent : dataSnapshot.child(FirebaseDBContract.User.EVENTS_REQUESTS).getChildren()) {
                                Log.i(TAG, "deleteCurrentUser: onDataChangeExecutor: cancelEventRequest " + eventUserRequestsSent.getKey());
                                Event e = UtilesContentProvider.getEventFromContentProvider(eventUserRequestsSent.getKey());
                                if (e != null)
                                    EventRequestFirebaseActions.cancelEventRequest(myUser.getUid(), eventUserRequestsSent.getKey(), e.getOwner());
                            }

                            //Delete all notifications
                            NotificationsFirebaseActions.deleteAllNotifications(myUser.getUid());

                            //Delete all alarms
                            AlarmFirebaseActions.deleteAllAlarms(myUser.getUid());

                            //Delete User
                            if (settingsFragment != null)
                                settingsFragment.userDataDeleted(
                                        myUser.getUid(), myUser.getProfile_picture(), deleteUser);
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Invocado para guardar un archivo de imagen en Firebase Storage.
     * Obtiene una referencia a la carpeta correspondiente del sistema de archivos en la nube y
     * sube el archivo al servidor. Si la subida tiene éxito, el comportamiento que se ejecute
     * será el del Listener proporcionado como parámetro.
     *
     * @param photo    ruta de acceso al archivo de imagen dentro del sistema de archivos del
     *                 dispositivo
     * @param listener Listener que se ejecutará si la subida del archivo tiene éxito
     */
    public static void storePhotoOnFirebase(Uri photo, OnSuccessListener<UploadTask.TaskSnapshot> listener) {
        String lastPathSegment = photo.getLastPathSegment();
        if (lastPathSegment == null) return;
        StorageReference photoRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Utiles.getFirebaseStorageRootReference())
                .child(FirebaseDBContract.Storage.PROFILE_PICTURES)
                .child(lastPathSegment);

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        // Upload file and metadata to the path
        UploadTask uploadTask = photoRef.putFile(photo, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "storePhotoOnFirebase:putFile:onFailure: ", exception);
            }
        }).addOnSuccessListener(listener);
    }

    /**
     * Invocado para borrar un archivo de imagen de Firebase Storage.
     * Obtiene una referencia al archivo correspondiente del sistema de archivos en la nube dada
     * su URL y lo borra del servidor.
     *
     * @param oldPhotoUrl URL de acceso al archivo de imagen dentro del sistema de archivos de
     *                    Firebase Storage
     */
    public static void deleteOldUserPhoto(String oldPhotoUrl) {
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldPhotoUrl);
        photoRef.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "deleteOldUserPhoto:delete:onFailure: ", exception);
            }
        });
    }
}
