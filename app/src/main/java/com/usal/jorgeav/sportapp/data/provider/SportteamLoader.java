package com.usal.jorgeav.sportapp.data.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Clase con métodos auxiliares, invocados desde varios puntos de la aplicación, utilizados para
 * consultar el Proveedor de Contenido de la aplicación, {@link SportteamProvider}.
 * <p>
 * Contiene métodos que instancian {@link CursorLoader}s con los parámetros necesarios para
 * consultar datos, correspondientes a identificadores de objetos, desde Loaders creados en los
 * Presentadores de los distintos Fragmentos de la aplicación. También hay métodos en los que
 * se realiza alguna consulta y se devuelve el resultado obtenido en forma de {@link Cursor}. Se
 * realiza con la ayuda de {@link SportteamContract}.
 */
public final class SportteamLoader {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = SportteamLoader.class.getSimpleName();

    /**
     * Identificador del Loader creado para consultar los datos de un perfil de usuario
     */
    public static final int LOADER_PROFILE_ID = 1010;
    /**
     * Identificador del Loader creado para consultar los deportes practicado por un usuario
     */
    public static final int LOADER_PROFILE_SPORTS_ID = 1011;

    /**
     * Crea un CursorLoader para consultar los datos de un perfil de usuario
     *
     * @param context contexto bajo el que se ejecuta
     * @param userId  identificador del usuario consultado
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderOneUser(Context context, String userId) {
        // Return user data
        return new CursorLoader(
                context,
                SportteamContract.UserEntry.CONTENT_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.UserEntry.USER_ID + " = ?",
                new String[]{userId},
                null);
    }

    /**
     * Crea un CursorLoader para consultar los deportes practicado por un usuario
     *
     * @param context contexto bajo el que se ejecuta
     * @param userId  identificador del usuario
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderSportsUser(Context context, String userId) {
        // Return sports data for userId
        return new CursorLoader(
                context,
                SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                SportteamContract.UserSportEntry.USER_SPORT_COLUMNS,
                SportteamContract.UserSportEntry.USER_ID + " = ?",
                new String[]{userId},
                null);
    }


    /**
     * Identificador del Loader creado para consultar los partidos creados por el usuario y los
     * partidos en los que participa
     */
    public static final int LOADER_MY_EVENTS_AND_PARTICIPATION_ID = 2100;

    /**
     * Crea un CursorLoader para consultar los partidos creados por el usuario y los partidos en
     * los que participa
     *
     * @param context  contexto bajo el que se ejecuta
     * @param myUserID identificador del usuario
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderMyEventsAndParticipation(Context context, String myUserID) {
        // Return my participation events
        return new CursorLoader(
                context,
                SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_WITH_EVENT_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.EventEntry.OWNER_TABLE_PREFIX + " = ? OR "
                        + "(" + SportteamContract.EventsParticipationEntry.USER_ID_TABLE_PREFIX + " = ? AND "
                        + SportteamContract.EventsParticipationEntry.PARTICIPATES_TABLE_PREFIX + " = ? )",
                new String[]{myUserID, myUserID, String.valueOf(1)},
                SportteamContract.EventEntry.DATE_TABLE_PREFIX + " DESC LIMIT 50");
    }

    /**
     * Identificador del Loader creado para consultar un partido
     */
    public static final int LOADER_EVENT_ID = 2010;
    /**
     * Identificador del Loader creado para consultar los datos de los usuarios
     * participantes/bloqueados de un partido
     */
    public static final int LOADER_EVENTS_PARTICIPANTS_ID = 2011;
    /**
     * Identificador del Loader creado para consultar los datos de los usuarios simulados
     * participantes de un partido
     */
    public static final int LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID = 2012;

