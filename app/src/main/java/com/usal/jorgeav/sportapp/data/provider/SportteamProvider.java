package com.usal.jorgeav.sportapp.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.usal.jorgeav.sportapp.data.provider.SportteamContract.AlarmEntry;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract.EmailLoggedEntry;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract.JoinQueryEntries;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract.SimulatedParticipantEntry;

import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.CONTENT_AUTHORITY;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.EventEntry;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.EventRequestsEntry;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.EventsInvitationEntry;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.EventsParticipationEntry;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.FieldEntry;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.FriendRequestEntry;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.FriendsEntry;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_ALARMS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_CITY_EVENTS_WITHOUT_RELATION_WITH_ME;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EMAIL_LOGGED;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENTS_PARTICIPATION;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENTS_PARTICIPATION_WITH_EVENT;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENTS_PARTICIPATION_WITH_USER;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENTS_REQUESTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENTS_REQUESTS_WITH_EVENT;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENTS_REQUESTS_WITH_USER;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENT_INVITATIONS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENT_INVITATIONS_WITH_EVENT;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENT_INVITATIONS_WITH_USER;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENT_SIMULATED_PARTICIPANT;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_FIELDS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_FIELDS_WITH_FIELD_SPORT;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_FIELD_SPORT;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_FRIENDS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_FRIENDS_REQUESTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_FRIENDS_REQUESTS_WITH_USER;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_FRIENDS_WITH_USER;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_MY_EVENTS_AND_PARTICIPATION;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_NOT_FRIENDS_USERS_FROM_CITY;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_NOT_FRIENDS_USERS_WITH_NAME;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_USERS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_USER_SPORT;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_ALARM;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_EMAIL_LOGGED;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_EVENT;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_EVENTS_PARTICIPATION;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_EVENTS_REQUESTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_EVENT_INVITATIONS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_EVENT_SIMULATED_PARTICIPANT;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_FIELD;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_FIELD_SPORTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_FRIENDS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_FRIENDS_REQUESTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_USER;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_USER_SPORTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.UserEntry;

/**
 * Esta clase representa al Proveedor de Contenido que se ejecuta cuando el Content Resolver
 * direcciona las URI hacia esta aplicación por coincidir con la URI especificada para esta clase en
 * el manifiesto (AndroidManifest.xml).
 * <p>
 * Al crearse, se obtiene una instancia de {@link SportteamDBHelper} sobre las que se realizan las
 * operaciones de una base de datos: inserción (e inserción múltiple), actualización, borrado y
 * consultas.
 * <p>
 * Para ello utiliza un {@link UriMatcher} con el que direccionar, dentro de este Proveedor de
 * Contenido, la tabla o tablas (o tuplas) de la base de datos SQLite sobre la que ejercer la
 * operación indicada. Para ello se utilizan una serie de códigos que se asocian a la ruta de la
 * URI que indica la tabla o tablas (o tuplas).
 */
public class SportteamProvider extends ContentProvider {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = SportteamProvider.class.getSimpleName();

