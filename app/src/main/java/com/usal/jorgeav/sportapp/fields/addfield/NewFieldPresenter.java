package com.usal.jorgeav.sportapp.fields.addfield;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.mainactivities.FieldsActivity;
import com.usal.jorgeav.sportapp.network.firebase.actions.FieldsFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.List;

/**
 * Presentador utilizado en la creación o edición de instalaciones.
 * <p>
 * Aquí se validan todos los parámetros de la instalación introducidos en la Vista
 * {@link NewFieldContract.View}. También inicia la consulta al Proveedor de Contenido para obtener
 * esos datos en caso de que se trate de una edición o para consultar las instalaciones de la ciudad
 * en caso de que el usuario quiera editar la dirección y deban mostrarse esas instalaciones sobre
 * un mapa, en ambos casos el resultado será enviado a la Vista {@link NewFieldContract.View}.
 * <p>
 * Implementa la interfaz {@link NewFieldContract.Presenter} para la comunicación con esta clase y
 * la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los callbacks de la
 * consulta.
 */
class NewFieldPresenter implements
        NewFieldContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    public static final String TAG = NewFieldPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private NewFieldContract.View mNewFieldView;

    /**
     * Constructor
     *
     * @param view Vista correspondiente a este Presentador
     */
    NewFieldPresenter(NewFieldContract.View view) {
        this.mNewFieldView = view;
    }

    /**
     * Transforma las horas introducidas a milisegundos, valida los parámetros especificados y,
     * si son correctos, crea o edita la instalación en la base de datos del servidor y finaliza el
     * Fragmento.
     *
     * @param id        identificador de la instalación o null si es una instalación nueva
     * @param name      nombre de la instalación
     * @param address   dirección de la instalación
     * @param coords    coordenadas de la instalación
     * @param city      ciudad de la instalación
     * @param openTime  hora de apertura de la instalación en formato HH:mm
     * @param closeTime hora de cierre de la instalación en formato HH:mm
     * @param sports    lista de pistas en las que se indica el deporte y la puntuación inicial
     */
    @Override
    public void addField(String id, String name, String address, LatLng coords, String city,
                         String openTime, String closeTime, List<SportCourt> sports) {
        // Store in database times in millis (without day) needs a baseDay to parse back to String properly
        Long baseDay = UtilesTime.stringDateToMillis("11/07/92");
        Long openMillis = UtilesTime.stringTimeToMillis(openTime);
        if (openMillis != null) openMillis += baseDay;
        Long closeMillis = UtilesTime.stringTimeToMillis(closeTime);
        if (closeMillis != null) closeMillis += baseDay;

        String creator = Utiles.getCurrentUserId();

        if (isValidAddress(address, city) && isValidName(name) && isValidCoords(coords)
                && isTimesCorrect(openMillis, closeMillis) && isValidCreator(creator)) {

            //If are equals means "all day" so: from 0:00 to 0:00
            if (openMillis != null && closeMillis != null
                    && openMillis.longValue() == closeMillis.longValue()) {
                openMillis = baseDay;
                closeMillis = baseDay;
            }

            Field field = new Field(id, name, address, coords.latitude, coords.longitude, city,
                    openMillis, closeMillis, creator, sports);

            Log.d(TAG, "addField: " + field);
            if (TextUtils.isEmpty(field.getId()))
                FieldsFirebaseActions.addField(field);
            else
                FieldsFirebaseActions.updateField(field);

//            ((FieldsActivity) mNewFieldView.getActivityContext()).mFieldId = null;
            ((FieldsActivity) mNewFieldView.getActivityContext()).mAddress = null;
            ((FieldsActivity) mNewFieldView.getActivityContext()).mCity = null;
            ((FieldsActivity) mNewFieldView.getActivityContext()).mCoord = null;
            mNewFieldView.getThis().resetBackStack();
        }
    }

    /**
     * Comprueba que la dirección es válida
     *
     * @param address dirección postal de la instalación
     * @param city    ciudad de la instalación
     * @return true si es válido, false en caso contrario
     */
    private boolean isValidAddress(String address, String city) {
        if (!TextUtils.isEmpty(address) && !TextUtils.isEmpty(city)
                && address.contains(city)) return true;
        Toast.makeText(mNewFieldView.getActivityContext(), R.string.toast_place_invalid, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "isValidField: not valid");
        return false;
    }

    /**
     * Comprueba que el nombre es válido
     *
     * @param name nombre para la instalación
     * @return true si es válido, false en caso contrario
     */
    private boolean isValidName(String name) {
        if (!TextUtils.isEmpty(name)) return true;
        Toast.makeText(mNewFieldView.getActivityContext(), R.string.error_invalid_name, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "isValidName: not valid");
        return false;
    }

    /**
     * Comprueba que el identificador del usuario creador es válido
     *
     * @param userId identificador del usuario creador
     * @return true si es válido, false en caso contrario
     */
    private boolean isValidCreator(String userId) {
        if (!TextUtils.isEmpty(userId)) return true;
        Toast.makeText(mNewFieldView.getActivityContext(), R.string.toast_invalid_arg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "isValidCreator: not valid");
        return false;
    }

    /**
     * Comprueba que las coordenadas son válidas
     *
     * @param coords coordenadas de la instalación
     * @return true si es válido, false en caso contrario
     */
    private boolean isValidCoords(LatLng coords) {
        if (coords != null && coords.latitude != 0 && coords.longitude != 0)
            return true;
        Toast.makeText(mNewFieldView.getActivityContext(), R.string.toast_place_invalid, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "isValidCoords: not valid");
        return false;
    }

    /**
     * Comprueba que las horas de apertura y cierre son válidas
     *
     * @param open  hora de apertura en milisegundos
     * @param close hora de cierre en milisegundos
     * @return true si es válido, false en caso contrario
     */
    private boolean isTimesCorrect(Long open, Long close) {
        if (open != null && close != null && open <= close) return true;
        Toast.makeText(mNewFieldView.getActivityContext(), R.string.toast_invalid_times, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "isTimesCorrect: incorrect");
        return false;
    }

    /**
     * Inicia el proceso de consulta a la base de datos de la instalación que se va a editar
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void openField(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewFieldFragment.BUNDLE_FIELD_ID)) {
            loaderManager.initLoader(SportteamLoader.LOADER_FIELD_ID, b, this);
            loaderManager.initLoader(SportteamLoader.LOADER_FIELD_SPORTS_ID, b, this);
        }
    }

    /**
     * Detiene el proceso de consulta a la base de datos de la instalación que se va a editar
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     */
    @Override
    public void destroyOpenFieldLoader(LoaderManager loaderManager) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_FIELD_ID);
        loaderManager.destroyLoader(SportteamLoader.LOADER_FIELD_SPORTS_ID);
    }

    /**
     * Inicia el proceso de consulta a la base de datos de las instalaciones de la ciudad del
     * usuario actual
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadNearbyFields(LoaderManager loaderManager, Bundle b) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY);
        String city = UtilesPreferences.getCurrentUserCity(mNewFieldView.getActivityContext());

        if (city != null) {
            FirebaseSync.loadFieldsFromCity(city, false);
            loaderManager.initLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY, b, this);
        }
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderFieldsFromCity(Context, String)
     * @see SportteamLoader#cursorLoaderOneField(Context, String)
     * @see SportteamLoader#cursorLoaderFieldSports(Context, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_FIELDS_FROM_CITY:
                String city = UtilesPreferences.getCurrentUserCity(mNewFieldView.getActivityContext());
                if (city != null)
                    return SportteamLoader
                            .cursorLoaderFieldsFromCity(mNewFieldView.getActivityContext(), city);
            case SportteamLoader.LOADER_FIELD_ID:
                return SportteamLoader
                        .cursorLoaderOneField(mNewFieldView.getActivityContext(),
                                args.getString(NewFieldFragment.BUNDLE_FIELD_ID));
            case SportteamLoader.LOADER_FIELD_SPORTS_ID:
                return SportteamLoader
                        .cursorLoaderFieldSports(mNewFieldView.getActivityContext(),
                                args.getString(NewFieldFragment.BUNDLE_FIELD_ID));
        }
        return null;
    }

    /**
     * Invocado cuando finaliza la consulta del Loader, envía a la Vista los resultados obtenidos,
     * transformando el {@link Cursor} en los objetos o listas que requieren los métodos de la Vista.
     *
     * @param loader Loader utilizado para la consulta
     * @param data   resultado de la consulta
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_FIELDS_FROM_CITY:
                mNewFieldView.retrieveFields(UtilesContentProvider.cursorToMultipleField(data));
                break;
            case SportteamLoader.LOADER_FIELD_ID:
                showFieldDetail(data);
                break;
            case SportteamLoader.LOADER_FIELD_SPORTS_ID:
                mNewFieldView.setSportCourts(UtilesContentProvider.cursorToMultipleSportCourt(data));
                break;
        }
    }

    /**
     * Invocado cuando el {@link LoaderManager} exige un reinicio del Loader indicado. Se utiliza
     * este método para borrar los resultados de las consultas anteriores.
     *
     * @param loader Loader que va a reiniciarse.
     */
    @Override
    public void onLoaderReset(Loader loader) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_FIELDS_FROM_CITY:
                mNewFieldView.retrieveFields(null);
                break;
            case SportteamLoader.LOADER_FIELD_ID:
                showFieldDetail(null);
                break;
            case SportteamLoader.LOADER_FIELD_SPORTS_ID:
                mNewFieldView.setSportCourts(null);
                break;
        }
    }

    /**
     * Extrae del {@link Cursor} los datos de la instalación para enviarlos a la Vista con el
     * formato adecuado
     *
     * @param data datos obtenidos del Proveedor de Contenido
     */
    private void showFieldDetail(Cursor data) {
        if (data != null && data.moveToFirst()) {
            String address = data.getString(SportteamContract.FieldEntry.COLUMN_ADDRESS);
            String city = data.getString(SportteamContract.FieldEntry.COLUMN_CITY);
            double lat = data.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LATITUDE);
            double lng = data.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LONGITUDE);
            LatLng coords = null;
            if (lat != 0 && lng != 0) coords = new LatLng(lat, lng);
            mNewFieldView.showFieldPlace(address, city, coords);

            mNewFieldView.showFieldName(data.getString(SportteamContract.FieldEntry.COLUMN_NAME));

            long openTime = data.getLong(SportteamContract.FieldEntry.COLUMN_OPENING_TIME);
            long closeTime = data.getLong(SportteamContract.FieldEntry.COLUMN_CLOSING_TIME);
            mNewFieldView.showFieldTimes(openTime, closeTime);
        }
    }
}
