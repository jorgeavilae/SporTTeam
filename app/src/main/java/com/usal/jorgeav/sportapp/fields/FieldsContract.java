package com.usal.jorgeav.sportapp.fields;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar la colección de
 * instalaciones de la ciudad.
 */
abstract class FieldsContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para iniciar el proceso de consulta a la base de datos de las instalaciones de
         * la ciudad
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void loadNearbyFields(LoaderManager loaderManager, Bundle b);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Invocado para mostrar las instalaciones contenidas en el {@link Cursor} y emplazarlas en
         * el mapa
         *
         * @param cursor instalaciones obtenidas en la consulta
         */
        void showFields(Cursor cursor);

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();
    }
}
