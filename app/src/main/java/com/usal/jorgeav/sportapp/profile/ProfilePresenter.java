package com.usal.jorgeav.sportapp.profile;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.mainactivities.ActivityContracts;
import com.usal.jorgeav.sportapp.network.firebase.actions.FriendsFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.actions.UserFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.sync.UsersFirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

/**
 * Presentador utilizado para mostrar los detalles del usuario actual o cualquier otro. Aquí se
 * inicia la consulta al Proveedor de Contenido para obtener los datos del usuario a mostrar y el
 * resultado será enviado a la Vista {@link ProfileContract.View}.
 * <p>
 * También se encarga de determinar la relación entre el usuario actual y el usuario mostrado.
 * Para ello utiliza una {@link AsyncTask} que permite consultar el Proveedor de Contenido fuera del
 * hilo principal. Además, se mantiene a la escucha de cambios en esa relación mediante un patrón
 * Observer que notifica a la Vista.
 * <p>
 * Por último, desde la Vista se pueden iniciar los procesos de envío y cancelación de peticiones de
 * amistad, aceptar o rechazar esa petición o borrar la amistad entre ambos usuarios. Si el usuario
 * mostrado resulta ser el usuario actual, a través de esta clase, se pueden modificar su nombre,
 * edad o foto de perfil.
 * <p>
 * Implementa la interfaz {@link ProfileContract.Presenter} para la comunicación con esta clase
 * y la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los callbacks de la
 * consulta.
 */
