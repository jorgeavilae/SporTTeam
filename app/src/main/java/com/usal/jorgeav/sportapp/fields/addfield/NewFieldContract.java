package com.usal.jorgeav.sportapp.fields.addfield;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SportCourt;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador,
 * de la arquitectura utilizada Modelo - Vista - Presentador, para la creación y edición de
 * instalaciones.
 */
public abstract class NewFieldContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para iniciar el proceso de consulta de la instalación de la base de datos que se
         * quiere editar
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void openField(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para iniciar el proceso de consulta a la base de datos de las instalaciones de
         * la ciudad del usuario actual
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void loadNearbyFields(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para crear la instalación en la base de datos con los parámetros especificados
         *
         * @param id        identificador de la instalación o null si es una instalación nueva
         * @param name      nombre de la instalación
         * @param address   dirección de la instalación
         * @param coords    coordenadas de la instalación
         * @param city      ciudad de la instalación
         * @param openTime  hora de apertura de la instalación en formato HH:mm
         * @param closeTime hora de cierre de la instalación en formato HH:mm
         * @param creator   identificador del usuario actual que está creado/editando la instalación
         * @param sports    lista de pistas en las que se indica el deporte y la puntuación inicial
         */
        void addField(String id, String name, String address, LatLng coords, String city,
                      String openTime, String closeTime, String creator, List<SportCourt> sports);

        /**
         * Invocado para detener el proceso de consulta a la base de datos de la instalación que se
         * quiere editar
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para detener la consulta al
         *                      Proveedor de Contenido
         */
        void destroyOpenFieldLoader(LoaderManager loaderManager);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Invocado para mostrar en la interfaz la dirección de la instalación
         *
         * @param address dirección postal de la instalación
         * @param city    ciudad correspondiente a esa dirección
         * @param coords  coordenadas correspondientes a esa dirección
         */
        void showFieldPlace(String address, String city, LatLng coords);

        /**
         * Invocado para mostrar en la interfaz el nombre de la instalación
         *
         * @param name nombre de la instalación
         */
        void showFieldName(String name);

        /**
         * Invocado para mostrar en la interfaz las horas de apertura y cierre de la instalación
         *
         * @param openTime   hora de apertura en milisegundos
         * @param closeTimes hora de cierre en milisegundos
         */
        void showFieldTimes(long openTime, long closeTimes);

        /**
         * Invocado para mostrar en la interfaz el usuario creador de la instalación
         *
         * @param creator identificador del usuario creador de la instalación
         */
        void showFieldCreator(String creator);

        /**
         * Invocado para establecer en la Vista las pistas de la instalación y sus puntuaciones en
         * el momento de la edición.
         *
         * @param sports lista de {@link SportCourt} representando una pista, su puntuación, y sus
         *               votos
         */
        void setSportCourts(List<SportCourt> sports);

        /**
         * Invocado para restablecer el estado de la interfaz antes de producirse la consulta
         */
        void clearUI();

        /**
         * Invocado para indicarle a la Vista las instalaciones encontradas en la base de datos
         *
         * @param fieldList lista de instalaciones
         */
        void retrieveFields(ArrayList<Field> fieldList);

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
