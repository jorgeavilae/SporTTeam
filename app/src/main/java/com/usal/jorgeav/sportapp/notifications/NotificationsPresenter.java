package com.usal.jorgeav.sportapp.notifications;

import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.mainactivities.LoginActivity;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.AlarmsFirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.sync.EventsFirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.sync.UsersFirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Presentador utilizado para mostrar la colección de notificaciones recibidas por el usuario actual.
 * Aquí se inicia la consulta, esta vez directamente al servidor de Firebase, para obtener las
 * notificaciones recibidas por este usuario y que serán enviadas a la Vista
 * {@link NotificationsContract.View}.
 * <p>
 * La consulta se hace directamente al servidor por medio de
 * {@link FirebaseSync#loadMyNotifications(ValueEventListener)}. Esto es debido a que las
 * notificaciones no se almacenan en el Proveedor de Contenido.
 * <p>
 * Implementa la interfaz {@link NotificationsContract.Presenter} para la comunicación con esta
 * clase.
 */
class NotificationsPresenter implements
        NotificationsContract.Presenter {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = NotificationsPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private NotificationsContract.View mView;

    /**
     * Constructor
     *
     * @param view Vista correspondiente a este Presentador
     */
    NotificationsPresenter(NotificationsContract.View view) {
        this.mView = view;
    }

    /**
     * Inicia el proceso de consulta a la base de datos del servidor de las notificaciones recibidas
     * por el usuario actual. Utiliza {@link FirebaseSync#loadMyNotifications(ValueEventListener)}
     * para establecer el comportamiento una vez realizada la consulta a Firebase.
     * <p>
     * Dentro del método {@link ValueEventListener#onDataChange(DataSnapshot)}, se crea un
     * {@link LinkedHashMap} para almacenar la notificación y su identificador, por orden
     * cronológico. Luego se van añadiendo una a una asegurándose que el objeto al que refieren
     * (usuario, partido o alarma) se encuentra en el Proveedor de Contenido, cargándolos del
     * servidor en caso contrario.
     *
     * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
     * FirebaseDatabase</a>
     * @see UsersFirebaseSync#loadAProfile(LoginActivity, String, boolean)
     * @see EventsFirebaseSync#loadAnEvent(String)
     * @see AlarmsFirebaseSync#loadAnAlarm(String)
     */
    @Override
    public void loadNotifications() {
        FirebaseSync.loadMyNotifications(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinkedHashMap<String, MyNotification> result = new LinkedHashMap<>();
                if (dataSnapshot.exists()) {
                    // Populate a list of notifications
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        MyNotification notification = data.getValue(MyNotification.class);
                        if (notification == null) return;

                        // Make sure data in notification is loaded in Content Provider
                        @FirebaseDBContract.NotificationDataTypes int type = notification.getData_type();
                        switch (type) {
                            case FirebaseDBContract.NOTIFICATION_TYPE_NONE:
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_USER:
                                User user = UtilesContentProvider.getUserFromContentProvider(notification.getExtra_data_one());
                                if (user == null) {
                                    UsersFirebaseSync.loadAProfile(null, notification.getExtra_data_one(), false);
                                }
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_EVENT:
                                Event event = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_one());
                                if (event == null) {
                                    EventsFirebaseSync.loadAnEvent(notification.getExtra_data_one());
                                }
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_ALARM:
                                Alarm alarm = UtilesContentProvider.getAlarmFromContentProvider(notification.getExtra_data_one());
                                Event eventCoincidence = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_two());
                                if (alarm == null) {
                                    AlarmsFirebaseSync.loadAnAlarm(notification.getExtra_data_one());
                                }
                                if (eventCoincidence == null) {
                                    EventsFirebaseSync.loadAnEvent(notification.getExtra_data_two());
                                }
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_ERROR:
                                break;
                        }
                    }
                }
                // Show notifications
                mView.showNotifications(reverseLinkedHashMap(result));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mView.getActivityContext(), R.string.toast_notification_load_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Invierte el orden del {@link LinkedHashMap} de notificaciones para mostrar las más recientes
     * primero.
     *
     * @param map conjunto de notificaciones.
     * @return el conjunto de notificaciones ordenadas en el sentido inverso al que se recibieron.
     */
    private LinkedHashMap<String, MyNotification> reverseLinkedHashMap(LinkedHashMap<String, MyNotification> map) {
        LinkedHashMap<String, MyNotification> result = new LinkedHashMap<>(map.size());

        List<Map.Entry<String, MyNotification>> list = new ArrayList<>(map.entrySet());

        for (int i = list.size() - 1; i >= 0; i--) {
            Map.Entry<String, MyNotification> entry = list.get(i);
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * Borra una notificación de las recibidas por el usuario.
     *
     * @param key identificador de la notificación que se desea borrar
     * @see NotificationsFirebaseActions#deleteNotification(String, String)
     */
    @Override
    public void deleteNotification(String key) {
        String myUserID = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUserID) && key != null && !TextUtils.isEmpty(key))
            NotificationsFirebaseActions.deleteNotification(myUserID, key);
    }

    /**
     * Borra todas las notificaciones recibidas por el usuario.
     *
     * @see NotificationsFirebaseActions#deleteAllNotifications(String)
     */
    @Override
    public void deleteAllNotifications() {
        String myUserID = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUserID))
            NotificationsFirebaseActions.deleteAllNotifications(myUserID);

        // Reload
        loadNotifications();
    }
}