package com.usal.jorgeav.sportapp.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.mainactivities.AlarmsActivity;
import com.usal.jorgeav.sportapp.mainactivities.EventsActivity;
import com.usal.jorgeav.sportapp.mainactivities.FriendsActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Clase con métodos auxiliares, invocados desde varios puntos de la aplicación, que proveen de
 * funcionalidad útil para crear notificaciones a partir de un objeto {@link MyNotification} y
 * mostrarlas en la barra de notificaciones del dispositivo.
 */
public class UtilesNotification {
    /**
     * Nombre de la clase
     */
    public static final String TAG = UtilesNotification.class.getSimpleName();

    /**
     * Nombre para el canal de notificaciones. Se usa el mismo que el del paquete de la aplicación
     * porque sólo es necesario establecer un canal. Debe ser único.
     */
    public static final String NOTIFICATION_CHANNEL_ID = "com.usal.jorgeav.sportapp";

    /**
     * Modificador que, aplicado a una variable, le permite adquirir como valor solamente el
     * siguiente conjunto de constantes que representan los distintos tipos de notificación que
     * pueden darse en el sistema.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NOTIFICATION_ID_ERROR,
            NOTIFICATION_ID_FRIEND_REQUEST_RECEIVED, NOTIFICATION_ID_FRIEND_REQUEST_ACCEPTED,
            NOTIFICATION_ID_EVENT_INVITATION_RECEIVED, NOTIFICATION_ID_EVENT_INVITATION_ACCEPTED,
            NOTIFICATION_ID_EVENT_INVITATION_DECLINED, NOTIFICATION_ID_EVENT_REQUEST_RECEIVED,
            NOTIFICATION_ID_EVENT_REQUEST_ACCEPTED, NOTIFICATION_ID_EVENT_REQUEST_DECLINED,
            NOTIFICATION_ID_EVENT_COMPLETE, NOTIFICATION_ID_EVENT_SOMEONE_QUIT,
            NOTIFICATION_ID_EVENT_EDIT, NOTIFICATION_ID_EVENT_DELETE, NOTIFICATION_ID_ALARM_EVENT,
            NOTIFICATION_ID_EVENT_CREATE, NOTIFICATION_ID_EVENT_EXPELLED})
    public @interface NotificationType {
    }

    /**
     * Error
     */
    public static final int NOTIFICATION_ID_ERROR = 0;
    /**
     * Petición de amistad recibida
     */
    public static final int NOTIFICATION_ID_FRIEND_REQUEST_RECEIVED = 1;
    /**
     * Petición de amistad aceptada
     */
    public static final int NOTIFICATION_ID_FRIEND_REQUEST_ACCEPTED = 2;
    /**
     * Invitación a partido recibida
     */
    public static final int NOTIFICATION_ID_EVENT_INVITATION_RECEIVED = 3;
    /**
     * Invitación a partido aceptada
     */
    public static final int NOTIFICATION_ID_EVENT_INVITATION_ACCEPTED = 4;
    /**
     * Invitación a partido rechazada
     */
    public static final int NOTIFICATION_ID_EVENT_INVITATION_DECLINED = 5;
    /**
     * Petición de participación a partido recibida
     */
    public static final int NOTIFICATION_ID_EVENT_REQUEST_RECEIVED = 6;
    /**
     * Petición de participación a partido aceptada
     */
    public static final int NOTIFICATION_ID_EVENT_REQUEST_ACCEPTED = 7;
    /**
     * Petición de participación a partido rechazada
     */
    public static final int NOTIFICATION_ID_EVENT_REQUEST_DECLINED = 8;
    /**
     * Partido se ha completado
     */
    public static final int NOTIFICATION_ID_EVENT_COMPLETE = 9;
    /**
     * El partido ya no está completo
     */
    public static final int NOTIFICATION_ID_EVENT_SOMEONE_QUIT = 10;
    /**
     * El partido ha sido editado
     */
    public static final int NOTIFICATION_ID_EVENT_EDIT = 11;
    /**
     * El partido ha sido borrado
     */
    public static final int NOTIFICATION_ID_EVENT_DELETE = 12;
    /**
     * Un partido coincide con una de las alarmas
     */
    public static final int NOTIFICATION_ID_ALARM_EVENT = 13;
    /**
     * Un amigo ha creado un partido nuevo
     */
    public static final int NOTIFICATION_ID_EVENT_CREATE = 14;
    /**
     * Has sido expulsado de un partido
     */
    public static final int NOTIFICATION_ID_EVENT_EXPELLED = 15;

