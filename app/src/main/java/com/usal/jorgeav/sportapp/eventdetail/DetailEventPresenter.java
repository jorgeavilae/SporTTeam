package com.usal.jorgeav.sportapp.eventdetail;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.Invitation;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.actions.EventRequestFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.actions.EventsFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.actions.InvitationFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.EventsFirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

/**
 * Presentador utilizado en la vista de detalles de partidos. Aquí se inicia la consulta al
 * Proveedor de Contenido para obtener los datos del partido, el resultado será enviado a la
 * Vista {@link DetailEventContract.View}.
 * <p>
 * También se encarga de determinar la relación entre el usuario actual y el partido que se está
 * mostrando. Para ello utiliza una {@link AsyncTask} que permite consultar el Proveedor de
 * Contenido fuera del hilo principal. Además, se mantiene a la escucha de cambios en esa relación
 * mediante un patrón Observer.
 * <p>
 * Por último, desde la Vista se pueden iniciar los procesos de borrado del partido,
 * envío y cancelación de peticiones de participación, contestación de invitaciones, o incluso
 * permitir que el usuario abandone el partido. Todos esos procesos se realizan a través de esta clase.
 * <p>
 * Implementa la interfaz {@link DetailEventContract.Presenter} para la comunicación con esta clase
 * y la interfaz {@link LoaderManager.LoaderCallbacks} para ser notificado por los callbacks de la
 * consulta.
 */
