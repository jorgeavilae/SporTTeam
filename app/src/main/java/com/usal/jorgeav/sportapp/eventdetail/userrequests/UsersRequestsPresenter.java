package com.usal.jorgeav.sportapp.eventdetail.userrequests;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.actions.EventRequestFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

/**
 * Presentador utilizado para mostrar la colección de usuarios que mandaron peticiones de participación
 * al partido. En una lista muestra los usuarios que tiene la petición pendiente de contestar y en
 * otra lista se muestran los usuarios cuyas peticiones fueron rechazadas.
 * <p>
 * Aquí se inicia la consulta al Proveedor de Contenido para obtener los datos de los usuarios
 * que tienen la petición pendiente de contestación y la lista de los usuarios cuya petición fue
 * rechazada y están bloqueados. Serán enviados a la Vista {@link UsersRequestsContract.View}.
 * También permite aceptar o rechazar las peticiones pendientes y desbloquear a los usuarios cuya
 * petición fue rechazada.
 * <p>
 * Implementa la interfaz {@link UsersRequestsContract.Presenter} para la comunicación con
 * esta clase y la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los
 * callbacks de las consultas.
 */
class UsersRequestsPresenter implements
        UsersRequestsContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = UsersRequestsPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private UsersRequestsContract.View mUsersRequestsView;

    /**
     * Constructor
     *
     * @param mUsersRequestsView Vista correspondiente a este Presentador
     */
    UsersRequestsPresenter(UsersRequestsContract.View mUsersRequestsView) {
        this.mUsersRequestsView = mUsersRequestsView;
    }

    /**
     * Acepta la petición de participación del usuario y lo añade como participante del partido.
     *
     * @param eventId identificador del partido
     * @param uid     identificador del usuario que va a ser aceptado como participante
     */
    @Override
    public void acceptUserRequestToThisEvent(String eventId, String uid) {
        if (!TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            EventRequestFirebaseActions.acceptUserRequestToThisEvent(mUsersRequestsView.getThis(), uid, eventId);
    }

    /**
     * Rechaza la petición de participación del usuario y lo añade como usuario bloqueado al partido.
     *
     * @param eventId identificador del partido
     * @param uid     identificador del usuario que va a ser rechazado como participante
     */
    @Override
    public void declineUserRequestToThisEvent(String eventId, String uid) {
        String myUserID = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUserID) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            EventRequestFirebaseActions.declineUserRequestToThisEvent(uid, eventId, myUserID);
    }

    /**
     * Desbloquea a un usuario bloqueado cuya petición de participación fue rechazada. Si el usuario
     * quiere participar debe enviar otra petición de participación.
     *
     * @param eventId identificador del partido
     * @param uid     identificador del usuario que va a ser aceptado como participante
     */
    @Override
    public void unblockUserParticipationRejectedToThisEvent(String eventId, String uid) {
        if (!TextUtils.isEmpty(eventId))
            EventRequestFirebaseActions.unblockUserParticipationRejectedToThisEvent(uid, eventId);
    }

    /**
     * Inicia el proceso de carga de la base de datos de los usuarios que han enviado una petición
     * de participación al partido
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadUsersRequests(LoaderManager loaderManager, Bundle b) {
        String eventId = b.getString(UsersRequestsFragment.BUNDLE_EVENT_ID);
        if (eventId != null && !TextUtils.isEmpty(eventId))
            FirebaseSync.loadUsersFromUserRequests(eventId);
        loaderManager.initLoader(SportteamLoader.LOADER_USERS_REQUESTS_RECEIVED_ID, b, this);
    }

    /**
     * Inicia el proceso de carga de la base de datos de los usuarios que enviaron una petición de
     * participación y fue rechazada
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadUsersRejected(LoaderManager loaderManager, Bundle b) {
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID, b, this);
    }

    /**
     * Invocado por  {@link LoaderManager} para crear los Loader usados para las consultas
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderUsersForEventRequestsReceived(Context, String)
     * @see SportteamLoader#cursorLoaderEventParticipants(Context, String, boolean)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String eventId = args.getString(UsersRequestsFragment.BUNDLE_EVENT_ID);
        switch (id) {
            case SportteamLoader.LOADER_USERS_REQUESTS_RECEIVED_ID:
                return SportteamLoader
                        .cursorLoaderUsersForEventRequestsReceived(mUsersRequestsView.getActivityContext(), eventId);
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                return SportteamLoader
                        .cursorLoaderEventParticipants(mUsersRequestsView.getActivityContext(), eventId, false);
        }
        return null;
    }

    /**
     * Invocado cuando finaliza cada consulta del Loader, entrega los resultados obtenidos en forma
     * de {@link Cursor} a la Vista.
     *
     * @param loader Loader utilizado para la consulta
     * @param data   resultado de la consulta
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_USERS_REQUESTS_RECEIVED_ID:
                mUsersRequestsView.showUsersRequests(data);
                break;
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                mUsersRequestsView.showRejectedUsers(data);
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
            case SportteamLoader.LOADER_USERS_REQUESTS_RECEIVED_ID:
                mUsersRequestsView.showUsersRequests(null);
                break;
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                mUsersRequestsView.showRejectedUsers(null);
                break;
        }
    }
}