    /**
     * Crea un CursorLoader para consultar un partido
     *
     * @param context contexto bajo el que se ejecuta
     * @param eventId identificador del partido
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderOneEvent(Context context, String eventId) {
        // Return event data
        return new CursorLoader(
                context,
                SportteamContract.EventEntry.CONTENT_EVENT_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.EventEntry.EVENT_ID + " = ?",
                new String[]{eventId},
                null);
    }

    /**
     * Crea un CursorLoader para consultar los datos de los usuarios participantes/bloqueados de
     * un partido
     *
     * @param context     contexto bajo el que se ejecuta
     * @param eventId     identificador del partido
     * @param participate true si se consultan los participantes, false si se consultan los
     *                    bloqueados
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderEventParticipants(Context context, String eventId, boolean participate) {
        // Return user data for participants in eventId
        return new CursorLoader(
                context,
                SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " = ? AND "
                        + SportteamContract.EventsParticipationEntry.PARTICIPATES_TABLE_PREFIX + " = ?",
                new String[]{eventId, String.valueOf(participate ? 1 : 0)},
                SportteamContract.EventsParticipationEntry.USER_ID_TABLE_PREFIX + " ASC");
    }

    /**
     * Crea un CursorLoader para consultar los identificadores de los usuarios participantes y los
     * bloqueados de un partido
     *
     * @param context contexto bajo el que se ejecuta
     * @param eventId identificador del partido
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderEventParticipantsNoData(Context context, String eventId) {
        // Return userId for participants in eventId
        return new CursorLoader(
                context,
                SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                SportteamContract.EventsParticipationEntry.EVENTS_PARTICIPATION_COLUMNS,
                SportteamContract.EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " = ? ",
                new String[]{eventId},
                SportteamContract.EventsParticipationEntry.USER_ID_TABLE_PREFIX + " ASC");
    }

    /**
     * Crea un CursorLoader para consultar los datos de los usuarios simulados participantes de
     * un partido
     *
     * @param context contexto bajo el que se ejecuta
     * @param eventId identificador del partido
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderEventSimulatedParticipants(Context context, String eventId) {
        // Return user data for participants in eventId
        return new CursorLoader(
                context,
                SportteamContract.SimulatedParticipantEntry.CONTENT_SIMULATED_PARTICIPANT_URI,
                SportteamContract.SimulatedParticipantEntry.SIMULATED_PARTICIPANTS_COLUMNS,
                SportteamContract.SimulatedParticipantEntry.EVENT_ID_TABLE_PREFIX + " = ? ",
                new String[]{eventId},
                SportteamContract.SimulatedParticipantEntry.ALIAS_TABLE_PREFIX + " ASC");
    }


    /**
     * Identificador del Loader creado para consultar los datos de los usuarios amigos de un usuario
     */
    public static final int LOADER_FRIENDS_ID = 3000;

    /**
     * Crea un CursorLoader para consultar los datos de los usuarios amigos de un usuario
     *
     * @param context  contexto bajo el que se ejecuta
     * @param myUserID identificador del usuario
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderFriends(Context context, String myUserID) {
        // Return user data for all of my friends
        return new CursorLoader(
                context,
                SportteamContract.FriendsEntry.CONTENT_FRIEND_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.FriendsEntry.MY_USER_ID_TABLE_PREFIX + " = ?",
                new String[]{myUserID},
                SportteamContract.FriendsEntry.DATE_TABLE_PREFIX + " ASC");
    }

    /**
     * Identificador del Loader creado para consultar los datos de los usuarios que mandaron una
     * petición de amistad a un usuario
     */
    public static final int LOADER_FRIENDS_REQUESTS_ID = 3100;

    /**
     * Crea un CursorLoader para consultar los datos de los usuarios que mandaron una petición de
     * amistad a un usuario
     *
     * @param context  contexto bajo el que se ejecuta
     * @param myUserID identificador del usuario
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderFriendRequests(Context context, String myUserID) {
        // Return user data for all of my friends requests
        return new CursorLoader(
                context,
                SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.FriendRequestEntry.RECEIVER_ID_TABLE_PREFIX + " = ?",
                new String[]{myUserID},
                SportteamContract.FriendRequestEntry.DATE_TABLE_PREFIX + " ASC");
    }


    /**
     * Identificador del Loader creado para consultar los datos de los usuarios que recibieron una
     * invitación a un partido por parte de un usuario
     */
    public static final int LOADER_EVENT_INVITATIONS_SENT_ID = 4100;

    /**
     * Crea un CursorLoader para consultar los datos de los usuarios que recibieron una invitación
     * a un partido por parte de un usuario
     *
     * @param context  contexto bajo el que se ejecuta
     * @param eventId  identificador del partido
     * @param myUserId identificador del usuario
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderUsersForEventInvitationsSent(Context context,
                                                                        String eventId,
                                                                        String myUserId) {
        // Return user data for invitations sent in eventId
        return new CursorLoader(
                context,
                SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " = ? AND "
                        + SportteamContract.EventsInvitationEntry.SENDER_ID_TABLE_PREFIX + " = ? ",
                new String[]{eventId, myUserId},
                SportteamContract.EventsInvitationEntry.DATE_TABLE_PREFIX + " ASC");
    }

    /**
     * Identificador del Loader creado para consultar los datos de los partidos para los que un
     * usuario recibió invitación
     */
    public static final int LOADER_EVENT_INVITATIONS_RECEIVED_ID = 4200;

    /**
     * Crea un CursorLoader para consultar los datos de los partidos para los que un usuario
     * recibió invitación
     *
     * @param context  contexto bajo el que se ejecuta
     * @param myUserId identificador del usuario
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderEventsForEventInvitationsReceived(Context context, String myUserId) {
        // Return event data for invitations received in myUserId
        return new CursorLoader(
                context,
                SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_WITH_EVENT_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.EventsInvitationEntry.RECEIVER_ID_TABLE_PREFIX + " = ? ",
                new String[]{myUserId},
                SportteamContract.EventsInvitationEntry.DATE_TABLE_PREFIX + " ASC");
    }


    /**
     * Identificador del Loader creado para consultar los datos de los usuarios que enviaron una
     * petición de participación a un partido
     */
    public static final int LOADER_USERS_REQUESTS_RECEIVED_ID = 5100;

