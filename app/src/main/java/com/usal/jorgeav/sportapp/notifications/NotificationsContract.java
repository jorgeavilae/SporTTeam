package com.usal.jorgeav.sportapp.notifications;

import android.content.Context;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.data.MyNotification;

import java.util.LinkedHashMap;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar la colección de
 * notificaciones recibidas por el usuario actual.
 */
abstract class NotificationsContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para iniciar el proceso de consulta a la base de datos de las notificaciones
         * recibidas por el usuario actual.
         */
        void loadNotifications();

        /**
         * Invocado para borrar una notificación de las recibidas por el usuario.
         *
         * @param key identificador de la notificación que se desea borrar
         */
        void deleteNotification(String key);

        /**
         * Invocado para borrar todas las notificaciones recibidas por el usuario.
         */
        void deleteAllNotifications();
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Invocado para mostrar las notificaciones obtenidas en la consulta
         *
         * @param notifications notificaciones obtenidas en la consulta. La clave es el identificador
         *                      de la notificación, el valor es el objeto notificación {@link MyNotification}
         */
        void showNotifications(LinkedHashMap<String, MyNotification> notifications);

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();

        /**
         * Invocado para obtener una referencia al {@link BaseFragment} que implementa este método
         *
         * @return BaseFragment que implementa este método
         */
        BaseFragment getThis();
    }
}
