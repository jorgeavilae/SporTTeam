package com.usal.jorgeav.sportapp.events.addevent;

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
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.mainactivities.EventsActivity;
import com.usal.jorgeav.sportapp.network.firebase.actions.EventsFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Presentador utilizado en la creación o edición de partidos. Aquí se validan todos los parámetros
 * del partido introducidos en la Vista {@link NewEventContract.View}. También inicia la consulta
 * al Proveedor de Contenido para obtener esos datos en caso de edición o para consultar
 * las posibles instalaciones sobre las que establecer la alarma o los amigos del usuario actual,
 * en ambos casos el resultado será enviado a la Vista {@link NewEventContract.View}.
 * <p>
 * Implementa la interfaz {@link NewEventContract.Presenter} para la comunicación con esta clase y
 * la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los callbacks de la
 * consulta.
 */
class NewEventPresenter implements
        NewEventContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    public static final String TAG = NewEventPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private NewEventContract.View mNewEventView;

    /**
     * Constructor
     *
     * @param view Vista correspondiente a este Presentador
     */
    NewEventPresenter(NewEventContract.View view) {
        this.mNewEventView = view;
    }

    /**
     * Valida los parámetros especificados y, si son correctos, crea el partido en la base de
     * datos del servidor y finaliza el Fragmento.
     *
     * @param id                    identificador del partido que se está editando, o null si es
     *                              un proceso de creación
     * @param sport                 deporte del partido
     * @param field                 identificador de la instalación donde se juega el partido, o
     *                              null si el deporte no necesita una instalación
     * @param address               dirección del lugar donde se juega el partido. Coincide con
     *                              el de la instalación, si la hay.
     * @param coord                 coordenadas del lugar donde se juega el partido. Coincide
     *                              con las de la instalación, si la hay.
     * @param name                  nombre del partido
     * @param city                  ciudad donde se juega el partido. Coincide con la de la
     *                              instalación, si la hay.
     * @param date                  fecha del partido
     * @param time                  hora del partido
     * @param total                 número total de jugadores para el partido
     * @param empty                 número de puestos vacantes
     * @param participants          listado de participantes ya inscritos en el momento de la
     *                              edición, o null en el caso de la creación.
     * @param simulatedParticipants listado de participantes simulados ya inscritos en el
     *                              momento de la edición, o null en el caso de la creación.
     * @param friendsId             listado de amigos del usuario creador del partido, a los que
     * @see EventsFirebaseActions#addEvent(Event, ArrayList)
     * @see EventsFirebaseActions#editEvent(Event)
     */
    @Override
    public void addEvent(String id, String sport, String field, String address, LatLng coord, String name, String city,
                         String date, String time, String total, String empty,
                         HashMap<String, Boolean> participants,
                         HashMap<String, SimulatedUser> simulatedParticipants,
                         ArrayList<String> friendsId) {
        // Parse date & time
        Long dateMillis = UtilesTime.stringDateToMillis(date);
        Long timeMillis = UtilesTime.stringTimeToMillis(time);

        // Parse emptyPlayers string if needed (empty == "Infinite")
        if (mNewEventView.getActivityContext().getString(R.string.infinite).equals(empty))
            empty = "0";

        // Get owner
        String myUid = Utiles.getCurrentUserId();

        if (isValidSport(sport) && isValidField(field, address, sport, city, coord) && isValidName(name)
                && isValidOwner(myUid) && isDateTimeCorrect(dateMillis, timeMillis) && isPlayersCorrect(total, empty, sport)) {

            Event event = new Event(id, sport, field, address, coord, name, city,
                    dateMillis + timeMillis, myUid, Long.valueOf(total), Long.valueOf(empty),
                    participants, simulatedParticipants);

            Log.d(TAG, "addEvent: " + event);
            if (TextUtils.isEmpty(event.getEvent_id()))
                EventsFirebaseActions.addEvent(event, friendsId);
            else
                EventsFirebaseActions.editEvent(event);

            ((EventsActivity) mNewEventView.getActivityContext()).mFieldId = null;
            ((EventsActivity) mNewEventView.getActivityContext()).mAddress = null;
            ((EventsActivity) mNewEventView.getActivityContext()).mCity = null;
            ((EventsActivity) mNewEventView.getActivityContext()).mCoord = null;
            mNewEventView.getThis().resetBackStack();
        }
    }

    /**
     * Comprueba que el deporte es válido
     *
     * @param sport identificador del deporte
     * @return true si es válido, false en caso contrario
     */
    private boolean isValidSport(String sport) {
        // If R.array.sport_id contains this sport
        String[] arraySports = mNewEventView.getActivityContext().getResources().getStringArray(R.array.sport_id_values);
        for (String sportArr : arraySports)
            if (sport.equals(sportArr)) return true;
        Toast.makeText(mNewEventView.getActivityContext(), R.string.toast_sport_invalid, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "isValidSport: not valid");
        return false;
    }

    /**
     * Comprueba que la dirección es válida. Si el deporte necesita una instalación, comprueba que
     * los datos proporcionados coinciden con el de la instalación especificada.
     *
     * @param fieldId     identificador de la instalación o null si el deporte no la necesita
     * @param address     dirección del partido
     * @param sportId     identificador del deporte del partido
     * @param city        ciudad del partido
     * @param coordinates coordenadas del partido
     * @return true si es válido, false en caso contrario
     */
    private boolean isValidField(String fieldId, String address, String sportId, String city, LatLng coordinates) {
        // Check if the sport doesn't need a field and address is valid
        if (!Utiles.sportNeedsField(sportId) && address != null && !TextUtils.isEmpty(address))
            return true;

        // Query database for the fieldId and checks if this sport exists
        Field field = UtilesContentProvider.getFieldFromContentProvider(fieldId);

        if (field != null
                && field.getAddress().equals(address)
                && field.getCity().equals(city)
                && field.getCoord_latitude() == coordinates.latitude
                && field.getCoord_longitude() == coordinates.longitude
                && field.containsSportCourt(sportId))
            return true;
        else {
            Toast.makeText(mNewEventView.getActivityContext(), R.string.toast_place_invalid, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "isValidField: not valid");
            return false;
        }
    }

    /**
     * Comprueba que el nombre es válido
     *
     * @param name nombre del partido
     * @return true si es válido, false en caso contrario
     */
    private boolean isValidName(String name) {
        if (!TextUtils.isEmpty(name)) return true;
        Toast.makeText(mNewEventView.getActivityContext(), R.string.error_invalid_name, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "isValidName: not valid");
        return false;
    }

    /**
     * Comprueba que el identificador del usuario creador, el usuario actual, es válido
     *
     * @param uid identificador del usuario actual
     * @return true si es válido, false en caso contrario
     */
    private boolean isValidOwner(String uid) {
        if (!TextUtils.isEmpty(uid)) return true;
        Toast.makeText(mNewEventView.getActivityContext(), R.string.toast_invalid_arg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "isValidOwner: not valid");
        return false;
    }

    /**
     * Comprueba que la fecha y la hora es válida y no que pertenecen al pasado
     *
     * @param date fecha en milisegundos
     * @param time hora en milisegundos
     * @return true si es válido, false en caso contrario
     */
    private boolean isDateTimeCorrect(Long date, Long time) {
        if (date != null && time != null && System.currentTimeMillis() < date + time) return true;
        Toast.makeText(mNewEventView.getActivityContext(), R.string.toast_date_invalid, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "isDateTimeCorrect: incorrect");
        return false;
    }

    /**
     * Comprueba que la cantidad de jugadores totales y puestos vacantes es correcta. Si se han
     * especificado puestos vacantes infinitos, se comprueba si el deporte lo permite.
     *
     * @param total   puestos totales para el partido
     * @param empty   puestos vacantes para el partido
     * @param sportId identificador del deporte
     * @return true si es válido, false en caso contrario
     */
    private boolean isPlayersCorrect(String total, String empty, String sportId) {
        // If total is grater than empty OR infinite is checked and sport doesn't need a field
        if (!TextUtils.isEmpty(total) && !TextUtils.isEmpty(empty))
            if (Integer.valueOf(total) >= Integer.valueOf(empty)
                    || (empty.equals("0") && !Utiles.sportNeedsField(sportId)))
                return true;
        Toast.makeText(mNewEventView.getActivityContext(), R.string.toast_players_relation_invalid, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "isPlayersCorrect: incorrect");
        return false;
    }

    /**
     * Inicia el proceso de consulta a la base de datos del partido que se va a editar
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void openEvent(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewEventFragment.BUNDLE_EVENT_ID)) {
            loaderManager.initLoader(SportteamLoader.LOADER_EVENT_ID, b, this);
            loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID, b, this);
            loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID, b, this);
        }
    }

    /**
     * Inicia el proceso de consulta a la base de datos de las instalaciones
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadFields(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewEventFragment.BUNDLE_SPORT_SELECTED_ID))
            loaderManager.initLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT, b, this);
    }

    /**
     * Inicia el proceso de consulta a la base de datos de los amigos del usuario actual
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadFriends(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(NewEventFragment.BUNDLE_SPORT_SELECTED_ID))
            loaderManager.initLoader(SportteamLoader.LOADER_FRIENDS_ID, b, this);
    }

    /**
     * Detiene el proceso de consulta a la base de datos de las instalaciones
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     */
    @Override
    public void stopLoadFields(LoaderManager loaderManager) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT);
    }

    /**
     * Detiene el proceso de consulta a la base de datos de los amigos del usuario actual
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     */
    @Override
    public void stopLoadFriends(LoaderManager loaderManager) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_FRIENDS_ID);
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderOneEvent(Context, String)
     * @see SportteamLoader#cursorLoaderEventParticipantsNoData(Context, String)
     * @see SportteamLoader#cursorLoaderEventSimulatedParticipants(Context, String)
     * @see SportteamLoader#cursorLoaderFieldsFromCityWithSport(Context, String, String)
     * @see SportteamLoader#cursorLoaderFriends(Context, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String eventId;
        switch (id) {
            case SportteamLoader.LOADER_EVENT_ID:
                eventId = args.getString(NewEventFragment.BUNDLE_EVENT_ID);
                return SportteamLoader
                        .cursorLoaderOneEvent(mNewEventView.getActivityContext(), eventId);
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                eventId = args.getString(NewEventFragment.BUNDLE_EVENT_ID);
                return SportteamLoader
                        .cursorLoaderEventParticipantsNoData(mNewEventView.getActivityContext(), eventId);
            case SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID:
                eventId = args.getString(NewEventFragment.BUNDLE_EVENT_ID);
                return SportteamLoader
                        .cursorLoaderEventSimulatedParticipants(mNewEventView.getActivityContext(), eventId);
            case SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT:
                String city = UtilesPreferences.getCurrentUserCity(mNewEventView.getActivityContext());
                String sportId = args.getString(NewEventFragment.BUNDLE_SPORT_SELECTED_ID);
                return SportteamLoader
                        .cursorLoaderFieldsFromCityWithSport(mNewEventView.getActivityContext(), city, sportId);
            case SportteamLoader.LOADER_FRIENDS_ID:
                String currentUserID = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(currentUserID)) return null;
                return SportteamLoader
                        .cursorLoaderFriends(mNewEventView.getActivityContext(), currentUserID);
        }
        return null;
    }

    /**
     * Invocado cuando finaliza la consulta del Loader, envía a la Vista los resultados obtenidos en
     * forma de {@link Cursor}.
     *
     * @param loader Loader utilizado para la consulta
     * @param data   resultado de la consulta
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_EVENT_ID:
                showEventDetails(data);
                break;
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                mNewEventView.setParticipants(UtilesContentProvider.cursorToMultipleParticipants(data));
                break;
            case SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID:
                mNewEventView.setSimulatedParticipants(UtilesContentProvider.cursorToMultipleSimulatedParticipants(data));
                break;
            case SportteamLoader.LOADER_FIELDS_FROM_CITY_WITH_SPORT:
                ArrayList<Field> dataList = UtilesContentProvider.cursorToMultipleField(data);
                mNewEventView.retrieveFields(dataList);
                break;
            case SportteamLoader.LOADER_FRIENDS_ID:
                ArrayList<String> friendsList = UtilesContentProvider.cursorToMultipleFriendsID(data);
                mNewEventView.retrieveFriendsID(friendsList);
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
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_EVENT_ID:
                showEventDetails(null);
                break;
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                mNewEventView.setParticipants(null);
                break;
            case SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID:
                mNewEventView.setSimulatedParticipants(null);
                break;
        }
    }

    /**
     * Extrae del {@link Cursor} los datos del partido para enviarlos a la Vista con el formato
     * adecuado
     *
     * @param data datos obtenidos del Proveedor de Contenido
     */
    private void showEventDetails(Cursor data) {
        if (data != null && data.moveToFirst()) {
            String fieldId = data.getString(SportteamContract.EventEntry.COLUMN_FIELD);
            String address = data.getString(SportteamContract.EventEntry.COLUMN_ADDRESS);
            String city = data.getString(SportteamContract.EventEntry.COLUMN_CITY);
            double latitude = data.getDouble(SportteamContract.EventEntry.COLUMN_FIELD_LATITUDE);
            double longitude = data.getDouble(SportteamContract.EventEntry.COLUMN_FIELD_LONGITUDE);
            LatLng coordinates = null;
            if (latitude != 0 && longitude != 0) coordinates = new LatLng(latitude, longitude);

            mNewEventView.showEventField(fieldId, address, city, coordinates);
            mNewEventView.showEventSport(data.getString(SportteamContract.EventEntry.COLUMN_SPORT));
            mNewEventView.showEventName(data.getString(SportteamContract.EventEntry.COLUMN_NAME));
            mNewEventView.showEventDate(data.getLong(SportteamContract.EventEntry.COLUMN_DATE));
            mNewEventView.showEventTotalPlayers(data.getInt(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS));
            mNewEventView.showEventEmptyPlayers(data.getInt(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS));
        } else {
            mNewEventView.clearUI();
        }
    }
}