    /**
     * Crea un CursorLoader para consultar los datos de los usuarios que enviaron una petición de
     * participación a un partido
     *
     * @param context contexto bajo el que se ejecuta
     * @param eventId identificador del partido
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderUsersForEventRequestsReceived(Context context, String eventId) {
        // Return user data for requests received in eventId
        return new CursorLoader(
                context,
                SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " = ? ",
                new String[]{eventId},
                SportteamContract.EventRequestsEntry.DATE_TABLE_PREFIX + " ASC");
    }

    /**
     * Identificador del Loader creado para consultar los datos de los partidos a los que un usuario
     * envió peticiones de participación
     */
    public static final int LOADER_EVENT_REQUESTS_SENT_ID = 5200;

    /**
     * Crea un CursorLoader para consultar los datos de los partidos a los que un usuario envió
     * peticiones de participación
     *
     * @param context  contexto bajo el que se ejecuta
     * @param myUserId identificador del usuario
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderEventsForEventRequestsSent(Context context, String myUserId) {
        // Return event data for event requests sent by myUserId
        return new CursorLoader(
                context,
                SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_WITH_EVENT_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.EventRequestsEntry.SENDER_ID_TABLE_PREFIX + " = ? ",
                new String[]{myUserId},
                SportteamContract.EventRequestsEntry.DATE_TABLE_PREFIX + " ASC");
    }


    /**
     * Identificador del Loader creado para consultar los datos de los usuarios de una ciudad y que
     * no tengan relación con un usuario
     */
    public static final int LOADER_USERS_FROM_CITY = 1100;
    /**
     * Identificador del Loader creado para consultar los datos de los usuarios cuyo nombre
     * contenga una cadena de caracteres dada y que no tengan relación con un usuario
     */
    public static final int LOADER_USERS_WITH_NAME = 1200;

    /**
     * Crea un CursorLoader para consultar los datos de los usuarios de una ciudad y que no tengan
     * relación con un usuario
     *
     * @param context  contexto bajo el que se ejecuta
     * @param myUserId identificador del usuario
     * @param city     ciudad
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderUsersFromCity(Context context, String myUserId, String city) {
        // Return user data from users in city
        return new CursorLoader(
                context,
                SportteamContract.JoinQueryEntries.CONTENT_NOT_FRIENDS_USERS_FROM_CITY_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.JoinQueryEntries.WHERE_NOT_FRIENDS_USERS_FROM_CITY,
                SportteamContract.JoinQueryEntries.queryNotFriendsUsersFromCityArguments(myUserId, city),
                SportteamContract.UserEntry.NAME_TABLE_PREFIX + " ASC");
    }

    /**
     * Crea un CursorLoader para consultar los datos de los usuarios cuyo nombre contenga una
     * cadena de caracteres dada y que no tengan relación con un usuario
     *
     * @param context  contexto bajo el que se ejecuta
     * @param myUserId identificador del usuario
     * @param username cadena de caracteres del nombre
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderUsersWithName(Context context, String myUserId, String username) {
        //Return user data from users with username
        return new CursorLoader(
                context,
                SportteamContract.JoinQueryEntries.CONTENT_NOT_FRIENDS_USERS_WITH_NAME_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.JoinQueryEntries.WHERE_NOT_FRIENDS_USERS_WITH_NAME,
                SportteamContract.JoinQueryEntries.queryNotFriendsUsersWithNameArguments(myUserId, username),
                SportteamContract.UserEntry.NAME_TABLE_PREFIX + " ASC");
    }


    /**
     * Identificador del Loader creado para consultar los datos de los partidos de una ciudad y que
     * no tengan relación con un usuario
     */
    public static final int LOADER_EVENTS_FROM_CITY = 2300;
    /**
     * Identificador del Loader creado para consultar los datos de los partidos de una ciudad, que
     * no tengan relación con un usuario y que cumplan una serie de parámetros
     */
    public static final int LOADER_EVENTS_WITH_PARAMS = 2400;

