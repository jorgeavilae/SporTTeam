package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.alarms.alarmdetail.DetailAlarmFragment;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.mainactivities.AlarmsActivity;
import com.usal.jorgeav.sportapp.network.firebase.actions.AlarmFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;

/**
 * Presentador utilizado en la creación de alarmas. Aquí se validan todos los parámetros de
 * la alarma introducidos en la Vista {@link NewAlarmContract.View}. También inicia la consulta
 * al Proveedor de Contenido para obtener los datos de la alarma en caso de edición o para consultar
 * las posibles instalaciones sobre las que establecer la alarma, en ambos casos el resultado será
 * enviado a la Vista {@link NewAlarmContract.View}.
 * Implementa la interfaz {@link NewAlarmContract.Presenter} para la comunicación con esta clase y la
 * interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los callbacks de la
 * consulta.
 */
class NewAlarmPresenter implements
        NewAlarmContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    private static final String TAG = NewAlarmPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private NewAlarmContract.View mNewAlarmView;

    /**
     * Constructor
     *
     * @param view referencia a la Vista correspondiente a este Presentador
     */
    NewAlarmPresenter(NewAlarmContract.View view){
        this.mNewAlarmView = view;
    }

    /**
     * Valida los parámetros especificados y, si son correctos, crea la alarma en la base de
     * datos del servidor.
     *
     * @param alarmId identificador de la alarma si se está editando o null si se está creando
     * @param sport deporte de la alarma
     * @param field instalación sobre la que escucha la alarma
     * @param city ciudad sobre la que escucha la alarma
     * @param dateFrom limite inferior del rango de fechas en las que la alarma está buscando
     * @param dateTo límite superior del rango de fechas en las que la alarma está buscando
     * @param totalFrom límite inferior del rango de puestos totales de los partidos buscados
     * @param totalTo límite superior del rango de puestos totales de los partidos buscados
     * @param emptyFrom límite inferior del rango de puestos vacantes de los partidos buscados
     * @param emptyTo límite superior del rango de puestos vacantes de los partidos buscados
     */
    @Override
    public void addAlarm(String alarmId, String sport, String field, String city, String dateFrom, String dateTo,
                         String totalFrom, String totalTo, String emptyFrom, String emptyTo) {

        // Parse emptyPlayers string if needed (empty == "Infinite")
        if (mNewAlarmView.getActivityContext().getString(R.string.infinite).equals(emptyFrom))
            emptyFrom = "0";
        if (mNewAlarmView.getActivityContext().getString(R.string.infinite).equals(emptyTo))
            emptyTo = "0";

        Alarm a = new Alarm();
        a.setId(alarmId);

        // An alarm is valid if sport and city are necessarily set
        if (isValidSport(sport))
            a.setSport_id(sport);
        else {
            Toast.makeText(mNewAlarmView.getActivityContext(), R.string.toast_sport_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        // field could be null, but not city
        if (city == null || TextUtils.isEmpty(city))
            city = UtilesPreferences.getCurrentUserCity(mNewAlarmView.getActivityContext());

        if (isValidField(city, field, sport)) {
            a.setField_id(field);
            a.setCity(city);
        } else {
            Toast.makeText(mNewAlarmView.getActivityContext(), R.string.toast_place_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        // dateFrom must be at least today and dateTo should be greater than dateFrom or null
        if (isDateCorrect(dateFrom, dateTo)) {
            a.setDate_from(UtilesTime.stringDateToMillis(dateFrom));
            a.setDate_to(UtilesTime.stringDateToMillis(dateTo));
        } else {
            Toast.makeText(mNewAlarmView.getActivityContext(), R.string.toast_date_period_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        // totalFrom could be null and totalTo should be greater than totalFrom or null
        if (isTotalPlayersCorrect(totalFrom, totalTo)) {
            if (!TextUtils.isEmpty(totalFrom)) a.setTotal_players_from(Long.valueOf(totalFrom)); else a.setTotal_players_from(null);
            if (!TextUtils.isEmpty(totalTo)) a.setTotal_players_to(Long.valueOf(totalTo)); else a.setTotal_players_to(null);
        } else {
            Toast.makeText(mNewAlarmView.getActivityContext(), R.string.toast_total_player_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        // emptyFrom must be at least 1 and emptyTo should be greater than emptyFrom or null
        if (emptyFrom == null || TextUtils.isEmpty(emptyFrom)) emptyFrom = "1";
        if (isEmptyPlayersCorrect(emptyFrom, emptyTo)) {
            a.setEmpty_players_from(Long.valueOf(emptyFrom));
            if (!TextUtils.isEmpty(emptyTo)) a.setEmpty_players_to(Long.valueOf(emptyTo)); else a.setEmpty_players_to(null);
        } else {
            Toast.makeText(mNewAlarmView.getActivityContext(), R.string.toast_empty_players_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        long totalPlayersFrom = (a.getTotal_players_from()!=null ? a.getTotal_players_from():-1);
        long emptyPlayersTo = (a.getEmpty_players_to()!=null ? a.getEmpty_players_to():-1);
        if (totalPlayersFrom < emptyPlayersTo && emptyPlayersTo > 0) {
            Toast.makeText(mNewAlarmView.getActivityContext(), R.string.toast_players_relation_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        String myUserId = Utiles.getCurrentUserId();
        if (myUserId == null || TextUtils.isEmpty(myUserId)) return;
        AlarmFirebaseActions.addAlarm(a, myUserId);
        NotificationsFirebaseActions.checkOneAlarmAndNotify(a);
        ((AlarmsActivity)mNewAlarmView.getActivityContext()).mFieldId = null;
        ((AlarmsActivity)mNewAlarmView.getActivityContext()).mCity = null;
        ((AlarmsActivity)mNewAlarmView.getActivityContext()).mCoord = null;
        ((BaseFragment)mNewAlarmView).resetBackStack();
    }

    /**
     * Comprueba que el deporte es válido
     *
     * @param sport identificador del deporte
     *
     * @return true si es válido, false si no lo es
     */
    private boolean isValidSport(String sport) {
        if (!TextUtils.isEmpty(sport)) {
            // If R.array.sport_id contains this sport
            String[] arraySports = mNewAlarmView.getActivityContext().getResources().getStringArray(R.array.sport_id_values);
            for (String sportArr : arraySports)
                if (sport.equals(sportArr)) return true;
        }
        return false;
    }


    /**
     * Comprueba que el lugar el válido, teniendo en cuenta el deporte seleccionado para permitir
     * que la instalación no sea especificada en deportes que no requieren de una.
     *
     * @param city ciudad
     * @param fieldId identificador de la instalación
     * @param sportId identificador del deporte
     *
     * @return true si es válido, false si no lo es
     */
    private boolean isValidField(String city, String fieldId, String sportId) {
        if (city != null && !TextUtils.isEmpty(city)) {
            // Check if the sport doesn't need a field
            String[] arraySports = mNewAlarmView.getActivityContext().getResources().getStringArray(R.array.sport_id_values);
            if (sportId.equals(arraySports[0]) || sportId.equals(arraySports[1]))
                return true;

            if (fieldId != null && !TextUtils.isEmpty(fieldId)) {
                // Query database for the fieldId and checks if this sport exists
                Field field = UtilesContentProvider.getFieldFromContentProvider(fieldId);

                if (field != null && field.getCity().equals(city)
                        && field.containsSportCourt(sportId)) return true;
                else {
                    Log.e(TAG, "isValidField: not valid");
                    return false;
                }
            } else
                return true; //Could be null
        }
        Log.e(TAG, "isValidField: city not valid");
        return false;
    }

    /**
     * Comprueba que las fechas son válidas, asegurándose de que se establecen al menos a partir
     * del día actual
     *
     * @param dateFrom limite inferior del rango de fechas en las que la alarma está buscando
     * @param dateTo límite superior del rango de fechas en las que la alarma está buscando
     *
     * @return true si es válido, false si no lo es
     */
    private boolean isDateCorrect(String dateFrom, String dateTo) {
        Long dateFromMillis = null, dateToMillis = null;
        if (!TextUtils.isEmpty(dateFrom))
            dateFromMillis = UtilesTime.stringDateToMillis(dateFrom);
        if (!TextUtils.isEmpty(dateTo))
            dateToMillis = UtilesTime.stringDateToMillis(dateTo);

        if (dateFromMillis != null && dateFromMillis > 0)
            if (dateToMillis != null && dateToMillis > 0)
                return (DateUtils.isToday(dateFromMillis) || System.currentTimeMillis() < dateFromMillis)
                                && dateFromMillis <= dateToMillis;
            else
                return DateUtils.isToday(dateFromMillis) || System.currentTimeMillis() < dateFromMillis;
        return false;
    }

    /**
     * Comprueba que el rango de puestos totales especificado es correcto
     *
     * @param totalFrom límite inferior del rango de puestos totales de los partidos buscados
     * @param totalTo límite superior del rango de puestos totales de los partidos buscados
     *
     * @return true si es válido, false si no lo es
     */
    private boolean isTotalPlayersCorrect(String totalFrom, String totalTo) {
        if (TextUtils.isEmpty(totalFrom))
            return TextUtils.isEmpty(totalTo);
        return !TextUtils.isEmpty(totalTo) && Integer.valueOf(totalFrom) <= Integer.valueOf(totalTo);
    }

    /**
     * Comprueba que el rango de puestos vacantes especificado es correcto
     *
     * @param emptyFrom límite inferior del rango de puestos vacantes de los partidos buscados
     * @param emptyTo límite superior del rango de puestos vacantes de los partidos buscados
     *
     * @return true si es válido, false si no lo es
     */
    private boolean isEmptyPlayersCorrect(String emptyFrom, String emptyTo) {
        return !TextUtils.isEmpty(emptyFrom) && Integer.valueOf(emptyFrom) >= 0 &&
                (TextUtils.isEmpty(emptyTo) || Integer.valueOf(emptyFrom) <= Integer.valueOf(emptyTo));

    }

    /**
     * Inicia el proceso de carga de la alarma que se va a editar de la base de datos
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void openAlarm(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewAlarmFragment.BUNDLE_ALARM_ID)) {
            loaderManager.initLoader(SportteamLoader.LOADER_ALARM_ID, b, this);
        }
    }

    /**
     * Inicia el proceso de carga de las instalaciones de la base de datos
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadFields(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewAlarmFragment.BUNDLE_SPORT_SELECTED_ID))
            loaderManager.initLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT, b, this);
    }

    /**
     * Detiene el proceso de carga de las instalaciones de la base de datos
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     */
    @Override
    public void stopLoadFields(LoaderManager loaderManager) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT);
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     *
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderOneAlarm(Context, String)
     * @see SportteamLoader#cursorLoaderFieldsFromCityWithSport(Context, String, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String alarmId = args.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
        switch (id) {
            case SportteamLoader.LOADER_ALARM_ID:
                return SportteamLoader
                        .cursorLoaderOneAlarm(mNewAlarmView.getActivityContext(), alarmId);
            case SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT:
                String city = UtilesPreferences.getCurrentUserCity(mNewAlarmView.getActivityContext());
                String sportId = args.getString(NewAlarmFragment.BUNDLE_SPORT_SELECTED_ID);
                return SportteamLoader
                        .cursorLoaderFieldsFromCityWithSport(mNewAlarmView.getActivityContext(), city, sportId);
        }
        return null;
    }

    /**
     * Invocado cuando finaliza la consulta del Loader, actúa sobre los resultados obtenidos en
     * forma de {@link Cursor}.
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
            case SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT:
                ArrayList<Field> dataList = UtilesContentProvider.cursorToMultipleField(data);
                mNewAlarmView.retrieveFields(dataList);
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
            case SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT:
                mNewAlarmView.retrieveFields(null);
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
            mNewAlarmView.showAlarmSport(a.getSport_id());
            mNewAlarmView.showAlarmField(a.getField_id(), a.getCity());
            mNewAlarmView.showAlarmDate(a.getDate_from(), a.getDate_to());
            mNewAlarmView.showAlarmTotalPlayers(a.getTotal_players_from(), a.getTotal_players_to());
            mNewAlarmView.showAlarmEmptyPlayers(a.getEmpty_players_from(), a.getEmpty_players_to());
        } else {
            mNewAlarmView.clearUI();
        }
    }
}
