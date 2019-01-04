package com.usal.jorgeav.sportapp.network.firebase;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.usal.jorgeav.sportapp.network.firebase.actions.UserFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;

/**
 * Clase necesaria para implementar la funcionalidad proporcionada por Firebase Cloud Messaging.
 * Esta clase es un Servicio que permanece activo a la escucha de cambios en el token identificativo
 * de la instancia de la aplicación que actúa como cliente del servicio de mensajes de Firebase.
 * Este token es una combinación alfanumérica única que permite a los servidores de Firebase
 * identificar el dispositivo y enviarle mensajes a través de Firebase Cloud Messaging. Los mensajes
 * serán procesados en la clase {@link MyFirebaseMessagingService}
 *
 * @see <a href= "https://firebase.google.com/docs/cloud-messaging/android/client">
 * Firebase Cloud Messaging for Android</a>
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/iid/FirebaseInstanceIdService">
 * FirebaseInstanceIdService</a>
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    /**
     * Nombre de la clase.
     */
    private static final String TAG = "MyFirebaseInstIDService";

    /**
     * Este método es invocado cada vez que el token cambia. Recupera el identificador del usuario
     * cuya sesión está iniciada y almacena el token y ese identificador de usuario en la base de
     * datos de Firebase. De esta forma, pueda ser consultado en el momento de enviar un mensaje
     * desde el servidor a este dispositivo.
     */
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Refreshed token: " + refreshedToken);

        String myUserID = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUserID))
            UserFirebaseActions.updateUserToken(myUserID, refreshedToken);
    }
}