    /**
     * Crea un CursorLoader para consultar los datos de los partidos de una ciudad y que no tengan
     * relación con un usuario
     *
     * @param context  contexto bajo el que se ejecuta
     * @param myUserId identificador del usuario
     * @param city     ciudad
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderEventsFromCity(Context context, String myUserId, String city) {
        // Return event data from events in city
        return new CursorLoader(
                context,
                SportteamContract.JoinQueryEntries.CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.JoinQueryEntries.WHERE_CITY_EVENTS_WITHOUT_RELATION_WITH_ME,
                SportteamContract.JoinQueryEntries.queryCityEventsWithoutRelationWithMeArguments(myUserId, city),
                SportteamContract.EventEntry.DATE_TABLE_PREFIX + " ASC");
    }

    /**
     * Crea un CursorLoader para consultar los datos de los partidos de una ciudad, que no tengan
     * relación con un usuario y que cumplan una serie de parámetros
     *
     * @param context   contexto bajo el que se ejecuta
     * @param myUserId  identificador del usuario
     * @param city      ciudad
     * @param sportId   identificador del deporte
     * @param dateFrom  limite inferior del periodo de fechas
     * @param dateTo    limite superior del periodo de fechas
     * @param totalFrom limite inferior del rango de puestos totales
     * @param totalTo   limite superior del rango de puestos totales
     * @param emptyFrom limite inferior del rango de puestos vacantes
     * @param emptyTo   limite superior del rango de puestos vacantes
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderEventsWithParams(Context context, String myUserId, String city,
                                                            String sportId, Long dateFrom, Long dateTo,
                                                            int totalFrom, int totalTo, int emptyFrom, int emptyTo) {
        String selection = SportteamContract.JoinQueryEntries.WHERE_CITY_EVENTS_WITHOUT_RELATION_WITH_ME;
        ArrayList<String> selectionArgs = new ArrayList<>(Arrays.asList(SportteamContract.JoinQueryEntries.queryCityEventsWithoutRelationWithMeArguments(myUserId, city)));

        if (sportId != null && !TextUtils.isEmpty(sportId)) {
            selection += "AND " + SportteamContract.EventEntry.SPORT_TABLE_PREFIX + " = ? ";
            selectionArgs.add(sportId);
        }

        if (dateFrom != null && dateFrom > -1) {
            selection += "AND " + SportteamContract.EventEntry.DATE_TABLE_PREFIX + " >= ? ";
            selectionArgs.add(dateFrom.toString());
        }
        if (dateTo != null && dateTo > -1) {
            selection += "AND " + SportteamContract.EventEntry.DATE_TABLE_PREFIX + " <= ? ";
            selectionArgs.add(dateTo.toString());
        }

        if (totalFrom > -1) {
            selection += "AND " + SportteamContract.EventEntry.TOTAL_PLAYERS_TABLE_PREFIX + " >= ? ";
            selectionArgs.add(Integer.toString(totalFrom));
        }
        if (totalTo > -1) {
            selection += "AND " + SportteamContract.EventEntry.TOTAL_PLAYERS_TABLE_PREFIX + " <= ? ";
            selectionArgs.add(Integer.toString(totalTo));
        }

        if (emptyFrom > -1) {
            selection += "AND " + SportteamContract.EventEntry.EMPTY_PLAYERS_TABLE_PREFIX + " >= ? ";
            selectionArgs.add(Integer.toString(emptyFrom));
        }
        if (emptyTo > -1) {
            selection += "AND " + SportteamContract.EventEntry.EMPTY_PLAYERS_TABLE_PREFIX + " <= ? ";
            selectionArgs.add(Integer.toString(emptyTo));
        }

        return new CursorLoader(
                context,
                SportteamContract.JoinQueryEntries.CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                selection,
                selectionArgs.toArray(new String[0]),
                SportteamContract.EventEntry.DATE_TABLE_PREFIX + " ASC");
    }


    /**
     * Identificador del Loader creado para consultar los datos de las instalaciones de una ciudad
     */
    public static final int LOADER_FIELDS_FROM_CITY = 6100;

    /**
     * Crea un CursorLoader para consultar los datos de las instalaciones de una ciudad
     *
     * @param context contexto bajo el que se ejecuta
     * @param city    ciudad
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderFieldsFromCity(Context context, String city) {
        //Return field data from fields in city
        return new CursorLoader(
                context,
                SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                SportteamContract.FieldEntry.FIELDS_COLUMNS,
                SportteamContract.FieldEntry.CITY + " = ? ",
                new String[]{city},
                SportteamContract.FieldEntry.COLUMN_NAME + " DESC");
    }

    /**
     * Identificador del Loader creado para consultar los datos de las instalaciones de una ciudad
     * que contengan una pista para un deporte determinado
     */
    public static final int LOADER_FIELDS_FROM_CITY_WITH_SPORT = 6200;

    /**
     * Crea un CursorLoader para consultar los datos de las instalaciones de una ciudad que
     * contengan una pista para un deporte determinado
     *
     * @param context contexto bajo el que se ejecuta
     * @param city    ciudad
     * @param sportId identificador del deporte
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderFieldsFromCityWithSport(Context context, String city, String sportId) {
        // Return field data from fields with sportId
        return new CursorLoader(
                context,
                SportteamContract.FieldEntry.CONTENT_FIELDS_WITH_FIELD_SPORT_URI,
                SportteamContract.FieldEntry.FIELDS_COLUMNS,
                SportteamContract.FieldEntry.CITY_TABLE_PREFIX + " = ? AND "
                        + SportteamContract.FieldSportEntry.SPORT_TABLE_PREFIX + " = ? ",
                new String[]{city, sportId},
                SportteamContract.FieldEntry.NAME + " ASC");
    }

    /**
     * Identificador del Loader creado para consultar los datos de una instalación
     */
    public static final int LOADER_FIELD_ID = 6010;
    /**
     * Identificador del Loader creado para consultar los datos de las pistas de una instalación
     */
    public static final int LOADER_FIELD_SPORTS_ID = 6011;

