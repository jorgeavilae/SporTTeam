package com.usal.jorgeav.sportapp.events;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.usal.jorgeav.sportapp.BaseFragment;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar la colección de partidos.
 */
abstract class EventsContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para iniciar el proceso de carga de los partidos de la base de datos
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void loadEvents(LoaderManager loaderManager, Bundle b);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Invocado para mostrar los partidos contenidos en el {@link Cursor} y emplazarlos en el
         * calendario
         *
         * @param cursor partidos obtenidos en la consulta
         */
        void showCalendarEvents(Cursor cursor);

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
