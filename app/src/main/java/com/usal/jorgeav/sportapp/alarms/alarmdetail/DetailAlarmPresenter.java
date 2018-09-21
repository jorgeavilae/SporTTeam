package com.usal.jorgeav.sportapp.alarms.alarmdetail;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.widget.Toast;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.alarms.addalarm.NewAlarmFragment;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.actions.AlarmFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.AlarmsFirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

/**
 * Presentador utilizado en la vista de detalles de alarmas. Aquí se inicia la consulta al
 * Proveedor de Contenido para obtener los datos de la alarma o para consultar los partidos que
 * coinciden con los parámetros de la alarma, en ambos casos el resultado será enviado a la
 * Vista {@link DetailAlarmContract.View}. También se encarga de iniciar el borrado de una alarma
 * de los servidores de la aplicación.
 * Implementa la interfaz {@link DetailAlarmContract.Presenter} para la comunicación con esta clase
 * y la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los callbacks de la
 * consulta.
 */
class DetailAlarmPresenter implements
        DetailAlarmContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = DetailAlarmPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private DetailAlarmContract.View mView;

    /**
     * Constructor
     *
     * @param view referencia a la Vista correspondiente a este Presentador
     */
    DetailAlarmPresenter(@NonNull DetailAlarmContract.View view) {
        this.mView = view;
    }

    /**
     * Inicia el proceso de carga de la alarma que se quiere mostrar de la base de datos. También
     * inicia la carga de los partidos que coinciden con los parámetros de la alarma.
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void openAlarm(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewAlarmFragment.BUNDLE_ALARM_ID)) {
            String alarmId = b.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
            if (alarmId != null) {
                AlarmsFirebaseSync.loadAnAlarm(alarmId);
                loaderManager.initLoader(SportteamLoader.LOADER_ALARM_ID, b, this);
                loaderManager.initLoader(SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID, b, this);
            }
        }
    }

    /**
     * Inicia el proceso de borrado de la alarma de la base de datos
     *
     * @param b contenedor de posibles parámetros utilizados en el borrado
     */
    @Override
    public void deleteAlarm(Bundle b) {
        String alarmId = b.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
        String userId = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(mView.getActivityContext(), R.string.action_not_done, Toast.LENGTH_SHORT).show();
            return;
        }
        AlarmFirebaseActions.deleteAlarm(userId, alarmId);
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     *
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderOneAlarm(Context, String)
     * @see SportteamLoader#cursorLoaderAlarmCoincidence(Context, String, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String alarmId = args.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
        String myUserId = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserId)) return null;
        switch (id) {
            case SportteamLoader.LOADER_ALARM_ID:
                return SportteamLoader
                        .cursorLoaderOneAlarm(mView.getActivityContext(), alarmId);
            case SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID:
                return SportteamLoader
                        .cursorLoaderAlarmCoincidence(mView.getActivityContext(), alarmId, myUserId);
        }
        return null;
    }

    /**
     * Invocado cuando finaliza la consulta del Loader, actúa sobre los resultados obtenidos en
     * forma de {@link Cursor} o envía los partidos encontrados a la Vista para que los muestre.
     *
     * @param loader Loader utilizado para la consulta
     * @param data resultado de la consulta
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_ALARM_ID:
                showAlarmDetails(data);
                break;
            case SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID:
                mView.showEvents(data);
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
            case SportteamLoader.LOADER_ALARM_ID:
                showAlarmDetails(null);
                break;
            case SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID:
                mView.showEvents(null);
                break;
        }
    }

    /**
     * Extrae del {@link Cursor} los datos de la alarma para enviarlos a la Vista con el formato
     * adecuado
     *
     * @param data datos obtenidos del Proveedor de Contenido
     */
    private void showAlarmDetails(Cursor data) {
        Alarm a = UtilesContentProvider.cursorToSingleAlarm(data);
        if (a != null) {
            mView.showAlarmSport(a.getSport_id());

            Field field = UtilesContentProvider.getFieldFromContentProvider(a.getField_id());
            mView.showAlarmPlace(field, a.getCity());

            mView.showAlarmDate(a.getDate_from(), a.getDate_to());
            mView.showAlarmTotalPlayers(a.getTotal_players_from(), a.getTotal_players_to());
            mView.showAlarmEmptyPlayers(a.getEmpty_players_from(), a.getEmpty_players_to());
        } else {
            mView.clearUI();
        }
    }
}
