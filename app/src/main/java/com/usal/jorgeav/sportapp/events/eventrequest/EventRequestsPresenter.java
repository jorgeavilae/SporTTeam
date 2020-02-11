package com.usal.jorgeav.sportapp.events.eventrequest;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

/**
 * Presentador utilizado para mostrar la colección de peticiones de participación sin respuesta
 * enviadas por el usuario actual.
 * <p>
 * Aquí se inicia la consulta al Proveedor de Contenido para obtener los datos de los partidos a los
 * que van dirigidas las peticiones y que serán enviados a la Vista {@link EventRequestsContract.View}.
 * <p>
 * Implementa la interfaz {@link EventRequestsContract.Presenter} para la comunicación con
 * esta clase y la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los
 * callbacks de la consulta.
 */
class EventRequestsPresenter implements
        EventRequestsContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = EventRequestsPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private EventRequestsContract.View mEventRequestsView;

    /**
     * Constructor
     *
     * @param mEventRequestsView Vista correspondiente a este Presentador
     */
    EventRequestsPresenter(EventRequestsContract.View mEventRequestsView) {
        this.mEventRequestsView = mEventRequestsView;
    }

    /**
     * Inicia el proceso de consulta a la base de datos de los partidos a los que el usuario actual
     * envió una petición de participación que todavía no tiene respuesta
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadEventRequests(LoaderManager loaderManager, Bundle b) {
        FirebaseSync.loadEventsFromEventsRequests();
        loaderManager.initLoader(SportteamLoader.LOADER_EVENT_REQUESTS_SENT_ID, b, this);

    }

    /**
     * Invocado por  {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderEventsForEventRequestsSent(Context, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(currentUserID)) return null;
        switch (id) {
            case SportteamLoader.LOADER_EVENT_REQUESTS_SENT_ID:
                return SportteamLoader
                        .cursorLoaderEventsForEventRequestsSent(mEventRequestsView.getActivityContext(), currentUserID);
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
        mEventRequestsView.showEventRequests(data);
    }

    /**
     * Invocado cuando el {@link LoaderManager} exige un reinicio del Loader indicado. Se utiliza
     * este método para borrar los resultados de la consulta anterior.
     *
     * @param loader Loader que va a reiniciarse.
     */
    @Override
    public void onLoaderReset(Loader loader) {
        mEventRequestsView.showEventRequests(null);
    }

}