    /**
     * Crea un CursorLoader para consultar los datos de una instalación
     *
     * @param context contexto bajo el que se ejecuta
     * @param fieldId identificador de la instalación
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderOneField(Context context, String fieldId) {
        // Return field data
        return new CursorLoader(
                context,
                SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                SportteamContract.FieldEntry.FIELDS_COLUMNS,
                SportteamContract.FieldEntry.FIELD_ID + " = ? ",
                new String[]{fieldId},
                null);
    }

    /**
     * Crea un CursorLoader para consultar los datos de las pistas de una instalación
     *
     * @param context contexto bajo el que se ejecuta
     * @param fieldId identificador de la instalación
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderFieldSports(Context context, String fieldId) {
        // Return field data
        return new CursorLoader(
                context,
                SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                SportteamContract.FieldSportEntry.FIELD_SPORT_COLUMNS,
                SportteamContract.FieldSportEntry.FIELD_ID + " = ? ",
                new String[]{fieldId},
                null);
    }


    /**
     * Identificador del Loader creado para consultar los datos de las alarmas
     */
    public static final int LOADER_MY_ALARMS_ID = 7100;

    /**
     * Crea un CursorLoader para consultar los datos de las alarmas
     *
     * @param context contexto bajo el que se ejecuta
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderMyAlarms(Context context) {
        // Return my events
        return new CursorLoader(
                context,
                SportteamContract.AlarmEntry.CONTENT_ALARM_URI,
                SportteamContract.AlarmEntry.ALARM_COLUMNS,
                null,
                null,
                SportteamContract.AlarmEntry.DATE_FROM_TABLE_PREFIX + " DESC");
    }

    /**
     * Identificador del Loader creado para consultar los datos de una alarma
     */
    public static final int LOADER_ALARM_ID = 7200;
    /**
     * Identificador del Loader creado para consultar los datos de los partidos de una ciudad, sin
     * relación con un usuario y que coincidan con los parámetros de una alarma
     */
    public static final int LOADER_ALARM_EVENTS_COINCIDENCE_ID = 7210;