    /**
     * Código utilizado por {@link UriMatcher} para la tabla de emails de los usuarios que alguna vez
     * iniciaron sesión con éxito
     */
    public static final int CODE_EMAIL_LOGGED = 10;
    /**
     * Código utilizado por {@link UriMatcher} para la tabla de usuarios
     */
    public static final int CODE_USERS = 100;
    /**
     * Código utilizado por {@link UriMatcher} para la tabla de deportes practicados por usuarios
     */
    public static final int CODE_USER_SPORT = 110;
    /**
     * Código utilizado por {@link UriMatcher} para la tabla de instalaciones
     */
    public static final int CODE_FIELDS = 200;
    /**
     * Código utilizado por {@link UriMatcher} para la tabla de pistas de una instalación
     */
    public static final int CODE_FIELD_SPORT = 210;
    /**
     * Código utilizado por {@link UriMatcher} para las tablas de instalaciones y sus pistas
     */
    public static final int CODE_FIELD_WITH_FIELD_SPORT = 211;
    /**
     * Código utilizado por {@link UriMatcher} para la tabla de partidos
     */
    public static final int CODE_EVENTS = 300;
    /**
     * Código utilizado por {@link UriMatcher} para la tabla de usuarios simulados
     */
    public static final int CODE_SIMULATED_PARTICIPANTS = 310;
    /**
     * Código utilizado por {@link UriMatcher} para la tabla de alarmas
     */
    public static final int CODE_ALARMS = 400;
    /**
     * Código utilizado por {@link UriMatcher} para la tabla de peticiones de amistad
     */
    public static final int CODE_FRIEND_REQUEST = 500;
    /**
     * Código utilizado por {@link UriMatcher} para las tablas de peticiones de amistad y de
     * esos usuarios
     */
    public static final int CODE_FRIEND_REQUEST_WITH_USER = 510;
    /**
     * Código utilizado por {@link UriMatcher} para la tabla de amigos
     */
    public static final int CODE_FRIEND = 600;
    /**
     * Código utilizado por {@link UriMatcher} para las tablas de amigos y de esos usuarios
     */
    public static final int CODE_FRIEND_WITH_USER = 610;
    /**
     * Código utilizado por {@link UriMatcher} para la tabla de participaciones en partidos
     */
    public static final int CODE_EVENTS_PARTICIPATION = 700;
    /**
     * Código utilizado por {@link UriMatcher} para las tablas de participaciones y de los usuarios
     */
    public static final int CODE_EVENTS_PARTICIPATION_WITH_USER = 710;
    /**
     * Código utilizado por {@link UriMatcher} para las tablas de participaciones y de los partidos
     */
    public static final int CODE_EVENTS_PARTICIPATION_WITH_EVENT = 720;
    /**
     * Código utilizado por {@link UriMatcher} para la tabla de invitaciones
     */
    public static final int CODE_EVENT_INVITATIONS = 800;
    /**
     * Código utilizado por {@link UriMatcher} para las tablas de invitaciones y de usuarios
     */
    public static final int CODE_EVENT_INVITATIONS_WITH_USER = 810;
    /**
     * Código utilizado por {@link UriMatcher} para las tablas de invitaciones y de partidos
     */
    public static final int CODE_EVENT_INVITATIONS_WITH_EVENT = 820;
    /**
     * Código utilizado por {@link UriMatcher} para la tabla de peticiones de participación
     */
    public static final int CODE_EVENTS_REQUESTS = 900;
    /**
     * Código utilizado por {@link UriMatcher} para las tablas de peticiones de participación y de
     * usuarios
     */
    public static final int CODE_EVENTS_REQUESTS_WITH_USER = 910;
    /**
     * Código utilizado por {@link UriMatcher} para las tablas de peticiones de participación y de
     * partidos
     */
    public static final int CODE_EVENTS_REQUESTS_WITH_EVENT = 920;

    // Codes for complex SQL sentences with multiple JOIN
    /**
     * Código utilizado por {@link UriMatcher} para la consulta de partidos del usuario sin
     * relación con uno de los amigos del usuario.
     */
    public static final int CODE_EVENTS_WITHOUT_RELATION_WITH_FRIEND = 1010;
    /**
     * Código utilizado por {@link UriMatcher} para la consulta de amigos del usuarios sin relación
     * con uno de los partidos del usuario.
     */
    public static final int CODE_FRIENDS_WITHOUT_RELATION_WITH_EVENT = 1020;
    /**
     * Código utilizado por {@link UriMatcher} para la consulta de partidos de la ciudad sin
     * relación con el usuario.
     */
    public static final int CODE_CITY_EVENTS_WITHOUT_RELATION_WITH_ME = 1030;
    /**
     * Código utilizado por {@link UriMatcher} para la consulta de partidos de un deporte específico
     * y de la ciudad sin relación con el usuario.
     */
    public static final int CODE_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME = 1040;
    /**
     * Código utilizado por {@link UriMatcher} para la consulta de usuarios de la ciudad sin
     * relación con el usuario.
     */
    public static final int CODE_NOT_FRIENDS_USERS_FROM_CITY = 1050;
    /**
     * Código utilizado por {@link UriMatcher} para la consulta de usuarios con un nombre específico
     * y de la ciudad sin relación con el usuario.
     */
    public static final int CODE_NOT_FRIENDS_USERS_WITH_NAME = 1060;
    /**
     * Código utilizado por {@link UriMatcher} para la consulta de partidos en los que participa el
     * usuario o creados por él.
     */
    public static final int CODE_MY_EVENTS_AND_PARTICIPATION = 1070;

