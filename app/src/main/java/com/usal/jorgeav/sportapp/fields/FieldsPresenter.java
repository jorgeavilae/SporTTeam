package com.usal.jorgeav.sportapp.fields;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

/**
 * Presentador utilizado para mostrar la colección de instalaciones de la ciudad del usuario actual.
 * Aquí se inicia la consulta al Proveedor de Contenido para obtener las instalaciones de la ciudad
 * que serán enviadas a la Vista {@link FieldsContract.View}.
 * <p>
 * Implementa la interfaz {@link FieldsContract.Presenter} para la comunicación con esta clase y la
 * interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los callbacks de la
 * consulta.
 */
class FieldsPresenter implements
        FieldsContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = FieldsPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private FieldsContract.View mFieldsView;

    /**
     * Constructor
     *
     * @param fieldsView Vista correspondiente a este Presentador
     */
    FieldsPresenter(FieldsContract.View fieldsView) {
        this.mFieldsView = fieldsView;
    }

    /**
     * Inicia el proceso de consulta a la base de datos de las instalaciones de la ciudad del
     * usuario actual
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     * @see UtilesPreferences#getCurrentUserCity(Context)
     */
    @Override
    public void loadNearbyFields(LoaderManager loaderManager, Bundle b) {
        String city = UtilesPreferences.getCurrentUserCity(mFieldsView.getActivityContext());
        if (city != null && !TextUtils.isEmpty(city))
            FirebaseSync.loadFieldsFromCity(city, false);
        loaderManager.initLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY, b, this);
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta.
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderFieldsFromCity(Context, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_FIELDS_FROM_CITY:
                String city = UtilesPreferences.getCurrentUserCity(mFieldsView.getActivityContext());
                return SportteamLoader
                        .cursorLoaderFieldsFromCity(mFieldsView.getActivityContext(), city);
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
        mFieldsView.showFields(data);
    }

    /**
     * Invocado cuando el {@link LoaderManager} exige un reinicio del Loader indicado. Se utiliza
     * este método para borrar los resultados de la consulta anterior.
     *
     * @param loader Loader que va a reiniciarse.
     */
    @Override
    public void onLoaderReset(Loader loader) {
    }
}