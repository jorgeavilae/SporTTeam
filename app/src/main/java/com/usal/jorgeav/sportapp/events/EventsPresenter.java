package com.usal.jorgeav.sportapp.events;

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
 * Presentador utilizado para mostrar la colección de partidos en los que participa el usuario.
 * Aquí se inicia la consulta al Proveedor de Contenido para obtener los partidos creados por este
 * usuario y los partidos en los que fue añadido como participante y que serán enviados a la Vista
 * {@link EventsContract.View}.
 * Implementa la interfaz {@link EventsContract.Presenter} para la comunicación con esta clase y la
 * interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los callbacks de la
 * consulta.
 */
class EventsPresenter implements
        EventsContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = EventsPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private EventsContract.View mEventsView;

    /**
     * Constructor
     *
     * @param eventsView Vista correspondiente a este Presentador
     */
    EventsPresenter(EventsContract.View eventsView) {
        this.mEventsView = eventsView;
    }

    /**
     * Inicia el proceso de carga de la base de datos de los partidos que fueron creados por el
     * usuario actual y los que no fueron creados por él pero en los que participa
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadEvents(LoaderManager loaderManager, Bundle b) {
        FirebaseSync.loadEventsFromMyOwnEvents();
        FirebaseSync.loadEventsFromEventsParticipation();

        loaderManager.initLoader(
                SportteamLoader.LOADER_MY_EVENTS_AND_PARTICIPATION_ID, b, this);
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta.
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderMyEventsAndParticipation(Context, String, boolean)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(currentUserID)) return null;
        switch (id) {
            case SportteamLoader.LOADER_MY_EVENTS_AND_PARTICIPATION_ID:
                return SportteamLoader
                        .cursorLoaderMyEventsAndParticipation(
                                mEventsView.getActivityContext(), currentUserID, true);
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
        switch (loader.getId()) {
            case SportteamLoader.LOADER_MY_EVENTS_AND_PARTICIPATION_ID:
                mEventsView.showCalendarEvents(data);
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
            case SportteamLoader.LOADER_MY_EVENTS_AND_PARTICIPATION_ID:
                mEventsView.showCalendarEvents(null);
                break;
        }
    }
}