class ProfilePresenter implements
        ProfileContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = ProfilePresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private ProfileContract.View mUserView;

    /**
     * Permite que el Presentador mantenga un callback sobre una URI de la base de datos que se
     * ejecuta cada vez esa URI es notificada a causa de un cambio en los datos a los que apunta.
     * Se basa en el patrón Observer.
     */
    private ContentObserver mContentObserver;

    /**
     * Constructor con argumentos. Aquí se inicializa el {@link ContentObserver} estableciendo su
     * comportamiento: determinar el nuevo tipo de relación con
     * {@link #getRelationTypeBetweenThisUserAndI()}
     *
     * @param userView Vista correspondiente a este Presentador
     */
    ProfilePresenter(ProfileContract.View userView) {
        mUserView = userView;
        mContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                getRelationTypeBetweenThisUserAndI();
            }
        };
    }

    /**
     * Inicia el proceso de consulta a la base de datos sobre los datos del usuario.
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void openUser(LoaderManager loaderManager, Bundle b) {
        String userId = b.getString(ProfileFragment.BUNDLE_INSTANCE_UID);
        if (userId != null) UsersFirebaseSync.loadAProfile(null, userId, false);
        FirebaseSync.loadUsersFromFriendsRequestsSent();
        loaderManager.initLoader(SportteamLoader.LOADER_PROFILE_ID, b, this);
        loaderManager.initLoader(SportteamLoader.LOADER_PROFILE_SPORTS_ID, b, this);
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderOneUser(Context, String)
     * @see SportteamLoader#cursorLoaderSportsUser(Context, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String userId = args.getString(ProfileFragment.BUNDLE_INSTANCE_UID);
        switch (id) {
            case SportteamLoader.LOADER_PROFILE_ID:
                return SportteamLoader
                        .cursorLoaderOneUser(mUserView.getActivityContext(), userId);
            case SportteamLoader.LOADER_PROFILE_SPORTS_ID:
                return SportteamLoader
                        .cursorLoaderSportsUser(mUserView.getActivityContext(), userId);
        }
        return null;
    }

    /**
     * Invocado cuando finaliza la consulta del Loader, actúa sobre los resultados obtenidos en
     * forma de {@link Cursor}. Extrayendo los parámetros del usuario o enviando el Cursor al
     * Adaptador de la Vista.
     *
     * @param loader Loader utilizado para la consulta
     * @param data   resultado de la consulta
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_PROFILE_ID:
                showUser(data);
                break;
            case SportteamLoader.LOADER_PROFILE_SPORTS_ID:
                mUserView.showSports(data);
                break;
        }
    }

    /**
     * Invocado cuando el {@link LoaderManager} exige un reinicio del Loader indicado. Se utiliza
     * este método para borrar los resultados de la consulta anterior.
     *
     * @param loader Loader que va a reiniciarse.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_PROFILE_ID:
                showUser(null);
                break;
            case SportteamLoader.LOADER_PROFILE_SPORTS_ID:
                mUserView.showSports(null);
                break;
        }
    }

    /**
     * Extrae del {@link Cursor} los datos del usuario para enviarlos a la Vista con el formato
     * adecuado
     *
     * @param data datos obtenidos del Proveedor de Contenido
     */
    private void showUser(Cursor data) {
        if (data != null && data.moveToFirst()) {
            mUserView.showUserImage(data.getString(SportteamContract.UserEntry.COLUMN_PHOTO));
            mUserView.showUserName(data.getString(SportteamContract.UserEntry.COLUMN_NAME));
            mUserView.showUserCity(data.getString(SportteamContract.UserEntry.COLUMN_CITY));
            mUserView.showUserAge(data.getInt(SportteamContract.UserEntry.COLUMN_AGE));
            mUserView.showContent();
        } else {
            mUserView.clearUI();
        }
    }

    /**
     * Modificador que, aplicado a una variable, le permite adquirir como valor solamente el
     * siguiente conjunto de constantes que representan los distintos tipos de relación posible
     * entre un usuario cualquiera y el usuario actual.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RELATION_TYPE_ERROR, RELATION_TYPE_ME, RELATION_TYPE_NONE, RELATION_TYPE_FRIENDS,
            RELATION_TYPE_I_SEND_REQUEST, RELATION_TYPE_I_RECEIVE_REQUEST})
    @interface UserRelationType {
    }

    /**
     * Error
     */
    static final int RELATION_TYPE_ERROR = -1;
    /**
     * Usuario actual es el usuario mostrado
     */
    static final int RELATION_TYPE_ME = 0;
    /**
     * Ninguna relación entre usuario actual y usuario mostrado
     */
    static final int RELATION_TYPE_NONE = 1;
    /**
     * Usuario actual es amigo del usuario mostrado
     */
    static final int RELATION_TYPE_FRIENDS = 2;
    /**
     * Usuario actual mandó una petición de amistad al usuario mostrado
     */
    static final int RELATION_TYPE_I_SEND_REQUEST = 3;
    /**
     * Usuario actual ha recibido una petición de amistad del usuario mostrado
     */
    static final int RELATION_TYPE_I_RECEIVE_REQUEST = 4;

    /**
     * Crea y ejecuta una {@link AsyncTask} para realizar una serie de consultas al Proveedor de
     * Contenido que determinen la relación entre el usuario actual y el usuario mostrado. Es
     * necesario que las consultas a la base de datos se realicen en segundo plano ya que pueden
     * tardar demasiado como para realizarlas sobre el hilo de ejecución principal, y ralentizarían
     * la interfaz.
     */
    @Override
    public void getRelationTypeBetweenThisUserAndI() {
        new MyAsyncTask(mUserView).execute();
    }

    /**
     * Clase interna derivada de {@link AsyncTask} para realizar acciones fuera del hilo principal
     * de ejecución. Concretamente, esta clase se encarga de consultar el Proveedor de Contenido
     * buscando la relación entre el identificador del usuario actual y el identificador de un
     * usuario cualquiera dado. Además, mantiene una referencia a la Vista correspondiente al
     * Presentador para poder comunicarle los resultado de la consulta.
     * <p>
     * Crear esta clase interna con una referencia débil {@link WeakReference} a la Vista
     * evita fugas de memoria, evitando una conexión fuerte entre la Vista, con un ciclo de vida
     * propio, y este objeto, que se ejecuta fuera del hilo de la interfaz, permite al GC borrar
     * la Vista.
     */
    private static class MyAsyncTask extends AsyncTask<Void, Void, Integer> {

        /**
         * Referencia a la Vista para comunicar resultados
         *
         * @see <a href="https://developer.android.com/reference/java/lang/ref/WeakReference">
         * WeakReference</a>
         */
        private WeakReference<ProfileContract.View> mView;

        /**
         * Constructor
         *
         * @param view Vista correspondiente al Presentador
         */
        MyAsyncTask(ProfileContract.View view) {
            mView = new WeakReference<>(view);
        }

        /**
         * Método de {@link AsyncTask} que se ejecuta fuera del hilo principal. Aquí se realizan
         * secuencialmente las consultas a diferentes tablas del Proveedor de Contenido hasta
         * determinar la relación entre el usuario mostrado y el usuario actual.
         *
         * @param voids no se requiere ningún parámetro
         * @return tipo de relación según las constantes declaradas en {@link UserRelationType}
         */
        @Override
        protected Integer doInBackground(Void... voids) {
            // Check if ProfileView still exists
            ProfileContract.View usersView = mView.get();
            if (usersView == null) return RELATION_TYPE_ERROR;

            try {
                String myUid = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(myUid)) return RELATION_TYPE_ERROR;

                //Me?
                if (myUid.equals(usersView.getUserID())) return RELATION_TYPE_ME;

                //Friends?
                Cursor cursorFriends = usersView.getActivityContext().getContentResolver().query(
                        SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI,
                        new String[]{SportteamContract.FriendsEntry.USER_ID_TABLE_PREFIX},
                        SportteamContract.FriendsEntry.MY_USER_ID + " = ? AND "
                                + SportteamContract.FriendsEntry.USER_ID + " = ?",
                        new String[]{myUid, usersView.getUserID()},
                        null);
                if (cursorFriends != null) {
                    if (cursorFriends.getCount() > 0) {
                        cursorFriends.close();
                        return RELATION_TYPE_FRIENDS;
                    }
                    cursorFriends.close();
                }

                //I have received a FriendRequest?
                Cursor cursorReceiver = usersView.getActivityContext().getContentResolver().query(
                        SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                        new String[]{SportteamContract.FriendRequestEntry.SENDER_ID_TABLE_PREFIX},
                        SportteamContract.FriendRequestEntry.SENDER_ID + " = ? AND "
                                + SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ?",
                        new String[]{usersView.getUserID(), myUid},
                        null);
                if (cursorReceiver != null) {
                    if (cursorReceiver.getCount() > 0) {
                        cursorReceiver.close();
                        return RELATION_TYPE_I_RECEIVE_REQUEST;
                    }
                    cursorReceiver.close();
                }

                //I have sent a FriendRequest?
                Cursor cursorSender = usersView.getActivityContext().getContentResolver().query(
                        SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                        new String[]{SportteamContract.FriendRequestEntry.RECEIVER_ID_TABLE_PREFIX},
                        SportteamContract.FriendRequestEntry.SENDER_ID + " = ? AND "
                                + SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ?",
                        new String[]{myUid, usersView.getUserID()},
                        null);
                if (cursorSender != null) {
                    if (cursorSender.getCount() > 0) {
                        cursorSender.close();
                        return RELATION_TYPE_I_SEND_REQUEST;
                    }
                    cursorSender.close();
                }

                //No relation
                return RELATION_TYPE_NONE;
            } catch (NullPointerException e) {
                e.printStackTrace();
                return RELATION_TYPE_ERROR;
            }
        }

        /**
         * Envía el resultado obtenido en {@link #doInBackground(Void...)} a la Vista. Este método
         * ya no se ejecuta en segundo plano.
         *
         * @param integer tipo de relación según las constantes declaradas en
         *                {@link UserRelationType}
         */
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mView.get().uiSetupForUserRelation(integer);
        }
    }

    /**
     * Invocado desde la Vista cuando el usuario quiere enviar una petición de amistad al usuario
     * mostrado
     *
     * @param uid identificador del usuario mostrado
     */
    @Override
    public void sendFriendRequest(String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(uid)) {
            FriendsFirebaseActions.sendFriendRequest(myUid, uid);
        }
    }

    /**
     * Invocado desde la Vista cuando el usuario quiere cancelar una petición de amistad enviada al
     * usuario mostrado
     *
     * @param uid identificador del usuario mostrado
     */
    @Override
    public void cancelFriendRequest(String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(uid)) {
            FriendsFirebaseActions.cancelFriendRequest(myUid, uid);
        }

    }

    /**
     * Invocado desde la Vista cuando el usuario quiere aceptar una petición de amistad recibida del
     * usuario mostrado
     *
     * @param uid identificador del usuario mostrado
     */
    @Override
    public void acceptFriendRequest(String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(uid)) {
            FriendsFirebaseActions.acceptFriendRequest(myUid, uid);
        }

    }

    /**
     * Invocado desde la Vista cuando el usuario quiere rechazar una petición de amistad recibida del
     * usuario mostrado
     *
     * @param uid identificador del usuario mostrado
     */
    @Override
    public void declineFriendRequest(String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(uid)) {
            FriendsFirebaseActions.declineFriendRequest(myUid, uid);
        }

    }

    /**
     * Invocado desde la Vista cuando el usuario quiere borrar la amistad con el usuario mostrado
     *
     * @param uid identificador del usuario mostrado
     */
    @Override
    public void deleteFriend(String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(uid)) {
            FriendsFirebaseActions.deleteFriend(myUid, uid);
        }
    }

    /**
     * Busca y comprueba la existencia, en la base de datos del servidor, de algún usuario con el
     * nombre proporcionado antes de cambiar el nombre del usuario actual (los nombres deben ser
     * únicos en el sistema).
     *
     * @param newName  nombre del que se comprueba la existencia
     * @param listener Listener con el que se especifica la acción a realizar una vez se
     *                 realice la consulta.
     * @see UsersFirebaseSync#queryUserName(String, ValueEventListener)
     */
    @Override
    public void checkUserName(String newName, ValueEventListener listener) {
        if (newName != null && !TextUtils.isEmpty(newName))
            UsersFirebaseSync.queryUserName(newName, listener);
    }

    /**
     * Establece el nombre proporcionado como nuevo nombre del usuario actual tanto en el objeto
     * FirebaseUser, como en la base de datos del servidor.
     *
     * @param name nuevo nombre del usuario actual
     * @see UserFirebaseActions#updateUserName(String, String)
     * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser">
     * FirebaseUser</a>
     */
    @Override
    public void updateUserName(String name) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser == null) return;

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        fUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (mUserView.getActivityContext() instanceof ActivityContracts.NavigationDrawerManagement)
                    ((ActivityContracts.NavigationDrawerManagement) mUserView.getActivityContext()).setUserInfoInNavigationDrawer();
            }
        });

        UserFirebaseActions.updateUserName(fUser.getUid(), name);
        UsersFirebaseSync.loadAProfile(null, fUser.getUid(), false);
    }

    /**
     * Establece la edad proporcionada como la nueva edad del usuario actual
     *
     * @param age nueva edad del usuario actual
     */
    @Override
    public void updateUserAge(int age) {
        String myUserId = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserId)) return;

        UserFirebaseActions.updateUserAge(myUserId, age);
        UsersFirebaseSync.loadAProfile(null, myUserId, false);
    }

    /**
     * Establece la imagen de perfil proporcionada como nueva imagen de perfil del usuario actual
     * tanto en el objeto FirebaseUser, como en la base de datos del servidor. También borra la
     * foto antigua del servidor Firebase Storage.
     *
     * @param photoCroppedUri ruta dentro del sistema de archivos del dispositivo a la nueva
     *                        imagen de perfil del usuario actual
     * @see UserFirebaseActions#updateUserPhoto(String, String)
     * @see UserFirebaseActions#deleteOldUserPhoto(String)
     * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser">
     * FirebaseUser</a>
     * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/storage/FirebaseStorage">
     * FirebaseStorage</a>
     */
    @Override
    public void updateUserPhoto(Uri photoCroppedUri) {
        UserFirebaseActions.storePhotoOnFirebase(photoCroppedUri, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                if (downloadUrl != null) {
                    String oldPhotoUrl = Utiles.getCurrentUserPhoto();

                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (fUser == null) return;

                    // Update photo URL in Firebase Auth profile
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUrl)
                            .build();
                    fUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Update NavigationDrawer header
                            if (mUserView.getActivityContext() instanceof ActivityContracts.NavigationDrawerManagement)
                                ((ActivityContracts.NavigationDrawerManagement) mUserView.getActivityContext()).setUserInfoInNavigationDrawer();
                        }
                    });

                    // Update photo URL in Firebase Database
                    UserFirebaseActions.updateUserPhoto(fUser.getUid(), downloadUrl.toString());
                    UsersFirebaseSync.loadAProfile(null, fUser.getUid(), false);

                    // Delete old photo in Firebase Storage
                    UserFirebaseActions.deleteOldUserPhoto(oldPhotoUrl);
                }
            }
        });
    }

    /**
     * Registra y activa el Observer creado en el Constructor de esta clase sobre la URI
     * correspondiente a la relación entre usuario mostrado y usuario actual
     */
    @Override
    public void registerUserRelationObserver() {
        mUserView.getActivityContext().getContentResolver().registerContentObserver(
                SportteamContract.UserEntry.CONTENT_USER_RELATION_USER_URI, false, mContentObserver);
    }

    /**
     * Desactiva el Observer creado en el Constructor de esta clase y que estaba puesto sobre la URI
     * correspondiente a la relación entre usuario mostrado y usuario actual
     */
    @Override
    public void unregisterUserRelationObserver() {
        mUserView.getActivityContext().getContentResolver().unregisterContentObserver(mContentObserver);
    }
}