    /**
     * UriMatcher utilizado para convertir la URI de los accesos a este Proveedor de Contenido en
     * códigos numéricos con los que clasificar dichos accesos.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    /**
     * Referencia a la base de datos.
     */
    private SportteamDBHelper mOpenHelper;

    /**
     * Construye el {@link #sUriMatcher} y establece las correspondencias entre las URI y los
     * códigos declarados.
     *
     * @return una nueva instancia de UriMatcher con las correspondencias establecidas
     */
    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        // This URI is content://com.usal.jorgeav.sportapp/users/
        matcher.addURI(authority, PATH_EMAIL_LOGGED, CODE_EMAIL_LOGGED);
        // This URI is content://com.usal.jorgeav.sportapp/users/
        matcher.addURI(authority, PATH_USERS, CODE_USERS);
        // This URI is content://com.usal.jorgeav.sportapp/userSport/
        matcher.addURI(authority, PATH_USER_SPORT, CODE_USER_SPORT);
        // This URI is content://com.usal.jorgeav.sportapp/fields/
        matcher.addURI(authority, PATH_FIELDS, CODE_FIELDS);
        // This URI is content://com.usal.jorgeav.sportapp/fieldSport/
        matcher.addURI(authority, PATH_FIELD_SPORT, CODE_FIELD_SPORT);
        // This URI is content://com.usal.jorgeav.sportapp/fields_fieldSport/
        matcher.addURI(authority, PATH_FIELDS_WITH_FIELD_SPORT, CODE_FIELD_WITH_FIELD_SPORT);
        // This URI is content://com.usal.jorgeav.sportapp/events/
        matcher.addURI(authority, PATH_EVENTS, CODE_EVENTS);
        // This URI is content://com.usal.jorgeav.sportapp/eventSimulatedParticipant/
        matcher.addURI(authority, PATH_EVENT_SIMULATED_PARTICIPANT, CODE_SIMULATED_PARTICIPANTS);
        // This URI is content://com.usal.jorgeav.sportapp/events/
        matcher.addURI(authority, PATH_ALARMS, CODE_ALARMS);
        // This URI is content://com.usal.jorgeav.sportapp/friendRequests/
        matcher.addURI(authority, PATH_FRIENDS_REQUESTS, CODE_FRIEND_REQUEST);
        // This URI is content://com.usal.jorgeav.sportapp/friendRequests_user/
        matcher.addURI(authority, PATH_FRIENDS_REQUESTS_WITH_USER, CODE_FRIEND_REQUEST_WITH_USER);
        // This URI is content://com.usal.jorgeav.sportapp/friends/
        matcher.addURI(authority, PATH_FRIENDS, CODE_FRIEND);
        // This URI is content://com.usal.jorgeav.sportapp/friends_user/
        matcher.addURI(authority, PATH_FRIENDS_WITH_USER, CODE_FRIEND_WITH_USER);
        // This URI is content://com.usal.jorgeav.sportapp/eventsParticipation/
        matcher.addURI(authority, PATH_EVENTS_PARTICIPATION, CODE_EVENTS_PARTICIPATION);
        // This URI is content://com.usal.jorgeav.sportapp/eventsParticipation_user/
        matcher.addURI(authority, PATH_EVENTS_PARTICIPATION_WITH_USER, CODE_EVENTS_PARTICIPATION_WITH_USER);
        // This URI is content://com.usal.jorgeav.sportapp/eventsParticipation_event/
        matcher.addURI(authority, PATH_EVENTS_PARTICIPATION_WITH_EVENT, CODE_EVENTS_PARTICIPATION_WITH_EVENT);
        // This URI is content://com.usal.jorgeav.sportapp/eventInvitations/
        matcher.addURI(authority, PATH_EVENT_INVITATIONS, CODE_EVENT_INVITATIONS);
        // This URI is content://com.usal.jorgeav.sportapp/eventInvitations_user/
        matcher.addURI(authority, PATH_EVENT_INVITATIONS_WITH_USER, CODE_EVENT_INVITATIONS_WITH_USER);
        // This URI is content://com.usal.jorgeav.sportapp/eventInvitations_event/
        matcher.addURI(authority, PATH_EVENT_INVITATIONS_WITH_EVENT, CODE_EVENT_INVITATIONS_WITH_EVENT);
        // This URI is content://com.usal.jorgeav.sportapp/eventRequests/
        matcher.addURI(authority, PATH_EVENTS_REQUESTS, CODE_EVENTS_REQUESTS);
        // This URI is content://com.usal.jorgeav.sportapp/eventRequests_user/
        matcher.addURI(authority, PATH_EVENTS_REQUESTS_WITH_USER, CODE_EVENTS_REQUESTS_WITH_USER);
        // This URI is content://com.usal.jorgeav.sportapp/eventRequests_event/
        matcher.addURI(authority, PATH_EVENTS_REQUESTS_WITH_EVENT, CODE_EVENTS_REQUESTS_WITH_EVENT);

