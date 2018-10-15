package com.usal.jorgeav.sportapp.fields.fielddetail;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.actions.FieldsFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.FieldsFirebaseSync;

/**
 * Presentador utilizado en la vista de detalles de instalaciones. Aquí se inicia la consulta al
 * Proveedor de Contenido para obtener los datos de la instalación, el resultado será enviado a la
 * Vista {@link DetailFieldContract.View}.
 * <p>
 * Desde la Vista se pueden iniciar el proceso de borrado de la instalación que sólo se hará
 * efectivo si no hay partidos próximos en ella. A través de este Presentador también se produce la
 * votación por alguna de las pistas de la instalación.
 * <p>
 * Implementa la interfaz {@link DetailFieldContract.Presenter} para la comunicación con esta clase
 * y la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los callbacks de la
 * consulta.
 */
class DetailFieldPresenter implements
        DetailFieldContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    public static final String TAG = DetailFieldPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private DetailFieldContract.View mView;

    /**
     * Constructor
     *
     * @param view Vista correspondiente a este Presentador
     */
    DetailFieldPresenter(@NonNull DetailFieldContract.View view) {
        this.mView = view;
    }

    /**
     * Invocado desde la Vista cuando el usuario quiere votar por una de las pistas de la instalación
     *
     * @param fieldId identificador de la instalación
     * @param sportId identificador de la pista
     * @param rating  puntuación del voto
     * @return true si los parámetros son correctos, false en otro caso
     */
    @Override
    public boolean voteSportInField(String fieldId, String sportId, float rating) {
        if (fieldId != null && !TextUtils.isEmpty(fieldId)
                && sportId != null && !TextUtils.isEmpty(sportId)
                && rating > 0 && rating <= 5) {
            FieldsFirebaseActions.voteField(fieldId, sportId, rating);
            return true;
        }
        return false;
    }

    /**
     * Invocado desde la Vista cuando el usuario creador de la instalación desea borrarla. Primero
     * comprueba gracias a {@link FieldsFirebaseActions} que no existen partidos en la instalación,
     * y, si no los encuentra, borra la instalación de la base de datos del servidor.
     *
     * @param fieldId identificador de la instalación
     * @see FieldsFirebaseActions#getFieldNextEventsReferenceWithId(String)
     * @see FieldsFirebaseActions#deleteField(String)
     */
    @Override
    public void deleteField(final String fieldId) {
        if (fieldId != null && !TextUtils.isEmpty(fieldId)) {
            FieldsFirebaseActions.getFieldNextEventsReferenceWithId(fieldId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(mView.getActivityContext(),
                                        R.string.toast_msg_error_there_is_next_events,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                FieldsFirebaseActions.deleteField(fieldId);
                                ((BaseFragment) mView).resetBackStack();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }

    /**
     * Inicia el proceso de carga de la instalación que se quiere mostrar de la base de datos.
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void openField(LoaderManager loaderManager, Bundle b) {
        String fieldId = b.getString(DetailFieldFragment.BUNDLE_FIELD_ID);
        if (fieldId != null) FieldsFirebaseSync.loadAField(fieldId);
        loaderManager.initLoader(SportteamLoader.LOADER_FIELD_ID, b, this);
        loaderManager.initLoader(SportteamLoader.LOADER_FIELD_SPORTS_ID, b, this);
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderOneField(Context, String)
     * @see SportteamLoader#cursorLoaderFieldSports(Context, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String fieldId = args.getString(DetailFieldFragment.BUNDLE_FIELD_ID);
        switch (id) {
            case SportteamLoader.LOADER_FIELD_ID:
                return SportteamLoader
                        .cursorLoaderOneField(mView.getActivityContext(), fieldId);
            case SportteamLoader.LOADER_FIELD_SPORTS_ID:
                return SportteamLoader
                        .cursorLoaderFieldSports(mView.getActivityContext(), fieldId);
        }
        return null;
    }

    /**
     * Invocado cuando finaliza la consulta del Loader, envía los resultados obtenidos en forma de
     * {@link Cursor} a la Vista.
     *
     * @param loader Loader utilizado para la consulta
     * @param data   resultado de la consulta
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_FIELD_ID:
                showFieldDetails(data);
                break;
            case SportteamLoader.LOADER_FIELD_SPORTS_ID:
                mView.showSportCourts(data);
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
            case SportteamLoader.LOADER_FIELD_ID:
                showFieldDetails(null);
                break;
            case SportteamLoader.LOADER_FIELD_SPORTS_ID:
                mView.showSportCourts(null);
                break;
        }
    }

    /**
     * Extrae del {@link Cursor} los datos de la instalación para enviarlos a la Vista con el formato
     * adecuado
     *
     * @param data datos obtenidos del Proveedor de Contenido
     */
    private void showFieldDetails(Cursor data) {
        if (data != null && data.moveToFirst()) {
            mView.showFieldName(data.getString(SportteamContract.FieldEntry.COLUMN_NAME));

            String address = data.getString(SportteamContract.FieldEntry.COLUMN_ADDRESS);
            String city = data.getString(SportteamContract.FieldEntry.COLUMN_CITY);
            double latitude = data.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LATITUDE);
            double longitude = data.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LONGITUDE);
            LatLng coordinates = null;
            if (latitude != 0 && longitude != 0) coordinates = new LatLng(latitude, longitude);
            mView.showFieldPlace(address, city, coordinates);

            long open = data.getLong(SportteamContract.FieldEntry.COLUMN_OPENING_TIME);
            long close = data.getLong(SportteamContract.FieldEntry.COLUMN_CLOSING_TIME);
            mView.showFieldTimes(open, close);

            mView.showFieldCreator(data.getString(SportteamContract.FieldEntry.COLUMN_CREATOR));
        } else {
            mView.clearUI();
        }
    }
}
