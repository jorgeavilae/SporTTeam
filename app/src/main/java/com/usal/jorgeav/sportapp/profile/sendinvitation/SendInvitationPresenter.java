package com.usal.jorgeav.sportapp.profile.sendinvitation;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.actions.InvitationFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

/**
 * Presentador utilizado para mostrar la colección de partidos para los que el usuario actual puede
 * enviar una invitación al usuario mostrado. Aquí se inicia la consulta al Proveedor de Contenido
 * para obtener los partidos para los que el usuario actual puede enviar una invitación y que serán
 * enviados a la Vista {@link SendInvitationContract.View}. Esta colección de partidos la forman
 * los partidos en los que el usuario actual participa menos los que ya tienen algún tipo de
 * relación con el usuario al que van a ir destinadas las invitaciones.
 * <p>
 * Implementa la interfaz {@link SendInvitationContract.Presenter} para la comunicación con
 * esta clase y la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los
 * callbacks de la consulta.
 */
class SendInvitationPresenter implements
        SendInvitationContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = SendInvitationPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private SendInvitationContract.View mSendInvitationView;

    /**
     * Constructor
     *
     * @param mSendInvitationView Vista correspondiente a este Presenter
     */
    SendInvitationPresenter(SendInvitationContract.View mSendInvitationView) {
        this.mSendInvitationView = mSendInvitationView;
    }

    /**
     * Envía una invitación al usuario mostrado para el partido seleccionado.
     *
     * @param eventId identificador del partido seleccionado
     * @param uid     identificador del usuario mostrado que recibirá la invitación
     * @see InvitationFirebaseActions#sendInvitationToThisEvent(String, String, String)
     */
    @Override
    public void sendInvitationToThisUser(String eventId, String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            InvitationFirebaseActions.sendInvitationToThisEvent(myUid, eventId, uid);
    }

    /**
     * Inicia el proceso de consulta a la base de datos sobre los partidos para los que el usuario
     * actual puede enviar una invitación al usuario mostrado.
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadEventsForInvitation(LoaderManager loaderManager, Bundle b) {
        FirebaseSync.loadEventsFromMyOwnEvents();
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_FOR_INVITATION_ID, b, this);
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderEventsForInvitation(Context, String, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_EVENTS_FOR_INVITATION_ID:
                String currentUserID = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(currentUserID)) return null;
                String otherUserID = args.getString(SendInvitationFragment.BUNDLE_INSTANCE_UID);
                return SportteamLoader
                        .cursorLoaderEventsForInvitation(
                                mSendInvitationView.getActivityContext(), currentUserID, otherUserID);
        }
        return null;
    }

    /**
     * Invocado cuando finaliza la consulta del Loader, entrega los resultados obtenidos en
     * forma de {@link Cursor} a la Vista.
     *
     * @param loader Loader utilizado para la consulta
     * @param data   resultado de la consulta
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSendInvitationView.showEventsForInvitation(data);
    }

    /**
     * Invocado cuando el {@link LoaderManager} exige un reinicio del Loader indicado. Se utiliza
     * este método para borrar los resultados de la consulta anterior.
     *
     * @param loader Loader que va a reiniciarse.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSendInvitationView.showEventsForInvitation(null);
    }
}
