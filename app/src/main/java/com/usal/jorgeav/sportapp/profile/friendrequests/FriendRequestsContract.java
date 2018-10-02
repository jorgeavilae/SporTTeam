package com.usal.jorgeav.sportapp.profile.friendrequests;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar la colección de
 * peticiones de amistad recibidas por el usuario actual.
 */
abstract class FriendRequestsContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para iniciar el proceso de consulta a la base de datos sobre las peticiones de
         * amistad recibidas por el usuario actual.
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void loadFriendRequests(LoaderManager loaderManager, Bundle b);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Invocado para mostrar los usuarios que han enviado una petición de amistad al usuario
         * actual y que están contenidos en el {@link Cursor}
         *
         * @param cursor usuarios obtenidos en la consulta
         */
        void showFriendRequests(Cursor cursor);

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();
    }
}