        // This URI is content://com.usal.jorgeav.sportapp/myEventAndParticipation/
        matcher.addURI(authority, PATH_MY_EVENTS_AND_PARTICIPATION, CODE_MY_EVENTS_AND_PARTICIPATION);
        // This URI is content://com.usal.jorgeav.sportapp/myEvent_friendUser/
        matcher.addURI(authority, PATH_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND, CODE_EVENTS_WITHOUT_RELATION_WITH_FRIEND);
        // This URI is content://com.usal.jorgeav.sportapp/friendsUser_myEvent/
        matcher.addURI(authority, PATH_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS, CODE_FRIENDS_WITHOUT_RELATION_WITH_EVENT);
        // This URI is content://com.usal.jorgeav.sportapp/cityEvent_myUser/
        matcher.addURI(authority, PATH_CITY_EVENTS_WITHOUT_RELATION_WITH_ME, CODE_CITY_EVENTS_WITHOUT_RELATION_WITH_ME);
        // This URI is content://com.usal.jorgeav.sportapp/notFriendsCity_users/
        matcher.addURI(authority, PATH_NOT_FRIENDS_USERS_FROM_CITY, CODE_NOT_FRIENDS_USERS_FROM_CITY);
        // This URI is content://com.usal.jorgeav.sportapp/notFriendsName_users/
        matcher.addURI(authority, PATH_NOT_FRIENDS_USERS_WITH_NAME, CODE_NOT_FRIENDS_USERS_WITH_NAME);

