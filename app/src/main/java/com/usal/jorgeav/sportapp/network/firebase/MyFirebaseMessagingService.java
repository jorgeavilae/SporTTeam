package com.usal.jorgeav.sportapp.network.firebase;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.actions.UserFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.AlarmsFirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.sync.EventsFirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.sync.UsersFirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import java.util.Map;

/**
 * Clase necesaria para implementar la funcionalidad proporcionada por Firebase Cloud Messaging.
 * Esta clase es un Servicio que permanece activo a la espera de mensajes enviados desde los
 * servidores de Firebase. Cuando esto ocurre, invoca el callback
 * {@link #onMessageReceived(RemoteMessage)} en el que se procesa el mensaje y se actúa en
 * consecuencia.
 * <p>
 * En concreto, en esta aplicación es usada para recibir los datos de una notificación recién
 * insertada en la rama correspondiente al usuario cuya sesión está iniciada. Este mensaje ha sido
 * dirigido a este dispositivo por haber guardado el token de esta aplicación cliente.
 *
 * @see <a href= "https://firebase.google.com/docs/cloud-messaging/android/client">
 * Firebase Cloud Messaging for Android</a>
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/messaging/FirebaseMessagingService">
 * FirebaseMessagingService</a>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(@NonNull String token) {
        Log.i(TAG, "Refreshed token: " + token);

        String myUserID = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUserID))
            UserFirebaseActions.updateUserToken(myUserID, token);
    }

    /**
     * Invocado al recibir un mensaje. Ejecuta {@link #notify(Map)} para mostrar el mensaje
     * recibido como una notificación en la barra de notificaciones.
     *
     * @param remoteMessage mensaje recibido del servidor
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0)
            notify(remoteMessage.getData());
    }

    /**
     * Muestra la notificación en la barra de notificaciones del dispositivo. Si la notificación
     * viene con datos relativos a un usuario o partido, se asegura de que estén presentes en el
     * Proveedor de Contenido. A continuación, crea y muestra la notificación con ayuda de
     * {@link UtilesNotification#createNotification(Context, MyNotification)}
     *
     * @param dataMap mapa de pares clave valor con los datos incorporados al mensaje del servidor.
     */
    private void notify(Map<String, String> dataMap) {
        MyNotification notification = parseNotificationFromMap(dataMap);
        String notificationID = "";
        if (dataMap.containsKey("notificationID"))
            notificationID = dataMap.get("notificationID");
        if (TextUtils.isEmpty(notificationID)) return;

        @FirebaseDBContract.NotificationDataTypes int type = notification.getData_type();
        switch (type) {
            case FirebaseDBContract.NOTIFICATION_TYPE_NONE:
                UtilesNotification.createNotification(MyApplication.getAppContext(), notification);
                break;
            case FirebaseDBContract.NOTIFICATION_TYPE_USER:
                User user = UtilesContentProvider.getUserFromContentProvider(notification.getExtra_data_one());
                if (user == null) {
                    UsersFirebaseSync.loadAProfileAndNotify(notificationID, notification);
                    return;
                }
                UtilesNotification.createNotification(MyApplication.getAppContext(), notification, user);
                break;
            case FirebaseDBContract.NOTIFICATION_TYPE_EVENT:
                Event event = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_one());
                if (event == null) {
                    EventsFirebaseSync.loadAnEventAndNotify(notificationID, notification);
                    return;
                }
                UtilesNotification.createNotification(MyApplication.getAppContext(), notification, event);
                break;
            case FirebaseDBContract.NOTIFICATION_TYPE_ALARM:
                Alarm alarm = UtilesContentProvider.getAlarmFromContentProvider(notification.getExtra_data_one());
                Event eventCoincidence = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_two());
                if (alarm == null || eventCoincidence == null) {
                    AlarmsFirebaseSync.loadAnAlarmAndNotify(notificationID, notification);
                    return;
                }
                UtilesNotification.createNotification(MyApplication.getAppContext(), notification, alarm);
                break;
            case FirebaseDBContract.NOTIFICATION_TYPE_ERROR:
                break;
        }
        NotificationsFirebaseActions.checkNotification(notificationID);
    }


    /**
     * Convierte el mapa de pares clave valor proporcionado en un objeto {@link MyNotification}.
     *
     * @param data mapa de pares clave valor con los datos incorporados al mensaje del servidor.
     * @return objeto {@link MyNotification} construido a partir de los datos del mapa
     */
    private MyNotification parseNotificationFromMap(Map<String, String> data) {
    /*
       data: {
           notificationID: notificationID,
           title: title,
           message: message,
           notification_type: notification_type,
           checked: checked,
           data_type: data_type,
           extra_data_one: extra_data_one,
           date: date
       }
     */
        Long notification_type = null;
        if (data.containsKey(FirebaseDBContract.Notification.NOTIFICATION_TYPE))
            notification_type = Long.parseLong(data.get(FirebaseDBContract.Notification.NOTIFICATION_TYPE));

        Boolean checked = null;
        if (data.containsKey(FirebaseDBContract.Notification.CHECKED))
            checked = Boolean.parseBoolean(data.get(FirebaseDBContract.Notification.CHECKED));

        String title = null;
        if (notification_type != null) {
            int strRes = UtilesNotification.parseNotificationTypeToTitle(notification_type.intValue());
            if (strRes != -1) title = getResources().getString(strRes);
        }
        if (title == null) /* If notification_type doesn't determine title */
            if (data.containsKey(FirebaseDBContract.Notification.TITLE))
                title = data.get(FirebaseDBContract.Notification.TITLE);

        String message = null;
        if (notification_type != null) {
            int strRes = UtilesNotification.parseNotificationTypeToMessage(notification_type.intValue());
            if (strRes != -1) message = getResources().getString(strRes);
        }
        if (message == null) /* If notification_type doesn't determine title */
            if (data.containsKey(FirebaseDBContract.Notification.MESSAGE))
                message = data.get(FirebaseDBContract.Notification.MESSAGE);

        String extra_data_one = null;
        if (data.containsKey(FirebaseDBContract.Notification.EXTRA_DATA_ONE))
            extra_data_one = data.get(FirebaseDBContract.Notification.EXTRA_DATA_ONE);

        String extra_data_two = null;
        if (data.containsKey(FirebaseDBContract.Notification.EXTRA_DATA_TWO))
            extra_data_two = data.get(FirebaseDBContract.Notification.EXTRA_DATA_TWO);

        Long data_type = null;
        if (data.containsKey(FirebaseDBContract.Notification.DATA_TYPE))
            data_type = Long.parseLong(data.get(FirebaseDBContract.Notification.DATA_TYPE));

        Long date = null;
        if (data.containsKey(FirebaseDBContract.Notification.DATE))
            date = Long.parseLong(data.get(FirebaseDBContract.Notification.DATE));

        return new MyNotification(notification_type, checked, title, message,
                extra_data_one, extra_data_two, data_type, date);
    }
}
