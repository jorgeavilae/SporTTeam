package com.usal.jorgeav.sportapp.fields.fielddetail;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador,
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar los detalles de las
 * instalaciones.
 */
abstract class DetailFieldContract {

    public interface Presenter {
        /**
         * Inicia el proceso de consulta a la base de datos de la instalación
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void openField(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para enviar un voto para una de las pistas de la instalación
         *
         * @param fieldId identificador de la instalación
         * @param sportId identificador de la pista
         * @param rating  puntuación del voto
         */
        boolean voteSportInField(String fieldId, String sportId, float rating);

        /**
         * Invocado cuando el creador de la instalación desea eliminarla de la base de datos
         *
         * @param fieldId identificador de la instalación
         */
        void deleteField(String fieldId);
    }

    public interface View {
        /**
         * Invocado para mostrar en la interfaz el nombre de la instalación
         *
         * @param name nombre de la instalación
         */
        void showFieldName(String name);

        /**
         * Invocado para mostrar en la interfaz la dirección de la instalación
         *
         * @param address dirección postal de la instalación
         * @param city    ciudad de la instalación
         * @param coords  coordenadas de la instalación
         */
        void showFieldPlace(String address, String city, LatLng coords);

        /**
         * Invocado para mostrar en la interfaz las horas de apertura y cierre de la instalación
         *
         * @param openTime  hora de apertura de la instalación en milisegundos
         * @param closeTime hora de cierre de la instalación en milisegundos
         */
        void showFieldTimes(long openTime, long closeTime);

        /**
         * Invocado para mostrar en la interfaz el usuario creador de la instalación
         *
         * @param creator identificador del usuario creador de la instalación
         */
        void showFieldCreator(String creator);

        /**
         * Invocado para mostrar en la interfaz las pistas de la instalación y sus puntuaciones
         *
         * @param cursor contiene la lista de pistas con sus puntuaciones y cantidad de votos
         */
        void showSportCourts(Cursor cursor);

        /**
         * Invocado para limpiar la interfaz de los datos de la instalación
         */
        void clearUI();

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();
    }

}
