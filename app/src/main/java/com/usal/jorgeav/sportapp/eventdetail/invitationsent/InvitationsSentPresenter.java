package com.usal.jorgeav.sportapp.eventdetail.invitationsent;

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
 * Presentador utilizado para mostrar la colección de invitaciones enviadas por el usuario actual.
 * Aquí se inicia la consulta al Proveedor de Contenido para obtener los datos de los usuarios que
 * han recibido las invitaciones y que serán enviados a la Vista {@link InvitationsSentContract.View}.
 * <p>
 * Implementa la interfaz {@link InvitationsSentContract.Presenter} para la comunicación con
 * esta clase y la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los
 * callbacks de la consulta.
 */
class InvitationsSentPresenter implements
        InvitationsSentContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = InvitationsSentPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private InvitationsSentContract.View mEventInvitationsView;

    /**
     * Constructor
     *
     * @param mEventInvitationsView Vista correspondiente a este Presenter
     */
    InvitationsSentPresenter(InvitationsSentContract.View mEventInvitationsView) {
        this.mEventInvitationsView = mEventInvitationsView;
    }

    /**
     * Borra una de las invitaciones de la base de datos de las que han sido enviadas por el
     * usuario actual.
     *
     * @param eventId identificador del evento al que hace referencia la invitación
     * @param uid     identificador del usuario que recibe la invitación
     */
    @Override
    public void deleteInvitationToThisEvent(String eventId, String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            InvitationFirebaseActions.deleteInvitationToThisEvent(myUid, eventId, uid);
    }

    /**
     * Inicia el proceso de carga de la base de datos de los usuarios que recibieron las
     * invitaciones enviadas por el usuario actual
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param bundle        contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadEventInvitationsSent(LoaderManager loaderManager, Bundle bundle) {
        String eventId = bundle.getString(InvitationsSentFragment.BUNDLE_EVENT_ID);
        if (eventId != null && !TextUtils.isEmpty(eventId))
            FirebaseSync.loadUsersFromInvitationsSent(eventId);
        loaderManager.initLoader(SportteamLoader.LOADER_EVENT_INVITATIONS_SENT_ID, bundle, this);
    }

    /**
     * Invocado por  {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderUsersForEventInvitationsSent(Context, String, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_EVENT_INVITATIONS_SENT_ID:
                String myUid = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(myUid)) return null;
                String eventId = args.getString(InvitationsSentFragment.BUNDLE_EVENT_ID);
                return SportteamLoader
                        .cursorLoaderUsersForEventInvitationsSent(mEventInvitationsView.getActivityContext(), eventId, myUid);
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
        mEventInvitationsView.showEventInvitationsSent(data);
    }

    /**
     * Invocado cuando el {@link LoaderManager} exige un reinicio del Loader indicado. Se utiliza
     * este método para borrar los resultados de la consulta anterior.
     *
     * @param loader Loader que va a reiniciarse.
     */
    @Override
    public void onLoaderReset(Loader loader) {
        mEventInvitationsView.showEventInvitationsSent(null);
    }
}