        return matcher;
    }

    /**
     * Invocado al iniciar la aplicación, obtiene una instancia de {@link SportteamDBHelper}
     *
     * @return true
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new SportteamDBHelper(getContext());
        return true;
    }

    /**
     * Invocado para realizar inserciones múltiples en la base de datos. Utiliza
     * {@link #sUriMatcher} para determinar la tabla objetivo. La operación se realiza por medio de
     * {@link #bulkInsert(ContentValues[], SQLiteDatabase, String)}. Al finalizar, notifica las
     * URIs afectadas por los cambios.
     *
     * @param uri    dirección de la tabla a la que se van insertar los datos
     * @param values array de valores que deben insertarse
     * @return número de tuplas insertadas
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        Context context = getContext();
        if (context == null) return 0;

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsInserted;
        switch (sUriMatcher.match(uri)) {
            case CODE_EVENTS:
                rowsInserted = bulkInsert(values, db, TABLE_EVENT);
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_MY_EVENTS_AND_PARTICIPATION_URI, null);
                    getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI, null);
                }
                return rowsInserted;
            case CODE_FIELDS:
                rowsInserted = bulkInsert(values, db, TABLE_FIELD);
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().getContentResolver().notifyChange(FieldEntry.CONTENT_FIELDS_WITH_FIELD_SPORT_URI, null);
                }
                return rowsInserted;
            case CODE_FIELD_SPORT:
                rowsInserted = bulkInsert(values, db, TABLE_FIELD_SPORTS);
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().getContentResolver().notifyChange(FieldEntry.CONTENT_FIELDS_WITH_FIELD_SPORT_URI, null);
                }
                return rowsInserted;
            case CODE_USER_SPORT:
                rowsInserted = bulkInsert(values, db, TABLE_USER_SPORTS);
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            case CODE_SIMULATED_PARTICIPANTS:
                rowsInserted = bulkInsert(values, db, TABLE_EVENT_SIMULATED_PARTICIPANT);
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            case CODE_FRIEND_REQUEST:
                rowsInserted = bulkInsert(values, db, TABLE_FRIENDS_REQUESTS);
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().getContentResolver().notifyChange(FriendRequestEntry.CONTENT_FRIEND_REQUESTS_WITH_USER_URI, null);
                }
                return rowsInserted;
            case CODE_FRIEND:
                rowsInserted = bulkInsert(values, db, TABLE_FRIENDS);
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().getContentResolver().notifyChange(FriendsEntry.CONTENT_FRIEND_WITH_USER_URI, null);
                    getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI, null);
                }
                return rowsInserted;
            case CODE_EVENTS_PARTICIPATION:
                rowsInserted = bulkInsert(values, db, TABLE_EVENTS_PARTICIPATION);
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_MY_EVENTS_AND_PARTICIPATION_URI, null);
                    getContext().getContentResolver().notifyChange(EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_WITH_USER_URI, null);
                    getContext().getContentResolver().notifyChange(EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_WITH_EVENT_URI, null);
                }
                return rowsInserted;
            case CODE_EVENT_INVITATIONS:
                rowsInserted = bulkInsert(values, db, TABLE_EVENT_INVITATIONS);
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().getContentResolver().notifyChange(EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_WITH_USER_URI, null);
                    getContext().getContentResolver().notifyChange(EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_WITH_EVENT_URI, null);
                }
                return rowsInserted;
            case CODE_EVENTS_REQUESTS:
                rowsInserted = bulkInsert(values, db, TABLE_EVENTS_REQUESTS);
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().getContentResolver().notifyChange(EventRequestsEntry.CONTENT_EVENTS_REQUESTS_WITH_USER_URI, null);
                    getContext().getContentResolver().notifyChange(EventRequestsEntry.CONTENT_EVENTS_REQUESTS_WITH_EVENT_URI, null);
                }
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    /**
     * Realiza la inserción múltiple. Realmente inicia una transacción sobre la base de datos en la
     * que va insertando una a una cada tupla, luego finaliza la transacción de manera que todas
     * las inserciones se realizan en conjunto de forma atómica.
     *
     * @param values    array de valores que deben insertarse.
     * @param db        base de datos
     * @param tableName nombre de la tabla donde se realiza la inserción
     * @return número de tuplas insertadas
     */
    private int bulkInsert(@NonNull ContentValues[] values, SQLiteDatabase db, String tableName) {
        int rowsInserted = 0;
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tableName, null, value);
                if (_id != -1) rowsInserted++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return rowsInserted;
    }

    /**
     * Invocado para realizar eliminaciones en la base de datos. Utiliza {@link #sUriMatcher} para
     * determinar la tabla objetivo. Al finalizar, notifica las URIs afectadas por los cambios.
     *
     * @param uri           dirección de la tabla de la que se van a borrar datos
     * @param selection     cláusula de selección de tuplas WHERE
     * @param selectionArgs parámetros para completar la cláusula WHERE
     * @return número de tuplas eliminadas
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Context context = getContext();
        if (context == null) return 0;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case CODE_USER_SPORT:
                count = db.delete(TABLE_USER_SPORTS, selection, selectionArgs);
                break;
            case CODE_FIELDS:
                count = db.delete(TABLE_FIELD, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(FieldEntry.CONTENT_FIELDS_WITH_FIELD_SPORT_URI, null);
                break;
            case CODE_FIELD_SPORT:
                count = db.delete(TABLE_FIELD_SPORTS, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(FieldEntry.CONTENT_FIELDS_WITH_FIELD_SPORT_URI, null);
                break;
            case CODE_EVENTS:
                count = db.delete(TABLE_EVENT, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_MY_EVENTS_AND_PARTICIPATION_URI, null);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI, null);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI, null);
                break;
            case CODE_SIMULATED_PARTICIPANTS:
                count = db.delete(TABLE_EVENT_SIMULATED_PARTICIPANT, selection, selectionArgs);
                break;
            case CODE_ALARMS:
                count = db.delete(TABLE_ALARM, selection, selectionArgs);
                break;
            case CODE_FRIEND_REQUEST:
                count = db.delete(TABLE_FRIENDS_REQUESTS, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(UserEntry.CONTENT_USER_RELATION_USER_URI, null);
                getContext().getContentResolver().notifyChange(FriendRequestEntry.CONTENT_FRIEND_REQUESTS_WITH_USER_URI, null);
                break;
            case CODE_FRIEND:
                count = db.delete(TABLE_FRIENDS, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(UserEntry.CONTENT_USER_RELATION_USER_URI, null);
                getContext().getContentResolver().notifyChange(FriendsEntry.CONTENT_FRIEND_WITH_USER_URI, null);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI, null);
                break;
            case CODE_EVENTS_PARTICIPATION:
                count = db.delete(TABLE_EVENTS_PARTICIPATION, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(UserEntry.CONTENT_USER_RELATION_EVENT_URI, null);
                getContext().getContentResolver().notifyChange(EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_WITH_USER_URI, null);
                getContext().getContentResolver().notifyChange(EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_WITH_EVENT_URI, null);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_MY_EVENTS_AND_PARTICIPATION_URI, null);
                break;
            case CODE_EVENT_INVITATIONS:
                count = db.delete(TABLE_EVENT_INVITATIONS, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(UserEntry.CONTENT_USER_RELATION_EVENT_URI, null);
                getContext().getContentResolver().notifyChange(EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_WITH_USER_URI, null);
                getContext().getContentResolver().notifyChange(EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_WITH_EVENT_URI, null);
                break;
            case CODE_EVENTS_REQUESTS:
                count = db.delete(TABLE_EVENTS_REQUESTS, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(UserEntry.CONTENT_USER_RELATION_EVENT_URI, null);
                getContext().getContentResolver().notifyChange(EventRequestsEntry.CONTENT_EVENTS_REQUESTS_WITH_USER_URI, null);
                getContext().getContentResolver().notifyChange(EventRequestsEntry.CONTENT_EVENTS_REQUESTS_WITH_EVENT_URI, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * Método no implementado
     *
     * @param uri dirección de la tabla
     * @return null
     */
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Invocado para realizar inserciones en la base de datos. Utiliza {@link #sUriMatcher} para
     * determinar la tabla objetivo. Al finalizar, notifica las URIs afectadas por los cambios.
     *
     * @param uri    dirección de la tabla en la que se van a insertar datos
     * @param values conjunto de datos
     * @return dirección de la tupla recién insertada dentro de la tabla
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Context context = getContext();
        if (context == null) return null;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri = null;
        long _id;
        switch (sUriMatcher.match(uri)) {
            case CODE_EMAIL_LOGGED:
                _id = db.insert(TABLE_EMAIL_LOGGED, null, values);
                if (_id > 0) returnUri = EmailLoggedEntry.buildUserUriWith(_id);
                break;
            case CODE_USERS:
                _id = db.insert(TABLE_USER, null, values);
                if (_id > 0) returnUri = UserEntry.buildUserUriWith(_id);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_NOT_FRIENDS_USERS_FROM_CITY_URI, null);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_NOT_FRIENDS_USERS_WITH_NAME_URI, null);
                break;
            case CODE_EVENTS:
                _id = db.insert(TABLE_EVENT, null, values);
                if (_id > 0) returnUri = EventEntry.buildEventUriWith(_id);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI, null);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_MY_EVENTS_AND_PARTICIPATION_URI, null);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI, null);
                break;
            case CODE_SIMULATED_PARTICIPANTS:
                _id = db.insert(TABLE_EVENT_SIMULATED_PARTICIPANT, null, values);
                if (_id > 0)
                    returnUri = SimulatedParticipantEntry.buildSimulatedParticipantUriWith(_id);
                break;
            case CODE_FIELDS:
                _id = db.insert(TABLE_FIELD, null, values);
                if (_id > 0) returnUri = FieldEntry.buildFieldUriWith(_id);
                getContext().getContentResolver().notifyChange(FieldEntry.CONTENT_FIELDS_WITH_FIELD_SPORT_URI, null);
                break;
            case CODE_ALARMS:
                _id = db.insert(TABLE_ALARM, null, values);
                if (_id > 0) returnUri = AlarmEntry.buildAlarmUriWith(_id);
                break;
            case CODE_FRIEND_REQUEST:
                _id = db.insert(TABLE_FRIENDS_REQUESTS, null, values);
                if (_id > 0) returnUri = FriendRequestEntry.buildFriendRequestsUriWith(_id);
                getContext().getContentResolver().notifyChange(UserEntry.CONTENT_USER_RELATION_USER_URI, null);
                getContext().getContentResolver().notifyChange(FriendRequestEntry.CONTENT_FRIEND_REQUESTS_WITH_USER_URI, null);
                break;
            case CODE_FRIEND:
                _id = db.insert(TABLE_FRIENDS, null, values);
                if (_id > 0) returnUri = FriendsEntry.buildFriendsUriWith(_id);
                getContext().getContentResolver().notifyChange(UserEntry.CONTENT_USER_RELATION_USER_URI, null);
                getContext().getContentResolver().notifyChange(FriendsEntry.CONTENT_FRIEND_WITH_USER_URI, null);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI, null);
                break;
            case CODE_EVENTS_PARTICIPATION:
                _id = db.insert(TABLE_EVENTS_PARTICIPATION, null, values);
                if (_id > 0)
                    returnUri = EventsParticipationEntry.buildEventsParticipationUriWith(_id);
                getContext().getContentResolver().notifyChange(UserEntry.CONTENT_USER_RELATION_EVENT_URI, null);
                getContext().getContentResolver().notifyChange(EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_WITH_USER_URI, null);
                getContext().getContentResolver().notifyChange(EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_WITH_EVENT_URI, null);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_MY_EVENTS_AND_PARTICIPATION_URI, null);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI, null);
                break;
            case CODE_EVENT_INVITATIONS:
                _id = db.insert(TABLE_EVENT_INVITATIONS, null, values);
                if (_id > 0) returnUri = EventsInvitationEntry.buildEventInvitationUriWith(_id);
                getContext().getContentResolver().notifyChange(UserEntry.CONTENT_USER_RELATION_EVENT_URI, null);
                getContext().getContentResolver().notifyChange(EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_WITH_USER_URI, null);
                getContext().getContentResolver().notifyChange(EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_WITH_EVENT_URI, null);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI, null);
                break;
            case CODE_EVENTS_REQUESTS:
                _id = db.insert(TABLE_EVENTS_REQUESTS, null, values);
                if (_id > 0) returnUri = EventRequestsEntry.buildEventRequestsUriWith(_id);
                getContext().getContentResolver().notifyChange(UserEntry.CONTENT_USER_RELATION_EVENT_URI, null);
                getContext().getContentResolver().notifyChange(EventRequestsEntry.CONTENT_EVENTS_REQUESTS_WITH_USER_URI, null);
                getContext().getContentResolver().notifyChange(EventRequestsEntry.CONTENT_EVENTS_REQUESTS_WITH_EVENT_URI, null);
                getContext().getContentResolver().notifyChange(JoinQueryEntries.CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    /**
     * Invocado para realizar consultas a la base de datos. Utiliza {@link #sUriMatcher} para
     * determinar la tabla objetivo.
     *
     * @param uri           dirección de la tabla sobre la que realizar la consulta
     * @param projection    array de nombres de las columnas que se consultan
     * @param selection     cláusula de selección de tuplas WHERE
     * @param selectionArgs parámetros para completar la cláusula WHERE
     * @param sortOrder     orden bajo el que organizar el resultado de la consulta
     * @return un {@link Cursor} con los resultados de la consulta
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Context context = getContext();
        if (context == null) return null;
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_EMAIL_LOGGED:
                cursor = mOpenHelper.getReadableDatabase().query(TABLE_EMAIL_LOGGED,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_USERS:
                cursor = mOpenHelper.getReadableDatabase().query(TABLE_USER,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_FIELDS:
                cursor = mOpenHelper.getReadableDatabase().query(TABLE_FIELD,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_EVENTS:
                cursor = mOpenHelper.getReadableDatabase().query(TABLE_EVENT,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_ALARMS:
                cursor = mOpenHelper.getReadableDatabase().query(TABLE_ALARM,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_USER_SPORT:
                cursor = mOpenHelper.getReadableDatabase().query(TABLE_USER_SPORTS,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_FIELD_SPORT:
                cursor = mOpenHelper.getReadableDatabase().query(TABLE_FIELD_SPORTS,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_FIELD_WITH_FIELD_SPORT:
                builder.setTables(FieldEntry.TABLES_FIELD_JOIN_FIELD_SPORT);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_SIMULATED_PARTICIPANTS:
                cursor = mOpenHelper.getReadableDatabase().query(TABLE_EVENT_SIMULATED_PARTICIPANT,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_FRIEND_REQUEST:
                cursor = mOpenHelper.getReadableDatabase().query(TABLE_FRIENDS_REQUESTS,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_FRIEND_REQUEST_WITH_USER:
                builder.setTables(FriendRequestEntry.TABLES_FRIENDS_REQUESTS_JOIN_USER);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_FRIEND:
                cursor = mOpenHelper.getReadableDatabase().query(TABLE_FRIENDS,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_FRIEND_WITH_USER:
                builder.setTables(FriendsEntry.TABLES_FRIENDS_JOIN_USER);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_EVENTS_PARTICIPATION:
                cursor = mOpenHelper.getReadableDatabase().query(TABLE_EVENTS_PARTICIPATION,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_EVENTS_PARTICIPATION_WITH_USER:
                builder.setTables(EventsParticipationEntry.TABLES_EVENTS_PARTICIPATION_JOIN_USER);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_EVENTS_PARTICIPATION_WITH_EVENT:
                builder.setTables(EventsParticipationEntry.TABLES_EVENTS_PARTICIPATION_JOIN_EVENT);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_EVENT_INVITATIONS:
                cursor = mOpenHelper.getReadableDatabase().query(TABLE_EVENT_INVITATIONS,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_EVENT_INVITATIONS_WITH_USER:
                builder.setTables(EventsInvitationEntry.TABLES_EVENT_INVITATIONS_JOIN_USER);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_EVENT_INVITATIONS_WITH_EVENT:
                builder.setTables(EventsInvitationEntry.TABLES_EVENT_INVITATIONS_JOIN_EVENT);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_EVENTS_REQUESTS:
                cursor = mOpenHelper.getReadableDatabase().query(TABLE_EVENTS_REQUESTS,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_EVENTS_REQUESTS_WITH_USER:
                builder.setTables(EventRequestsEntry.TABLES_EVENTS_REQUESTS_JOIN_USER);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_EVENTS_REQUESTS_WITH_EVENT:
                builder.setTables(EventRequestsEntry.TABLES_EVENTS_REQUESTS_JOIN_EVENT);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_MY_EVENTS_AND_PARTICIPATION:
                builder.setTables(JoinQueryEntries.TABLES_EVENTS_JOIN_PARTICIPATION);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_EVENTS_WITHOUT_RELATION_WITH_FRIEND:
                builder.setTables(JoinQueryEntries.TABLES_EVENTS_JOIN_PARTICIPATION_P_JOIN_INVITATIONS_JOIN_REQUESTS);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_FRIENDS_WITHOUT_RELATION_WITH_EVENT:
                builder.setTables(JoinQueryEntries.TABLES_USERS_JOIN_FRIENDS_JOIN_PARTICIPATION_JOIN_INVITATIONS_JOIN_REQUESTS);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_CITY_EVENTS_WITHOUT_RELATION_WITH_ME:
                builder.setTables(JoinQueryEntries.TABLES_EVENTS_JOIN_PARTICIPATION_JOIN_INVITATIONS_JOIN_REQUESTS);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME:
                builder.setTables(JoinQueryEntries.TABLES_EVENTS_JOIN_PARTICIPATION_JOIN_INVITATIONS_JOIN_REQUESTS);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_NOT_FRIENDS_USERS_FROM_CITY:
                builder.setTables(JoinQueryEntries.TABLES_USERS_JOIN_FRIENDS);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_NOT_FRIENDS_USERS_WITH_NAME:
                builder.setTables(JoinQueryEntries.TABLES_USERS_JOIN_FRIENDS);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Invocado para realizar actualizaciones en la base de datos. Sólo se realizan actualizaciones
     * en la tabla de emails de usuarios que han iniciado sesión con éxito, para el resto de tablas
     * no es necesario implementar este método.
     * <p>
     * Esto se debe al esquema de la base de datos ({@link SportteamDBHelper#onCreate(SQLiteDatabase)})
     * en el que esta tabla ignora inserciones de emails que ya estén dentro de ella, mientras el
     * resto de tablas reemplazan las tuplas cuando se pretende insertar alguna con una clave que ya
     * existe.
     *
     * @param uri           dirección de la tabla en la que se pretenden actualizar datos
     * @param values        datos nuevos con los que se pretende actualizar
     * @param selection     cláusula de selección de tuplas WHERE
     * @param selectionArgs parámetros para completar la cláusula WHERE
     * @return número de tuplas actualizadas
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Context context = getContext();
        if (context == null) return 0;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case CODE_EMAIL_LOGGED:
                count = db.update(TABLE_EMAIL_LOGGED, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not implemented");
        }
        context.getContentResolver().notifyChange(uri, null);
        return count;
    }
}