    /**
     * Borra las notificaciones mostradas en la barra de notificaciones relativas a esta aplicación
     *
     * @param context contexto bajo el que se ejecuta esta acción
     */
    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.cancelAll();
    }

    /**
     * Crea y muestra una notificación con la información de un usuario
     *
     * @param context       contexto bajo el que se ejecuta esta acción
     * @param fNotification objeto del que extraer los datos para mostrar la notificación
     * @param user          usuario relativo a la información de la notificación
     */
    public static void createNotification(Context context, MyNotification fNotification, User user) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white)  /* Notification icons must be entirely white */
                    .setContentTitle(fNotification.getTitle())
                    .setContentText(fNotification.getMessage())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getMessage()))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(contentProfileIntent(context, user.getUid()))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null)
                notificationManager.notify(fNotification.getNotification_type(), notificationBuilder.build());
        }
    }

    /**
     * Crea y devuelve un {@link PendingIntent} con el que se abrirá esta aplicación, concretamente
     * {@link FriendsActivity} con la vista de detalles del usuario indicado.
     *
     * @param context contexto bajo el que se ejecuta esta acción
     * @param userId  identificador del usuario que se pretende mostrar
     * @return {@link PendingIntent} para insertarlo en la notificación que debe mostrar el usuario
     */
    private static PendingIntent contentProfileIntent(Context context, String userId) {
        Intent startActivityIntent = Intent.makeRestartActivityTask(new ComponentName(context, FriendsActivity.class));
        startActivityIntent.putExtra(FriendsActivity.USER_ID_PENDING_INTENT_EXTRA, userId);
        return PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(), /* To ensure every PendingIntent is unique */
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Crea y muestra una notificación con la información de un partido
     *
     * @param context       contexto bajo el que se ejecuta esta acción
     * @param fNotification objeto del que extraer los datos para mostrar la notificación
     * @param event         partido relativo a la información de la notificación
     */
    public static void createNotification(Context context, MyNotification fNotification, Event event) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white)  /* Notification icons must be entirely white */
                    .setContentTitle(fNotification.getTitle())
                    .setContentText(fNotification.getMessage())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getMessage()))
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(contentDetailEventIntent(context, event.getEvent_id()))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null)
                notificationManager.notify(fNotification.getNotification_type(), notificationBuilder.build());
        }
    }

    /**
     * Crea y devuelve un {@link PendingIntent} con el que se abrirá esta aplicación, concretamente
     * {@link EventsActivity} con la vista de detalles del partido indicado.
     * <p>
     * También utilizado para abrir la aplicación desde los partidos mostrados en el widget.
     *
     * @param context contexto bajo el que se ejecuta esta acción
     * @param eventId identificador del partido que se pretende mostrar
     * @return {@link PendingIntent} para insertarlo en la notificación que debe mostrar el partido
     * @see com.usal.jorgeav.sportapp.widget.EventsAppWidget
     */
    private static PendingIntent contentDetailEventIntent(Context context, String eventId) {
        Intent startActivityIntent = Intent.makeRestartActivityTask(new ComponentName(context, EventsActivity.class));
        startActivityIntent.putExtra(EventsActivity.EVENT_ID_PENDING_INTENT_EXTRA, eventId);
        return PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(), /* To ensure every PendingIntent is unique */
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Crea y muestra una notificación con la información de una alarma
     *
     * @param context       contexto bajo el que se ejecuta esta acción
     * @param fNotification objeto del que extraer los datos para mostrar la notificación
     * @param alarm         alarma relativa a la información de la notificación
     */
    public static void createNotification(Context context, MyNotification fNotification, Alarm alarm) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white)  /* Notification icons must be entirely white */
                    .setContentTitle(fNotification.getTitle())
                    .setContentText(fNotification.getMessage())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getMessage()))
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(contentAlarmIntent(context, alarm.getId()))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null)
                notificationManager.notify(fNotification.getNotification_type(), notificationBuilder.build());
        }
    }

    /**
     * Crea y devuelve un {@link PendingIntent} con el que se abrirá esta aplicación, concretamente
     * {@link AlarmsActivity} con la vista de detalles de la alarma indicada.
     *
     * @param context contexto bajo el que se ejecuta esta acción
     * @param alarmId identificador de la alarma que se pretende mostrar
     * @return {@link PendingIntent} para insertarlo en la notificación que debe mostrar la alarma
     */
    private static PendingIntent contentAlarmIntent(Context context, String alarmId) {
        Intent startActivityIntent = Intent.makeRestartActivityTask(new ComponentName(context, AlarmsActivity.class));
        startActivityIntent.putExtra(AlarmsActivity.ALARM_ID_PENDING_INTENT_EXTRA, alarmId);
        return PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(), /* To ensure every PendingIntent is unique */
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Crea y muestra una notificación sin información adicional
     *
     * @param context       contexto bajo el que se ejecuta esta acción
     * @param fNotification objeto del que extraer los datos para mostrar la notificación
     */
    public static void createNotification(Context context, MyNotification fNotification) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white) /* Notification icons must be entirely white */
                    .setContentTitle(fNotification.getTitle())
                    .setContentText(fNotification.getMessage())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getMessage()))
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(contentEventIntent(context))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null)
                notificationManager.notify(fNotification.getNotification_type(), notificationBuilder.build());
        }
    }

    /**
     * Crea y devuelve un {@link PendingIntent} con el que se abrirá esta aplicación, concretamente
     * {@link EventsActivity} con la vista del calendario.
     * <p>
     * También utilizado para abrir la aplicación desde la cabecera del widget.
     *
     * @param context contexto bajo el que se ejecuta esta acción
     * @return {@link PendingIntent} para insertarlo en la notificación que debe mostrar el calendario
     * @see com.usal.jorgeav.sportapp.widget.EventsAppWidget
     */
    public static PendingIntent contentEventIntent(Context context) {
        Intent startActivityIntent = Intent.makeRestartActivityTask(new ComponentName(context, EventsActivity.class));
        return PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(), /* To ensure every PendingIntent is unique */
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Obtiene el recurso de cadena de texto correspondiente al título de una notificación
     * dependiendo del tipo de notificación indicado.
     *
     * @param type tipo de notificación
     * @return recurso de cadena de texto del título de la notificación
     */
    public static int parseNotificationTypeToTitle(@NotificationType int type) {
        switch (type) {
            case NOTIFICATION_ID_ALARM_EVENT:
                return R.string.notification_title_alarm_event;
            case NOTIFICATION_ID_EVENT_COMPLETE:
                return R.string.notification_title_event_complete;
            case NOTIFICATION_ID_EVENT_CREATE:
                return R.string.notification_title_event_create;
            case NOTIFICATION_ID_EVENT_DELETE:
                return R.string.notification_title_event_delete;
            case NOTIFICATION_ID_EVENT_EDIT:
                return R.string.notification_title_event_edit;
            case NOTIFICATION_ID_EVENT_EXPELLED:
                return R.string.notification_title_event_expelled;
            case NOTIFICATION_ID_EVENT_INVITATION_ACCEPTED:
                return R.string.notification_title_event_invitation_accepted;
            case NOTIFICATION_ID_EVENT_INVITATION_DECLINED:
                return R.string.notification_title_event_invitation_declined;
            case NOTIFICATION_ID_EVENT_INVITATION_RECEIVED:
                return R.string.notification_title_event_invitation_received;
            case NOTIFICATION_ID_EVENT_REQUEST_ACCEPTED:
                return R.string.notification_title_event_request_accepted;
            case NOTIFICATION_ID_EVENT_REQUEST_DECLINED:
                return R.string.notification_title_event_request_declined;
            case NOTIFICATION_ID_EVENT_REQUEST_RECEIVED:
                return R.string.notification_title_event_request_received;
            case NOTIFICATION_ID_EVENT_SOMEONE_QUIT:
                return R.string.notification_title_event_someone_quit;
            case NOTIFICATION_ID_FRIEND_REQUEST_ACCEPTED:
                return R.string.notification_title_friend_request_accepted;
            case NOTIFICATION_ID_FRIEND_REQUEST_RECEIVED:
                return R.string.notification_title_friend_request_received;
            default: case NOTIFICATION_ID_ERROR:
                return -1;
        }
    }

    /**
     * Obtiene el recurso de cadena de texto correspondiente al mensaje de una notificación
     * dependiendo del tipo de notificación indicado.
     *
     * @param type tipo de notificación
     * @return recurso de cadena de texto del mensaje de la notificación
     */
    public static int parseNotificationTypeToMessage(@NotificationType int type) {
        switch (type) {
            case NOTIFICATION_ID_ALARM_EVENT:
                return R.string.notification_msg_alarm_event;
            case NOTIFICATION_ID_EVENT_COMPLETE:
                return R.string.notification_msg_event_complete;
            case NOTIFICATION_ID_EVENT_CREATE:
                return R.string.notification_msg_event_create;
            case NOTIFICATION_ID_EVENT_DELETE:
                return R.string.notification_msg_event_delete;
            case NOTIFICATION_ID_EVENT_EDIT:
                return R.string.notification_msg_event_edit;
            case NOTIFICATION_ID_EVENT_EXPELLED:
                return R.string.notification_msg_event_expelled;
            case NOTIFICATION_ID_EVENT_INVITATION_ACCEPTED:
                return R.string.notification_msg_event_invitation_accepted;
            case NOTIFICATION_ID_EVENT_INVITATION_DECLINED:
                return R.string.notification_msg_event_invitation_declined;
            case NOTIFICATION_ID_EVENT_INVITATION_RECEIVED:
                return R.string.notification_msg_event_invitation_received;
            case NOTIFICATION_ID_EVENT_REQUEST_ACCEPTED:
                return R.string.notification_msg_event_request_accepted;
            case NOTIFICATION_ID_EVENT_REQUEST_DECLINED:
                return R.string.notification_msg_event_request_declined;
            case NOTIFICATION_ID_EVENT_REQUEST_RECEIVED:
                return R.string.notification_msg_event_request_received;
            case NOTIFICATION_ID_EVENT_SOMEONE_QUIT:
                return R.string.notification_msg_event_someone_quit;
            case NOTIFICATION_ID_FRIEND_REQUEST_ACCEPTED:
                return R.string.notification_msg_friend_request_accepted;
            case NOTIFICATION_ID_FRIEND_REQUEST_RECEIVED:
                return R.string.notification_msg_friend_request_received;
            default: case NOTIFICATION_ID_ERROR:
                return -1;
        }
    }
}