    /**
     * Crea un CursorLoader para consultar los datos de una alarma
     *
     * @param context contexto bajo el que se ejecuta
     * @param alarmId identificador de la alarma
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderOneAlarm(Context context, String alarmId) {
        // Return event data
        return new CursorLoader(
                context,
                SportteamContract.AlarmEntry.CONTENT_ALARM_URI,
                SportteamContract.AlarmEntry.ALARM_COLUMNS,
                SportteamContract.AlarmEntry.ALARM_ID + " = ?",
                new String[]{alarmId},
                null);
    }

    /**
     * Crea un CursorLoader para consultar los datos de los partidos de una ciudad, sin relación
     * con un usuario y que coincidan con los parámetros de una alarma
     *
     * @param context  contexto bajo el que se ejecuta
     * @param alarmId  identificador de la alarma
     * @param myUserId identificador del usuario
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderAlarmCoincidence(Context context, String alarmId, String myUserId) {
        Alarm alarm = UtilesContentProvider.cursorToSingleAlarm(simpleQueryAlarmId(context, alarmId));

        if (alarm != null) {
            String selection = SportteamContract.JoinQueryEntries.WHERE_CITY_EVENTS_WITHOUT_RELATION_WITH_ME;
            ArrayList<String> selectionArgs = new ArrayList<>(Arrays.asList(SportteamContract.JoinQueryEntries.queryCityEventsWithoutRelationWithMeArguments(myUserId, alarm.getCity())));

            // sportId is always set
            selection += "AND " + SportteamContract.EventEntry.SPORT_TABLE_PREFIX + " = ? ";
            selectionArgs.add(alarm.getSport_id());

            // field could be null
            if (alarm.getField_id() != null) {
                selection += "AND " + SportteamContract.EventEntry.FIELD_TABLE_PREFIX + " = ? ";
                selectionArgs.add(alarm.getField_id());
            }

            // dateFrom must be at least today and dateTo should be greater than dateFrom or null
            selection += "AND " + SportteamContract.EventEntry.DATE_TABLE_PREFIX + " >= ? ";
            selectionArgs.add(alarm.getDate_from().toString());
            if (alarm.getDate_to() != null) {
                selection += "AND " + SportteamContract.EventEntry.DATE_TABLE_PREFIX + " <= ? ";
                selectionArgs.add(alarm.getDate_to().toString());
            }

            // totalFrom could be null and totalTo should be greater than totalFrom or null
            if (alarm.getTotal_players_from() != null) {
                selection += "AND " + SportteamContract.EventEntry.TOTAL_PLAYERS_TABLE_PREFIX + " >= ? ";
                selectionArgs.add(alarm.getTotal_players_from().toString());
            }
            if (alarm.getTotal_players_to() != null) {
                selection += "AND " + SportteamContract.EventEntry.TOTAL_PLAYERS_TABLE_PREFIX + " <= ? ";
                selectionArgs.add(alarm.getTotal_players_to().toString());
            }

            // emptyFrom must be at least 1 and emptyTo should be greater than emptyFrom or null
            selection += "AND " + SportteamContract.EventEntry.EMPTY_PLAYERS_TABLE_PREFIX + " >= ? ";
            selectionArgs.add(alarm.getEmpty_players_from().toString());
            if (alarm.getEmpty_players_to() != null) {
                selection += "AND " + SportteamContract.EventEntry.EMPTY_PLAYERS_TABLE_PREFIX + " <= ? ";
                selectionArgs.add(alarm.getEmpty_players_to().toString());
            }

            return new CursorLoader(
                    context,
                    SportteamContract.JoinQueryEntries.CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI,
                    SportteamContract.EventEntry.EVENT_COLUMNS,
                    selection,
                    selectionArgs.toArray(new String[0]),
                    SportteamContract.EventEntry.DATE_TABLE_PREFIX + " ASC");
            /*
            SELECT all event columns
            FROM event
                LEFT JOIN eventsParticipation
                    ON (event.eventId = eventsParticipation.eventId AND eventsParticipation.userId = XPs5... )
                LEFT JOIN eventInvitations
                    ON (event.eventId = eventInvitations.eventId AND eventInvitations.receiverId = XPs5... )
                LEFT JOIN eventRequest
                    ON (event.eventId = eventRequest.eventId AND eventRequest.senderId = XPs5... )
            WHERE (
                event.city = ciudad AND
                event.sport = basketball AND
                event.owner <> XPs5... AND
                eventsParticipation.eventId IS NULL AND
                eventInvitations.eventId IS NULL AND
                eventRequest.eventId IS NULL AND

                event.field = -Kka... AND
                event.date >= 1497823200000 AND event.date <= 1497909600000 AND
                event.totalPlayers >= 22 AND event.totalPlayers <= 22 AND
                event.emptyPlayers >= 2 AND event.emptyPlayers <= 2
                )
             */
        }
        return null;
    }

    /**
     * Realiza una consulta sobre los datos de los partidos de una ciudad, sin relación con un
     * usuario y que coincidan con los parámetros de una alarma; y devuelve el Cursor obtenido
     *
     * @param contentResolver referencia al Content Resolver para realizar la consulta
     * @param alarm           objeto Alarm del que obtener los parámetros de la consulta
     * @param myUserId        identificador del usuario
     * @return una instancia de Cursor
     */
    public static Cursor cursorAlarmCoincidence(ContentResolver contentResolver, Alarm alarm, String myUserId) {
        if (alarm != null) {
            String selection = SportteamContract.JoinQueryEntries.WHERE_CITY_EVENTS_WITHOUT_RELATION_WITH_ME;
            // Store as ArrayList to use add() method to complete selection arguments list.
            ArrayList<String> selectionArgs = new ArrayList<>(Arrays.asList(SportteamContract.
                    JoinQueryEntries.queryCityEventsWithoutRelationWithMeArguments(myUserId, alarm.getCity())));

            // sportId is always set
            selection += "AND " + SportteamContract.EventEntry.SPORT_TABLE_PREFIX + " = ? ";
            selectionArgs.add(alarm.getSport_id());

            // field could be null
            if (alarm.getField_id() != null) {
                selection += "AND " + SportteamContract.EventEntry.FIELD_TABLE_PREFIX + " = ? ";
                selectionArgs.add(alarm.getField_id());
            }

            // dateFrom must be at least today and dateTo should be greater than dateFrom or null
            selection += "AND " + SportteamContract.EventEntry.DATE_TABLE_PREFIX + " >= ? ";
            selectionArgs.add(alarm.getDate_from().toString());
            if (alarm.getDate_to() != null) {
                selection += "AND " + SportteamContract.EventEntry.DATE_TABLE_PREFIX + " <= ? ";
                selectionArgs.add(alarm.getDate_to().toString());
            }

            // totalFrom could be null and totalTo should be greater than totalFrom or null
            if (alarm.getTotal_players_from() != null) {
                selection += "AND " + SportteamContract.EventEntry.TOTAL_PLAYERS_TABLE_PREFIX + " >= ? ";
                selectionArgs.add(alarm.getTotal_players_from().toString());
            }
            if (alarm.getTotal_players_to() != null) {
                selection += "AND " + SportteamContract.EventEntry.TOTAL_PLAYERS_TABLE_PREFIX + " <= ? ";
                selectionArgs.add(alarm.getTotal_players_to().toString());
            }

            // emptyFrom must be at least 1 and emptyTo should be greater than emptyFrom or null
            selection += "AND " + SportteamContract.EventEntry.EMPTY_PLAYERS_TABLE_PREFIX + " >= ? ";
            selectionArgs.add(alarm.getEmpty_players_from().toString());
            if (alarm.getEmpty_players_to() != null) {
                selection += "AND " + SportteamContract.EventEntry.EMPTY_PLAYERS_TABLE_PREFIX + " <= ? ";
                selectionArgs.add(alarm.getEmpty_players_to().toString());
            }

            return contentResolver.query(
                    SportteamContract.JoinQueryEntries.CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI,
                    SportteamContract.EventEntry.EVENT_COLUMNS,
                    selection,
                    selectionArgs.toArray(new String[0]),
                    SportteamContract.EventEntry.DATE_TABLE_PREFIX + " ASC");
        }
        return null;
    }

    /**
     * Identificador del Loader creado para consultar los datos de los partidos de un usuario que
     * no tengan relación con otro usuario, para saber a que partidos puede invitarle
     */
    public static final int LOADER_EVENTS_FOR_INVITATION_ID = 8100;

    /**
     * Crea un CursorLoader para consultar los datos de los partidos de un usuario que no tengan
     * relación con otro usuario, para saber a que partidos puede invitarle
     *
     * @param context     contexto bajo el que se ejecuta
     * @param myUserID    identificador del usuario
     * @param otherUserID identificador del otro usuario
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderEventsForInvitation(Context context, String myUserID, String otherUserID) {
        // Return all of my events data in which otherUser has no relation
        return new CursorLoader(
                context,
                SportteamContract.JoinQueryEntries.CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.JoinQueryEntries.WHERE_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND,
                SportteamContract.JoinQueryEntries.queryMyEventsWithoutRelationWithFriendArguments(myUserID, otherUserID),
                SportteamContract.EventEntry.DATE_TABLE_PREFIX + " ASC");
    }

    /**
     * Identificador del Loader creado para consultar los datos de los usuarios amigos de un usuario
     * que no tengan relación con un partido de dicho usuario, para saber a quien puede invitar a
     * dicho partido
     */
    public static final int LOADER_USERS_FOR_INVITE_ID = 8200;

    /**
     * Crea un CursorLoader para consultar los datos de los usuarios amigos de un usuario que no
     * tengan relación con un partido de dicho usuario, para saber a quien puede invitar a dicho
     * partido
     *
     * @param context  contexto bajo el que se ejecuta
     * @param myUserID identificador del usuario
     * @param eventId  identificador del partido
     * @return una instancia de CursorLoader
     */
    public static CursorLoader cursorLoaderUsersForInvite(Context context, String myUserID, String eventId) {
        // Return all of my friends data who has no relation with eventId
        return new CursorLoader(
                context,
                SportteamContract.JoinQueryEntries.CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.JoinQueryEntries.WHERE_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS,
                SportteamContract.JoinQueryEntries.queryMyFriendsWithoutRelationWithMyEventsArguments(myUserID, eventId),
                SportteamContract.FriendsEntry.DATE_TABLE_PREFIX + " ASC");
    }

    /**
     * Realiza una consulta sobre los datos de un usuario, y devuelve el Cursor obtenido
     *
     * @param context contexto bajo el que se ejecuta
     * @param userId  identificador del usuario
     * @return resultado de la consulta contenido en un Cursor
     */
    public static Cursor simpleQueryUserId(Context context, String userId) {
        return context.getContentResolver().query(
                SportteamContract.UserEntry.CONTENT_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.UserEntry.USER_ID + " = ? ",
                new String[]{userId},
                null);
    }

    /**
     * Realiza una consulta sobre la foto de perfil de un usuario, y devuelve el Cursor obtenido
     *
     * @param context contexto bajo el que se ejecuta
     * @param userId  identificador del usuario
     * @return resultado de la consulta contenido en un Cursor
     */
    public static Cursor simpleQueryUserIdPicture(Context context, String userId) {
        return context.getContentResolver().query(
                SportteamContract.UserEntry.CONTENT_USER_URI,
                new String[]{SportteamContract.UserEntry.PHOTO_TABLE_PREFIX},
                SportteamContract.UserEntry.USER_ID + " = ? ",
                new String[]{userId},
                null);
    }

    /**
     * Realiza una consulta sobre el nombre de un usuario, y devuelve el Cursor obtenido
     *
     * @param context contexto bajo el que se ejecuta
     * @param userId  identificador del usuario
     * @return resultado de la consulta contenido en un Cursor
     */
    public static Cursor simpleQueryUserIdName(Context context, String userId) {
        return context.getContentResolver().query(
                SportteamContract.UserEntry.CONTENT_USER_URI,
                new String[]{SportteamContract.UserEntry.NAME_TABLE_PREFIX},
                SportteamContract.UserEntry.USER_ID + " = ? ",
                new String[]{userId},
                null);
    }

    /**
     * Realiza una consulta sobre los datos de un partido, y devuelve el Cursor obtenido
     *
     * @param context contexto bajo el que se ejecuta
     * @param eventId identificador del partido
     * @return resultado de la consulta contenido en un Cursor
     */
    public static Cursor simpleQueryEventId(Context context, String eventId) {
        return context.getContentResolver().query(
                SportteamContract.EventEntry.CONTENT_EVENT_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.EventEntry.EVENT_ID + " = ? ",
                new String[]{eventId},
                null);
    }

    /**
     * Realiza una consulta sobre el deporte de un partido, y devuelve el Cursor obtenido
     *
     * @param context contexto bajo el que se ejecuta
     * @param eventId identificador del partido
     * @return resultado de la consulta contenido en un Cursor
     */
    public static Cursor simpleQueryEventIdSport(Context context, String eventId) {
        return context.getContentResolver().query(
                SportteamContract.EventEntry.CONTENT_EVENT_URI,
                new String[]{SportteamContract.EventEntry.SPORT_TABLE_PREFIX},
                SportteamContract.EventEntry.EVENT_ID + " = ? ",
                new String[]{eventId},
                null);
    }

    /**
     * Realiza una consulta sobre los datos de una instalación, y devuelve el Cursor obtenido
     *
     * @param context contexto bajo el que se ejecuta
     * @param fieldId identificador de la instalación
     * @return resultado de la consulta contenido en un Cursor
     */
    public static Cursor simpleQueryFieldId(Context context, String fieldId) {
        return context.getContentResolver().query(
                SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                SportteamContract.FieldEntry.FIELDS_COLUMNS,
                SportteamContract.FieldEntry.FIELD_ID + " = ? ",
                new String[]{fieldId},
                null);
    }

    /**
     * Realiza una consulta sobre los datos de las pistas de una instalación, y devuelve el
     * Cursor obtenido
     *
     * @param context contexto bajo el que se ejecuta
     * @param fieldId identificador de la instalación
     * @return resultado de la consulta contenido en un Cursor
     */
    public static Cursor simpleQuerySportsOfFieldId(Context context, String fieldId) {
        return context.getContentResolver().query(
                SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                SportteamContract.FieldSportEntry.FIELD_SPORT_COLUMNS,
                SportteamContract.FieldSportEntry.FIELD_ID + " = ? ",
                new String[]{fieldId},
                null);
    }

    /**
     * Realiza una consulta sobre el nombre de una instalación, y devuelve el Cursor obtenido
     *
     * @param context contexto bajo el que se ejecuta
     * @param fieldId identificador de la instalación
     * @return resultado de la consulta contenido en un Cursor
     */
    public static Cursor simpleQueryFieldIdName(Context context, String fieldId) {
        return context.getContentResolver().query(
                SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                new String[]{SportteamContract.FieldEntry.NAME_TABLE_PREFIX},
                SportteamContract.FieldEntry.FIELD_ID + " = ? ",
                new String[]{fieldId},
                null);
    }

    /**
     * Realiza una consulta sobre los datos de una alarma, y devuelve el Cursor obtenido
     *
     * @param context contexto bajo el que se ejecuta
     * @param alarmId identificador de la alarma
     * @return resultado de la consulta contenido en un Cursor
     */
    public static Cursor simpleQueryAlarmId(Context context, String alarmId) {
        return context.getContentResolver().query(
                SportteamContract.AlarmEntry.CONTENT_ALARM_URI,
                SportteamContract.AlarmEntry.ALARM_COLUMNS,
                SportteamContract.AlarmEntry.ALARM_ID + " = ?",
                new String[]{alarmId},
                null);
    }

    /**
     * Realiza una consulta sobre el deporte de una alarma, y devuelve el Cursor obtenido
     *
     * @param context contexto bajo el que se ejecuta
     * @param alarmId identificador de la alarma
     * @return resultado de la consulta contenido en un Cursor
     */
    public static Cursor simpleQueryAlarmIdSport(Context context, String alarmId) {
        return context.getContentResolver().query(
                SportteamContract.AlarmEntry.CONTENT_ALARM_URI,
                new String[]{SportteamContract.AlarmEntry.SPORT_TABLE_PREFIX},
                SportteamContract.AlarmEntry.ALARM_ID + " = ? ",
                new String[]{alarmId},
                null);
    }

    /**
     * Realiza una consulta sobre los datos de los partidos creados y los partidos a los que asiste
     * un usuario, y devuelve el Cursor obtenido
     *
     * @param context contexto bajo el que se ejecuta
     * @param userID  identificador del usuario
     * @return resultado de la consulta contenido en un Cursor
     */
    public static Cursor simpleQueryMyEventsAndEventsParticipation(Context context, String userID) {
        return context.getContentResolver().query(
                SportteamContract.JoinQueryEntries.CONTENT_MY_EVENTS_AND_PARTICIPATION_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.JoinQueryEntries.WHERE_MY_EVENTS_AND_PARTICIPATION,
                SportteamContract.JoinQueryEntries.queryMyEventsAndParticipationArguments(userID),
                SportteamContract.EventEntry.DATE_TABLE_PREFIX + " ASC");
    }
}
