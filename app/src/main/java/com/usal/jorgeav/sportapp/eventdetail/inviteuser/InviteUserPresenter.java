package com.usal.jorgeav.sportapp.eventdetail.inviteuser;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.actions.InvitationFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

/**
 * Presentador utilizado para mostrar la colección de usuarios a los que enviar una invitación.
 * Aquí se inicia la consulta al Proveedor de Contenido para obtener los datos de los usuarios que
 * pueden recibir una invitación y que serán enviados a la Vista {@link InviteUserContract.View}.
 * Esta colección de usuarios la forman los amigos del usuario actual menos los que ya tienen algún
 * tipo de relación con el partido.
 *
 * <p>Implementa la interfaz {@link InviteUserContract.Presenter} para la comunicación con
 * esta clase y la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los
 * callbacks de la consulta.
 */
class InviteUserPresenter implements
        InviteUserContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = InviteUserPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private InviteUserContract.View mSendInvitationView;

    /**
     * Constructor
     *
     * @param mSendInvitationView Vista correspondiente a este Presenter
     */
    InviteUserPresenter(InviteUserContract.View mSendInvitationView) {
        this.mSendInvitationView = mSendInvitationView;
    }


    /**
     * Envía una invitación para el evento <var>eventId</var> al usuario <var>uid</var> de parte
     * del usuario actual.
     *
     * @param eventId identificador del evento al que hace referencia la invitación
     * @param uid identificador del usuario que recibe la invitación
     */
    @Override
    public void sendInvitationToThisEvent(String eventId, String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            InvitationFirebaseActions.sendInvitationToThisEvent(myUid, eventId, uid);
    }

    /**
     * Inicia el proceso de carga de la base de datos de los usuarios que pueden recibir una
     * invitación a este partido
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param bundle contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadFriends(LoaderManager loaderManager, Bundle bundle) {
        FirebaseSync.loadUsersFromFriends();
        loaderManager.initLoader(SportteamLoader.LOADER_USERS_FOR_INVITE_ID, bundle, this);
    }

    /**
     * Invocado por  {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     *
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderUsersForInvite(Context, String, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_USERS_FOR_INVITE_ID:
                String eventID = args.getString(InviteUserFragment.BUNDLE_EVENT_ID);
                String currentUserID = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(currentUserID)) return null;
                return SportteamLoader
                        .cursorLoaderUsersForInvite(mSendInvitationView.getActivityContext(), currentUserID, eventID);
        }
        return null;
    }

    /**
     * Invocado cuando finaliza la consulta del Loader, entrega los resultados obtenidos en
     * forma de {@link Cursor} a la Vista.
     *
     * @param loader Loader utilizado para la consulta
     * @param data resultado de la consulta
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSendInvitationView.showFriends(data);
    }

    /**
     * Invocado cuando el {@link LoaderManager} exige un reinicio del Loader indicado. Se utiliza
     * este método para borrar los resultados de la consulta anterior.
     *
     * @param loader Loader que va a reiniciarse.
     */
    @Override
    public void onLoaderReset(Loader loader) {
        mSendInvitationView.showFriends(null);
    }
}
