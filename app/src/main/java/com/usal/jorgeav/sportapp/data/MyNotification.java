package com.usal.jorgeav.sportapp.data;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import java.util.HashMap;
import java.util.Map;

/**
 * Representación de una Notificación de las almacenadas en Firebase Realtime Database
 */
@SuppressWarnings("unused")
public class MyNotification {
    /**
     * Tipo de notificación, indica qué notifica
     * @see UtilesNotification.NotificationType
     */
    private Long notification_type;
    /**
     * Indica si la notificación ya fue mostrada en la barra de notificaciones
     */
    private Boolean checked;
    /**
     * Título de la notificación
     */
    private String title;
    /**
     * Mensaje de la notificación
     */
    private String message;
    /**
     * Identificador único del objeto que acompaña a la
     * notificación ({@link User}, {@link Event}, {@link Alarm})
     */
    private String extra_data_one;

    /**
     * Identificador único del segundo objeto que acompaña a la
     * notificación ({@link User}, {@link Event}, {@link Alarm})
     */
    private String extra_data_two;
    /**
     * Indica el tipo de dato almacenado en {@link #extra_data_one} y en {@link #extra_data_two}
     * @see FirebaseDBContract.NotificationDataTypes
     */
    private Long data_type;
    /**
     * Fecha y hora de creación de la notificación, en milisegundos
     */
    private Long date;

    /**
     * Constructor sin argumentos. Necesario para el parseo de este objeto desde Firebase
     * Realtime Database con
     * {@link
     * <a href= "https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/database/DataSnapshot.html#getValue(com.google.firebase.database.GenericTypeIndicator%3CT%3E)">
     *     DataSnapshot.getValue(Class)
     * </a>}
     */
    public MyNotification() {
        // Default constructor required for calls to DataSnapshot.getValue(MyNotification.class)
    }

    /**
     * Constructor con argumentos
     *
     * @param notification_type tipo de notificación
     * @param checked true si ha sido mostrada en la barra de notificacion, false en otro caso
     * @param title título de la notificación
     * @param message mensaje de la notificación
     * @param extra_data_one identificador del dato que acompaña la notificación
     * @param extra_data_two identificador del segundo dato que acompaña la notificación
     * @param data_type tipo de datos que acompaña la notificación
     * @param date fecha de creación de la notificación
     */
    public MyNotification(Long notification_type, Boolean checked, String title, String message,
                          String extra_data_one, String extra_data_two, Long data_type, Long date) {
        this.notification_type = notification_type;
        this.checked = checked;
        this.title = title;
        this.message = message;
        this.extra_data_one = extra_data_one;
        this.extra_data_two = extra_data_two;
        this.data_type = data_type;
        this.date = date;
    }

    @UtilesNotification.NotificationType
    public int getNotification_type() {
        return longToNotificationType(notification_type);
    }

    /**
     * Transforma el parámetro entero en su correspondiente tipo
     * de {@link UtilesNotification.NotificationType}
     *
     * @param type tipo en formato de número entero
     * @return tipo en formato {@link UtilesNotification.NotificationType}
     */
    @UtilesNotification.NotificationType
    private int longToNotificationType(Long type) {
        if (type == (long) UtilesNotification.NOTIFICATION_ID_FRIEND_REQUEST_RECEIVED)
            return UtilesNotification.NOTIFICATION_ID_FRIEND_REQUEST_RECEIVED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_FRIEND_REQUEST_ACCEPTED)
            return UtilesNotification.NOTIFICATION_ID_FRIEND_REQUEST_ACCEPTED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_RECEIVED)
            return UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_RECEIVED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_ACCEPTED)
            return UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_ACCEPTED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_DECLINED)
            return UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_DECLINED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_RECEIVED)
            return UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_RECEIVED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_ACCEPTED)
            return UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_ACCEPTED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_DECLINED)
            return UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_DECLINED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_COMPLETE)
            return UtilesNotification.NOTIFICATION_ID_EVENT_COMPLETE;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_SOMEONE_QUIT)
            return UtilesNotification.NOTIFICATION_ID_EVENT_SOMEONE_QUIT;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_EDIT)
            return UtilesNotification.NOTIFICATION_ID_EVENT_EDIT;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_DELETE)
            return UtilesNotification.NOTIFICATION_ID_EVENT_DELETE;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_ALARM_EVENT)
            return UtilesNotification.NOTIFICATION_ID_ALARM_EVENT;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_CREATE)
            return UtilesNotification.NOTIFICATION_ID_EVENT_CREATE;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_EXPELLED)
            return UtilesNotification.NOTIFICATION_ID_EVENT_EXPELLED;
        return UtilesNotification.NOTIFICATION_ID_ERROR;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getExtra_data_one() {
        return extra_data_one;
    }

    public String getExtra_data_two() {
        return extra_data_two;
    }

    @FirebaseDBContract.NotificationDataTypes
    public int getData_type() {
        return longToNotificationDataType(data_type);
    }

    /**
     * Transforma el parámetro entero en su correspondiente tipo
     * de {@link FirebaseDBContract.NotificationDataTypes}
     *
     * @param type tipo en formato de número entero
     * @return tipo en formato {@link FirebaseDBContract.NotificationDataTypes}
     */
    @FirebaseDBContract.NotificationDataTypes
    private int longToNotificationDataType(Long type) {
        if (type != null) {
            if (type == (long) FirebaseDBContract.NOTIFICATION_TYPE_NONE)
                return FirebaseDBContract.NOTIFICATION_TYPE_NONE;
            if (type == (long) FirebaseDBContract.NOTIFICATION_TYPE_USER)
                return FirebaseDBContract.NOTIFICATION_TYPE_USER;
            if (type == (long) FirebaseDBContract.NOTIFICATION_TYPE_EVENT)
                return FirebaseDBContract.NOTIFICATION_TYPE_EVENT;
            if (type == (long) FirebaseDBContract.NOTIFICATION_TYPE_ALARM)
                return FirebaseDBContract.NOTIFICATION_TYPE_ALARM;
        }
        return FirebaseDBContract.NOTIFICATION_TYPE_ERROR;
    }

    public Long getDate() {
        return date;
    }

    /**
     * Introduce los datos de este objeto en un Map para Firebase Realtime Database.
     * La clave se obtiene de la clase diccionario {@link FirebaseDBContract} y como valor se
     * utilizan las variables de la clase.
     *
     * @return Map con los datos de {@link MyNotification}
     */
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.Notification.NOTIFICATION_TYPE, notification_type);
        result.put(FirebaseDBContract.Notification.CHECKED, checked);
        result.put(FirebaseDBContract.Notification.TITLE, title);
        result.put(FirebaseDBContract.Notification.MESSAGE, message);
        result.put(FirebaseDBContract.Notification.EXTRA_DATA_ONE, extra_data_one);
        result.put(FirebaseDBContract.Notification.EXTRA_DATA_TWO, extra_data_two);
        result.put(FirebaseDBContract.Notification.DATA_TYPE, data_type);
        result.put(FirebaseDBContract.Notification.DATE, date);
        return result;
    }

    /**
     * Representación del objeto en cadena de texto
     * @return la cadena de texto con los datos del objeto
     */
    @Override
    public String toString() {
        return "MyNotification{" +
                "notification_type=" + notification_type +
                ", checked=" + checked +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", extra_data_one='" + extra_data_one + '\'' +
                ", extra_data_two='" + extra_data_two + '\'' +
                ", data_type=" + data_type +
                ", date=" + date +
                '}';
    }
}
