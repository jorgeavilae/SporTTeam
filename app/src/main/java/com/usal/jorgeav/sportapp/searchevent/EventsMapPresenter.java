package com.usal.jorgeav.sportapp.searchevent;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.mainactivities.SearchEventsActivity;
import com.usal.jorgeav.sportapp.network.firebase.sync.EventsFirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

/**
 * Presentador utilizado para mostrar la colección de partidos a los que el usuario actual puede
 * unirse por no mantener ningún tipo de relación con ellos (no participa, no está invitado, no ha
 * enviado una petición de participación...).
 * <p>
 * Aquí se inicia la consulta al Proveedor de Contenido para obtener esos partidos de la ciudad
 * actual del usuario y que serán enviados a la Vista {@link EventsMapContract.View}.
 * <p>
 * Implementa la interfaz {@link EventsMapContract.Presenter} para la comunicación con esta clase y la
 * interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los callbacks de la
 * consulta.
 */
class EventsMapPresenter implements
        EventsMapContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = EventsMapPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private EventsMapContract.View mEventsMapView;

    /**
     * Constructor
     *
     * @param mEventsMapView Vista correspondiente a este Presentador
     */
    EventsMapPresenter(EventsMapContract.View mEventsMapView) {
        this.mEventsMapView = mEventsMapView;
    }

    /**
     * Inicia el proceso de consulta a la base de datos, de los partidos con los que el usuario
     * actual no tiene relación.
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadNearbyEvents(LoaderManager loaderManager, Bundle b) {
        String city = UtilesPreferences.getCurrentUserCity(mEventsMapView.getActivityContext());
        if (city != null && !TextUtils.isEmpty(city)) EventsFirebaseSync.loadEventsFromCity(city);

        if (b.isEmpty()) {
            loaderManager.destroyLoader(SportteamLoader.LOADER_EVENTS_WITH_PARAMS);
            loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_FROM_CITY, b, this);
        } else {
            loaderManager.destroyLoader(SportteamLoader.LOADER_EVENTS_FROM_CITY);
            loaderManager.restartLoader(SportteamLoader.LOADER_EVENTS_WITH_PARAMS, b, this);
        }
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta. Dependiendo
     * de si la consulta necesita parámetros o no, utiliza un Loader u otro. Además usa una serie de
     * métodos auxiliares para extraer los valores de los parámetros..
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderEventsFromCity(Context, String, String)
     * @see SportteamLoader#cursorLoaderEventsWithParams(Context, String, String, String, Long, Long, int, int, int, int)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = Utiles.getCurrentUserId();
        String city = UtilesPreferences.getCurrentUserCity(mEventsMapView.getActivityContext());
        switch (id) {
            case SportteamLoader.LOADER_EVENTS_FROM_CITY:
                return SportteamLoader
                        .cursorLoaderEventsFromCity(mEventsMapView.getActivityContext(), currentUserID, city);
            case SportteamLoader.LOADER_EVENTS_WITH_PARAMS:
                return SportteamLoader
                        .cursorLoaderEventsWithParams(mEventsMapView.getActivityContext(), currentUserID, city,
                                getBundleSportId(args),
                                getBundleDateFrom(args),
                                getBundleDateTo(args),
                                getBundleTotalFrom(args),
                                getBundleTotalTo(args),
                                getBundleEmptyFrom(args),
                                getBundleEmptyTo(args));
        }
        return null;
    }

    /**
     * Método auxiliar para extraer del {@link Bundle} el parámetro con el deporte
     *
     * @param args {@link Bundle} con los argumentos para la consulta
     * @return identificador del deporte para la consulta, o null.
     */
    private String getBundleSportId(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_SPORT_SELECTED))
                return args.getString(SearchEventsActivity.INSTANCE_SPORT_SELECTED);
        }
        return null;
    }

    /**
     * Método auxiliar para extraer del {@link Bundle} el parámetro con el límite inferior del rango
     * de fechas establecido
     *
     * @param args {@link Bundle} con los argumentos para la consulta
     * @return límite inferior del rango de fechas establecido para la consulta, o null.
     */
    private Long getBundleDateFrom(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_DATE_FROM_SELECTED))
                return args.getLong(SearchEventsActivity.INSTANCE_DATE_FROM_SELECTED);
        }
        return -1L;
    }

    /**
     * Método auxiliar para extraer del {@link Bundle} el parámetro con el límite superior del rango
     * de fechas establecido
     *
     * @param args {@link Bundle} con los argumentos para la consulta
     * @return límite superior del rango de fechas establecido para la consulta, o null.
     */
    private Long getBundleDateTo(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_DATE_TO_SELECTED))
                return args.getLong(SearchEventsActivity.INSTANCE_DATE_TO_SELECTED);
        }
        return -1L;
    }

    /**
     * Método auxiliar para extraer del {@link Bundle} el parámetro con el límite inferior del rango
     * de puestos totales establecido
     *
     * @param args {@link Bundle} con los argumentos para la consulta
     * @return límite inferior del rango de puestos totales establecido para la consulta, o null.
     */
    private int getBundleTotalFrom(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_TOTAL_FROM_SELECTED))
                return args.getInt(SearchEventsActivity.INSTANCE_TOTAL_FROM_SELECTED);
        }
        return -1;
    }

    /**
     * Método auxiliar para extraer del {@link Bundle} el parámetro con el límite superior del rango
     * de puestos totales establecido
     *
     * @param args {@link Bundle} con los argumentos para la consulta
     * @return límite superior del rango de puestos totales establecido para la consulta, o null.
     */
    private int getBundleTotalTo(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_TOTAL_TO_SELECTED))
                return args.getInt(SearchEventsActivity.INSTANCE_TOTAL_TO_SELECTED);
        }
        return -1;
    }

    /**
     * Método auxiliar para extraer del {@link Bundle} el parámetro con el límite inferior del rango
     * de puestos vacantes establecido
     *
     * @param args {@link Bundle} con los argumentos para la consulta
     * @return límite inferior del rango de puestos vacantes establecido para la consulta, o null.
     */
    private int getBundleEmptyFrom(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_EMPTY_FROM_SELECTED))
                return args.getInt(SearchEventsActivity.INSTANCE_EMPTY_FROM_SELECTED);
        }
        return -1;
    }

    /**
     * Método auxiliar para extraer del {@link Bundle} el parámetro con el límite superior del rango
     * de puestos vacantes establecido
     *
     * @param args {@link Bundle} con los argumentos para la consulta
     * @return límite superior del rango de puestos vacantes establecido para la consulta, o null.
     */
    private int getBundleEmptyTo(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_EMPTY_TO_SELECTED))
                return args.getInt(SearchEventsActivity.INSTANCE_EMPTY_TO_SELECTED);
        }
        return -1;
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
        mEventsMapView.showEvents(data);
    }

    /**
     * Invocado cuando el {@link LoaderManager} exige un reinicio del Loader indicado. Se utiliza
     * este método para borrar los resultados de la consulta anterior.
     *
     * @param loader Loader que va a reiniciarse.
     */
    @Override
    public void onLoaderReset(Loader loader) {
        mEventsMapView.showEvents(null);
    }
}
