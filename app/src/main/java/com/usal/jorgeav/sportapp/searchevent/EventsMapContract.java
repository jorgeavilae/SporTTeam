package com.usal.jorgeav.sportapp.searchevent;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar en un mapa la colección
 * de partidos con los que el usuario actual no tiene relación y por tanto puede unirse.
 */
public abstract class EventsMapContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para iniciar el proceso de consulta a la base de datos, de los partidos con los
         * que el usuario actual no tiene relación
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void loadNearbyEvents(LoaderManager loaderManager, Bundle b);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Invocado para mostrar los partidos contenidos en el {@link Cursor} y emplazarlos en el
         * mapa
         *
         * @param cursor partidos obtenidos en la consulta
         */
        void showEvents(Cursor cursor);

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();
    }
}