public class DetailEventPresenter implements
        DetailEventContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = DetailEventPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private DetailEventContract.View mView;

    /**
     * Permite que el Presentador mantenga un callback sobre una URI de la base de datos que se
     * ejecuta cada vez esa URI es notificada a causa de un cambio en los datos a los que apunta.
     * Se basa en el patrón Observer.
     */
    private ContentObserver mContentObserver;

    /**
     * Identificador del usuario creador del partido
     */
    private String ownerUid = "";
    /**
     * Objeto {@link Invitation} con los parámetros asociados a la posible invitación recibida por
     * el usuario
     */
    private static Invitation mInvitation = null;

    /**
     * Constructor con argumentos. Aquí se inicializa el {@link ContentObserver} estableciendo su
     * comportamiento: determinar el nuevo tipo de relación con
     * {@link #getRelationTypeBetweenThisEventAndI()}
     *
     * @param view Vista correspondiente a este Presentador
     */
    DetailEventPresenter(@NonNull DetailEventContract.View view) {
        this.mView = view;

        mContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                getRelationTypeBetweenThisEventAndI();
            }
        };
    }

    /**
     * Inicia el proceso de carga del partido que se quiere mostrar de la base de datos.
     *
     * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
     *                      de Contenido
     * @param b             contenedor de posibles parámetros utilizados en la consulta
     */
    @Override
    public void openEvent(LoaderManager loaderManager, Bundle b) {
        String eventId = b.getString(DetailEventFragment.BUNDLE_EVENT_ID);
        if (eventId != null) EventsFirebaseSync.loadAnEvent(eventId);
        loaderManager.initLoader(SportteamLoader.LOADER_EVENT_ID, b, this);
    }

    /**
     * Invocado por {@link LoaderManager} para crear el Loader usado para la consulta
     *
     * @param id   identificador del Loader
     * @param args contenedor de posibles parámetros utilizados en la consulta
     * @return Loader que realiza la consulta.
     * @see SportteamLoader#cursorLoaderOneEvent(Context, String)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_EVENT_ID:
                String eventId = args.getString(DetailEventFragment.BUNDLE_EVENT_ID);
                if (eventId == null) return null;
                return SportteamLoader
                        .cursorLoaderOneEvent(mView.getActivityContext(), eventId);
        }
        return null;
    }

    /**
     * Invocado cuando finaliza la consulta del Loader, actúa sobre los resultados obtenidos en
     * forma de {@link Cursor}.
     *
     * @param loader Loader utilizado para la consulta
     * @param data   resultado de la consulta
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        showEventDetails(data);
    }

    /**
     * Invocado cuando el {@link LoaderManager} exige un reinicio del Loader indicado. Se utiliza
     * este método para borrar los resultados de la consulta anterior.
     *
     * @param loader Loader que va a reiniciarse.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        showEventDetails(null);
    }

    /**
     * Extrae del {@link Cursor} los datos del partido para enviarlos a la Vista con el formato
     * adecuado
     *
     * @param data datos obtenidos del Proveedor de Contenido
     */
    private void showEventDetails(Cursor data) {
        if (data != null && data.moveToFirst()) {
            mView.showEventSport(data.getString(SportteamContract.EventEntry.COLUMN_SPORT));

            Field field = UtilesContentProvider.getFieldFromContentProvider(
                    data.getString(SportteamContract.EventEntry.COLUMN_FIELD));
            String address = data.getString(SportteamContract.EventEntry.COLUMN_ADDRESS);
            double latitude = data.getDouble(SportteamContract.EventEntry.COLUMN_FIELD_LATITUDE);
            double longitude = data.getDouble(SportteamContract.EventEntry.COLUMN_FIELD_LONGITUDE);
            LatLng coord = null;
            if (latitude != 0 && longitude != 0) coord = new LatLng(latitude, longitude);
            mView.showEventField(field, address, coord);

            mView.showEventName(data.getString(SportteamContract.EventEntry.COLUMN_NAME));
            mView.showEventDate(data.getLong(SportteamContract.EventEntry.COLUMN_DATE));
            mView.showEventPlayers(data.getInt(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS),
                    data.getInt(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS));

            ownerUid = data.getString(SportteamContract.EventEntry.COLUMN_OWNER);
            mView.showEventOwner(ownerUid);
        } else {
            ownerUid = null;
            mInvitation = null;
            mView.clearUI();
        }
    }

    /**
     * Modificador que, aplicado a una variable, le permite adquirir como valor solamente el
     * conjunto de constantes que representan los distintos tipos de relación posible entre un
     * usuario y un partido.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RELATION_TYPE_ERROR, RELATION_TYPE_NONE, RELATION_TYPE_OWNER,
            RELATION_TYPE_I_SEND_REQUEST, RELATION_TYPE_I_RECEIVE_INVITATION,
            RELATION_TYPE_ASSISTANT, RELATION_TYPE_BLOCKED})
    public @interface EventRelationType {
    }

    /**
     * Error
     */
    public static final int RELATION_TYPE_ERROR = -1;
    /**
     * Ninguna relación
     */
    public static final int RELATION_TYPE_NONE = 0;
    /**
     * Creador
     */
    public static final int RELATION_TYPE_OWNER = 1; //Event.owner = me
    /**
     * Petición de participación enviada
     */
    public static final int RELATION_TYPE_I_SEND_REQUEST = 2; //Request.sender = me
    /**
     * Invitación al partido recibida
     */
    public static final int RELATION_TYPE_I_RECEIVE_INVITATION = 3; //Invitation.receiver = me
    /**
     * Participante en el partido
     */
    public static final int RELATION_TYPE_ASSISTANT = 4; //Participation true
    /**
     * Bloqueado para el partido
     */
    public static final int RELATION_TYPE_BLOCKED = 5; //Participation false

    /**
     * Crea y ejecuta una {@link AsyncTask} para realizar una serie de consultas al Proveedor de
     * Contenido que determinen la relación entre el usuario y el partido. Es necesario que las
     * consultas a la base de datos se realicen en segundo plano ya que pueden tardar demasiado
     * como para realizarlas sobre el hilo de ejecución principal, y ralentizarían la interfaz.
     */
    @Override
    public void getRelationTypeBetweenThisEventAndI() {
        // Do it in AsyncTask to avoid query ContentProvider in UI Thread
        new MyAsyncTask(mView).execute();
    }

    /**
     * Clase interna derivada de {@link AsyncTask} para realizar acciones fuera del hilo principal
     * de ejecución. Concretamente, esta clase se encarga de consultar el Proveedor de Contenido
     * buscando la relación entre un identificador de usuario y un identificador de partido dados.
     * Además, mantiene una referencia a la Vista correspondiente al Presentador para poder
     * comunicarle los resultado de la consulta.
     * <p>
     * Crear esta clase interna con una referencia débil {@link WeakReference} a la Vista
     * evita fugas de memoria, evitando una conexión fuerte entre la Vista, con un ciclo de vida
     * propio, y este objeto, que se ejecuta fuera del hilo de la interfaz, permite al GC borrar
     * la Vista.
     */
    private static class MyAsyncTask extends AsyncTask<Void, Void, Integer> {

        /**
         * Referencia a la Vista para comunicar resultados
         *
         * @see <a href="https://developer.android.com/reference/java/lang/ref/WeakReference">
         * WeakReference</a>
         */
        private WeakReference<DetailEventContract.View> mView;

        /**
         * Constructor
         *
         * @param view Vista correspondiente al Presentador
         */
        MyAsyncTask(DetailEventContract.View view) {
            mView = new WeakReference<>(view);
        }

        /**
         * Método de {@link AsyncTask} que se ejecuta fuera del hilo principal. Aquí se realizan
         * secuencialmente las consultas a diferentes tablas del Proveedor de Contenido hasta
         * determinar la relación entre el usuario y el partido.
         *
         * @param voids no se requiere ningún parámetro
         * @return tipo de relación según las constantes declaradas en {@link EventRelationType}
         */
        @Override
        protected Integer doInBackground(Void... voids) {
            // Check if DetailEventView still exists
            DetailEventContract.View detailEventView = mView.get();
            if (detailEventView == null) return RELATION_TYPE_ERROR;

            try {
                String myUid = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(myUid)) return RELATION_TYPE_ERROR;

                //Owner?
                Cursor cursorOwner = detailEventView.getActivityContext().getContentResolver().query(
                        SportteamContract.EventEntry.CONTENT_EVENT_URI,
                        new String[]{SportteamContract.EventEntry.EVENT_ID_TABLE_PREFIX},
                        SportteamContract.EventEntry.EVENT_ID + " = ? AND "
                                + SportteamContract.EventEntry.OWNER + " = ?",
                        new String[]{detailEventView.getEventID(), myUid},
                        null);
                if (cursorOwner != null) {
                    if (cursorOwner.getCount() > 0) {
                        cursorOwner.close();
                        return RELATION_TYPE_OWNER;
                    }
                    cursorOwner.close();
                }

                //I have received an Invitation?
                Cursor cursorReceiver = detailEventView.getActivityContext().getContentResolver().query(
                        SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI,
                        SportteamContract.EventsInvitationEntry.EVENT_INVITATIONS_COLUMNS,
                        SportteamContract.EventsInvitationEntry.EVENT_ID + " = ? AND "
                                + SportteamContract.EventsInvitationEntry.RECEIVER_ID + " = ? ",
                        new String[]{detailEventView.getEventID(), myUid},
                        null);
                if (cursorReceiver != null) {
                    if (cursorReceiver.getCount() > 0 && cursorReceiver.moveToFirst()) {
                        // In this case, store Invitation to accept it or decline it later
                        String sender = cursorReceiver.getString(SportteamContract.EventsInvitationEntry.COLUMN_SENDER_ID);
                        Long date = cursorReceiver.getLong(SportteamContract.EventsInvitationEntry.COLUMN_DATE);
                        mInvitation = new Invitation(sender, myUid, detailEventView.getEventID(), date);

                        cursorReceiver.close();
                        return RELATION_TYPE_I_RECEIVE_INVITATION;
                    }
                    cursorReceiver.close();
                }

                //I have sent a EventRequest?
                Cursor cursorSender = detailEventView.getActivityContext().getContentResolver().query(
                        SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI,
                        new String[]{SportteamContract.EventRequestsEntry.EVENT_ID_TABLE_PREFIX},
                        SportteamContract.EventRequestsEntry.SENDER_ID + " = ? AND "
                                + SportteamContract.EventRequestsEntry.EVENT_ID + " = ?",
                        new String[]{myUid, detailEventView.getEventID()},
                        null);
                if (cursorSender != null) {
                    if (cursorSender.getCount() > 0) {
                        cursorSender.close();
                        return RELATION_TYPE_I_SEND_REQUEST;
                    }
                    cursorSender.close();
                }

                //I assist
                Cursor cursorAssist = detailEventView.getActivityContext().getContentResolver().query(
                        SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                        new String[]{SportteamContract.EventsParticipationEntry.EVENT_ID_TABLE_PREFIX},
                        SportteamContract.EventsParticipationEntry.EVENT_ID + " = ? AND "
                                + SportteamContract.EventsParticipationEntry.USER_ID + " = ? AND "
                                + SportteamContract.EventsParticipationEntry.PARTICIPATES + " = ?",
                        new String[]{detailEventView.getEventID(), myUid, String.valueOf(1)}, /*1 -> true*/
                        null);
                if (cursorAssist != null) {
                    if (cursorAssist.getCount() > 0) {
                        cursorAssist.close();
                        return RELATION_TYPE_ASSISTANT;
                    }
                    cursorAssist.close();
                }

                //I don't assist
                Cursor cursorNotAssist = detailEventView.getActivityContext().getContentResolver().query(
                        SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                        new String[]{SportteamContract.EventsParticipationEntry.EVENT_ID_TABLE_PREFIX},
                        SportteamContract.EventsParticipationEntry.EVENT_ID + " = ? AND "
                                + SportteamContract.EventsParticipationEntry.USER_ID + " = ? AND "
                                + SportteamContract.EventsParticipationEntry.PARTICIPATES + " = ?",
                        new String[]{detailEventView.getEventID(), myUid, String.valueOf(0)}, /*0 -> false*/
                        null);
                if (cursorNotAssist != null) {
                    if (cursorNotAssist.getCount() > 0) {
                        cursorNotAssist.close();
                        return RELATION_TYPE_BLOCKED;
                    }
                    cursorNotAssist.close();
                }

                //No relation
                return RELATION_TYPE_NONE;
            } catch (NullPointerException e) {
                e.printStackTrace();
                return RELATION_TYPE_ERROR;
            }
        }

        /**
         * Envía el resultado obtenido en {@link #doInBackground(Void...)} a la Vista. Este método
         * ya no se ejecuta en segundo plano.
         *
         * @param integer tipo de relación según las constantes declaradas en
         *                {@link EventRelationType}
         */
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mView.get().uiSetupForEventRelation(integer);
        }
    }

    /**
     * Invocado desde la Vista cuando el usuario quiere enviar una petición de participación
     * al partido
     *
     * @param eventId identificador del partido
     */
    @Override
    public void sendEventRequest(String eventId) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(ownerUid))
            EventRequestFirebaseActions.sendEventRequest(myUid, eventId, ownerUid);
    }

    /**
     * Invocado desde la Vista cuando el usuario quiere cancelar una petición de participación
     * enviada al partido previamente
     *
     * @param eventId identificador del partido
     */
    @Override
    public void cancelEventRequest(String eventId) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(ownerUid))
            EventRequestFirebaseActions.cancelEventRequest(myUid, eventId, ownerUid);
    }

    /**
     * Invocado desde la Vista cuando el usuario quiere aceptar la invitación al partido que ha recibido
     *
     * @param eventId identificador del partido
     * @param sender  identificador del usuario que envió la petición
     */
    @Override
    public void acceptEventInvitation(String eventId, String sender) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(sender))
            InvitationFirebaseActions.acceptEventInvitation(mView.getThis(), myUid, eventId, sender);
    }

    /**
     * Invocado desde la Vista cuando el usuario quiere rechazar la invitación al partido que ha
     * recibido
     *
     * @param eventId identificador del partido
     * @param sender  identificador del usuario que envió la petición
     */
    @Override
    public void declineEventInvitation(String eventId, String sender) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(sender))
            InvitationFirebaseActions.declineEventInvitation(myUid, eventId, sender);
    }

    /**
     * Devuelve la posible invitación al partido recibida por el usuario
     *
     * @return la invitación recibida o null
     */
    @Override
    public Invitation getEventInvitation() {
        return mInvitation;
    }

    /**
     * Invocado desde la Vista cuando el usuario ya no quiere asistir al partido
     *
     * @param eventId                     identificador del partido
     * @param deleteSimulatedParticipants true si se desea borrar también los usuarios simulados
     *                                    {@link SimulatedUser} añadidos por el usuario que ya no
     *                                    asistirá, false en otro caso.
     */
    @Override
    public void quitEvent(String eventId, boolean deleteSimulatedParticipants) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId))
            EventsFirebaseActions.quitEvent(myUid, eventId, deleteSimulatedParticipants);
    }

    /**
     * Invocado desde la Vista cuando el creador del partido desea eliminarlo de la base de datos
     *
     * @param eventId identificador del partido
     */
    @Override
    public void deleteEvent(String eventId) {
        EventsFirebaseActions.deleteEvent(mView.getThis(), eventId);
    }

    /**
     * Registra y activa el Observer creado en el Constructor de esta clase sobre la URI
     * correspondiente a la relación entre usuario y partido
     */
    @Override
    public void registerUserRelationObserver() {
        // Register observer to listen for changes in event-user relation
        mView.getActivityContext().getContentResolver().registerContentObserver(
                SportteamContract.UserEntry.CONTENT_USER_RELATION_EVENT_URI, false, mContentObserver);
    }

    /**
     * Desactiva el Observer creado en el Constructor de esta clase y que estaba puesto sobre la URI
     * correspondiente a la relación entre usuario y partido
     */
    @Override
    public void unregisterUserRelationObserver() {
        mView.getActivityContext().getContentResolver().unregisterContentObserver(mContentObserver);
    }
}
