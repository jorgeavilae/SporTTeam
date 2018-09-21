package com.usal.jorgeav.sportapp.eventdetail.participants;

import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.actions.EventsFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.EventsFirebaseSync;

/**
 * Presentador utilizado para mostrar la colección de participantes del partido, tanto usuarios
 * de la aplicación, como usuarios simulados añadidos por los primeros.
 * <p>
 * <p>Aquí se inicia la consulta al Proveedor de Contenido para obtener los datos de los usuarios
 * que participan y la lista de los usuarios simulados asociados a ese partido. Serán enviados a la
 * Vista {@link ParticipantsContract.View}. También permite eliminar, tanto los participantes, como
 * los usuarios simulados.
 * <p>
 * <p>Implementa la interfaz {@link ParticipantsContract.Presenter} para la comunicación con
 * esta clase y la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los
 * callbacks de las consultas.
 */
class ParticipantsPresenter implements
        ParticipantsContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = ParticipantsPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private ParticipantsContract.View mParticipantsView;

    /**
     * Identificador del usuario creador del partido. Se consultan sus datos y se añaden a la lista
     * de participantes posteriormente.
     */
    private String mOwnerUid = "";

    /**
     * Constructor con argumentos
     *
     * @param mParticipantsView Vista correspondiente a este Presentador
     */
    ParticipantsPresenter(ParticipantsContract.View mParticipantsView) {
        this.mParticipantsView = mParticipantsView;
    }

    /**
     * Borra al usuario como participante del partido. También, dependiendo de un parámetro
     * booleano, se borran (o no) los usuarios simulados creados por ese usuario.
     *
     * @param userId                     identificador del usuario que va a ser eliminado como participante del
     *                                   partido
     * @param eventId                    identificador del partido
     * @param deleteSimulatedParticipant true si se quieren borrar también los usuarios
     *                                   simulados que <var>userId</var> haya podido añadir,
     */
    @Override
    public void quitEvent(String userId, String eventId, boolean deleteSimulatedParticipant) {
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(eventId))
            EventsFirebaseActions.quitEvent(userId, eventId, deleteSimulatedParticipant, true);
    }

    /**
     * Borra un usuario simulado del partido.
     *
     * @param simulatedUserId identificador del usuario simulado que va a ser eliminado del
     *                        partido
     * @param eventId         identificador del partido
     */
    @Override
    public void deleteSimulatedUser(String simulatedUserId, String eventId) {
        if (!TextUtils.isEmpty(simulatedUserId) && !TextUtils.isEmpty(eventId))
            EventsFirebaseActions.deleteSimulatedParticipant(simulatedUserId, eventId);
    }

    /**
     * Inicia el proceso de carga de la base de datos de los usuarios que participan en el partido
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadParticipants(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(ParticipantsFragment.BUNDLE_OWNER_ID))
            mOwnerUid = b.getString(ParticipantsFragment.BUNDLE_OWNER_ID);
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID, b, this);
    }

    /**
     * Inicia el proceso de carga de la base de datos de los usuarios simulados que participan en
     * el partido
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void loadSimulatedParticipants(LoaderManager loaderManager, Bundle b) {
        // Load Event to get an updated Simulated Participants list in Content Provider
        if (b != null && b.containsKey(ParticipantsFragment.BUNDLE_EVENT_ID)) {
            String eventId = b.getString(ParticipantsFragment.BUNDLE_EVENT_ID);
            if (eventId != null) EventsFirebaseSync.loadAnEvent(eventId);
        }
        if (b != null && b.containsKey(ParticipantsFragment.BUNDLE_OWNER_ID))
            mOwnerUid = b.getString(ParticipantsFragment.BUNDLE_OWNER_ID);
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID, b, this);
    }

    /**
     * Invocado por  {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String eventId = args.getString(ParticipantsFragment.BUNDLE_EVENT_ID);
        switch (id) {
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                return SportteamLoader
                        .cursorLoaderEventParticipants(mParticipantsView.getActivityContext(), eventId, true);
            case SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID:
                return SportteamLoader
                        .cursorLoaderEventSimulatedParticipants(mParticipantsView.getActivityContext(), eventId);
        }
        return null;
    }

    /**
     * Invocado cuando finaliza la consulta del Loader, entrega los resultados obtenidos en
     * forma de {@link Cursor} a la Vista. En el caso de los usuarios normales, añade como
     * participante al creador del evento.
     *
     * @param loader Loader utilizado para la consulta
     * @param data   resultado de la consulta
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                if (mOwnerUid != null && !TextUtils.isEmpty(mOwnerUid)) {
                    Cursor c = addParticipantToCursor(data, mOwnerUid);
                    mParticipantsView.showParticipants(c);
                }
                break;
            case SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID:
                mParticipantsView.showSimulatedParticipants(data);
                break;
        }
    }

    /**
     * Añade un usuario participante al {@link Cursor} proporcionado como parámetro. Utiliza el
     * objeto {@link MergeCursor} para fusionar en uno el Cursor de la consulta del Loader y el
     * Cursor obtenido al consultar los datos identificador de usuario <var>uid</var>
     *
     * @param data {@link Cursor} con el resto de participantes
     * @param uid  identificador del usuario que se quiere añadir a la colección
     * @return un Cursor con los usuarios de la consulta al Loader más el que se acaba de añadir
     */
    /* https://stackoverflow.com/a/16440093/4235666 */
    private Cursor addParticipantToCursor(Cursor data, String uid) {
        Cursor uidCursor = MyApplication.getAppContext().getContentResolver().query(
                SportteamContract.UserEntry.CONTENT_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.UserEntry.USER_ID + " = ? ",
                new String[]{uid},
                null);
        //todo comprobar este cambio: uidCursor puede cerrarse?
        MergeCursor result = new MergeCursor(new Cursor[]{uidCursor, data});
        if (uidCursor != null) uidCursor.close();
        return result;
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
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                mParticipantsView.showParticipants(null);
                break;
            case SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID:
                mParticipantsView.showSimulatedParticipants(null);
                break;
        }
    }
}
