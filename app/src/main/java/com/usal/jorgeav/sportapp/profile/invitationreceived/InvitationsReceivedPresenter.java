package com.usal.jorgeav.sportapp.profile.invitationreceived;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

/**
 * Presentador utilizado para mostrar la colección de partidos para los que el usuario actual ha
 * recibido una invitación que está pendiente de respuesta. Aquí se inicia la consulta al Proveedor
 * de Contenido para obtener los partidos a los que está invitado este usuario y que serán enviados
 * a la Vista {@link InvitationsReceivedContract.View}.
 * <p>
 * Implementa la interfaz {@link InvitationsReceivedContract.Presenter} para la comunicación con
 * esta clase y la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los
 * callbacks de la consulta.
 */
class InvitationsReceivedPresenter implements
        InvitationsReceivedContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = InvitationsReceivedPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private InvitationsReceivedContract.View mInvitationsReceivedView;

    /**
     * Constructor
     *
     * @param mEventInvitationsView Vista correspondiente a este Presentador
     */
    InvitationsReceivedPresenter(InvitationsReceivedContract.View mEventInvitationsView) {
        this.mInvitationsReceivedView = mEventInvitationsView;
    }

    /**
     * Inicia el proceso de consulta a la base de datos sobre las invitaciones recibidas por el
     * usuario actual.
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadEventInvitations(LoaderManager loaderManager, Bundle b) {
        FirebaseSync.loadEventsFromInvitationsReceived();
        loaderManager.initLoader(SportteamLoader.LOADER_EVENT_INVITATIONS_RECEIVED_ID, b, this);
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderEventsForEventInvitationsReceived(Context, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(currentUserID)) return null;
        switch (id) {
            case SportteamLoader.LOADER_EVENT_INVITATIONS_RECEIVED_ID:
                return SportteamLoader
                        .cursorLoaderEventsForEventInvitationsReceived(mInvitationsReceivedView.getActivityContext(), currentUserID);
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
        mInvitationsReceivedView.showEventInvitations(data);
    }

    /**
     * Invocado cuando el {@link LoaderManager} exige un reinicio del Loader indicado. Se utiliza
     * este método para borrar los resultados de la consulta anterior.
     *
     * @param loader Loader que va a reiniciarse.
     */
    @Override
    public void onLoaderReset(Loader loader) {
        mInvitationsReceivedView.showEventInvitations(null);
    }

}
