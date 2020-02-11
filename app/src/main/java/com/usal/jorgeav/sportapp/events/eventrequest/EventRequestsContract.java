package com.usal.jorgeav.sportapp.events.eventrequest;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar la colección de
 * peticiones de participación sin respuesta enviadas por el usuario actual.
 */
abstract class EventRequestsContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para iniciar el proceso de consulta a la base de datos de los partidos a los que
         * el usuario actual envió una petición de participación que todavía no tiene respuesta
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void loadEventRequests(LoaderManager loaderManager, Bundle b);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Muestra las peticiones de participación enviadas por el usuario actual contenidas en el
         * {@link Cursor}
         *
         * @param cursor peticiones de participación obtenidas en la consulta
         */
        void showEventRequests(Cursor cursor);

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();
    }
}
