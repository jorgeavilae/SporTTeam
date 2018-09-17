package com.usal.jorgeav.sportapp.alarms;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;

/**
 * Presentador utilizado para mostrar la colección de alarmas guardadas. Aquí se inicia la consulta
 * al Proveedor de Contenido para obtener las alarmas creadas por este usuario que serán enviadas a
 * la Vista {@link AlarmsContract.View}.
 * Implementa la interfaz {@link AlarmsContract.Presenter} para la comunicación con esta clase y la
 * interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los callbacks de la
 * consulta.
 */
class AlarmsPresenter implements
        AlarmsContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = AlarmsPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private AlarmsContract.View mAlarmsView;

    /**
     * Constructor
     *
     * @param mAlarmsView referencia a la Vista correspondiente a este Presentador
     */
    AlarmsPresenter(AlarmsContract.View mAlarmsView) {
        this.mAlarmsView = mAlarmsView;
    }

    /**
     * Inicia el proceso de carga de las alarmas de la base de datos
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadAlarms(LoaderManager loaderManager, Bundle b) {
        FirebaseSync.loadAlarmsFromMyAlarms();
        loaderManager.initLoader(SportteamLoader.LOADER_MY_ALARMS_ID, b, this);
    }

    /**
     * Invocado por  {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     *
     * @return Loader que realiza la consulta.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_MY_ALARMS_ID:
                return SportteamLoader
                        .cursorLoaderMyAlarms(mAlarmsView.getActivityContext());
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
        mAlarmsView.showAlarms(data);
    }

    /**
     * Invocado cuando el {@link LoaderManager} exige un reinicio del Loader indicado. Se utiliza
     * este método para borrar los resultados de la consulta anterior.
     *
     * @param loader Loader que va a reiniciarse.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAlarmsView.showAlarms(null);
    }
}
