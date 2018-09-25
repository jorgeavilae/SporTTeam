package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.usal.jorgeav.sportapp.data.Field;

import java.util.ArrayList;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador,
 * de la arquitectura utilizada Modelo - Vista - Presentador, para la creación y edición de alarmas.
 */
public abstract class NewAlarmContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Inicia el proceso de consulta de la alarma de la base de datos que se quiere editar
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void openAlarm(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para crear la alarma en la base de datos con los parámetros especificados
         *
         * @param alarmId   identificador de la alarma si se está editando o null si se está creando
         * @param sport     deporte de la alarma
         * @param field     instalación sobre la que escucha la alarma
         * @param city      ciudad sobre la que escucha la alarma
         * @param dateFrom  limite inferior del rango de fechas en las que la alarma está buscando
         * @param dateTo    límite superior del rango de fechas en las que la alarma está buscando
         * @param totalFrom límite inferior del rango de puestos totales de los partidos buscados
         * @param totalTo   límite superior del rango de puestos totales de los partidos buscados
         * @param emptyFrom límite inferior del rango de puestos vacantes de los partidos buscados
         * @param emptyTo   límite superior del rango de puestos vacantes de los partidos buscados
         */
        void addAlarm(String alarmId, String sport, String field, String city,
                      String dateFrom, String dateTo,
                      String totalFrom, String totalTo,
                      String emptyFrom, String emptyTo);

        /**
         * Inicia el proceso de consulta de instalaciones de la base de datos donde establecer la alarma
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param arguments     contenedor de posibles parámetros utilizados en la consulta
         */
        void loadFields(LoaderManager loaderManager, Bundle arguments);

        /**
         * Detiene el proceso de consulta de instalaciones de la base de datos
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para detener la consulta al
         *                      Proveedor de Contenido
         */
        void stopLoadFields(LoaderManager loaderManager);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Muestra el deporte en la interfaz
         *
         * @param sport identificador del deporte
         */
        void showAlarmSport(String sport);

        /**
         * Muestra la instalación en la interfaz
         *
         * @param fieldId identificador de la instalación
         * @param city    ciudad
         */
        void showAlarmField(String fieldId, String city);

        /**
         * Muestra el rango de fechas en la interfaz
         *
         * @param dateFrom limite inferior del rango de fechas
         * @param dateTo   limite superior del rango de fechas
         */
        void showAlarmDate(Long dateFrom, Long dateTo);

        /**
         * Muestra el rango de puestos totales en la interfaz
         *
         * @param totalPlayersFrom limite inferior del rango de puestos totales
         * @param totalPlayersTo   limite superior del rango de puestos totales
         */
        void showAlarmTotalPlayers(Long totalPlayersFrom, Long totalPlayersTo);

        /**
         * Muestra el rango de puestos vacantes en la interfaz
         *
         * @param emptyPlayersFrom limite inferior del rango de puestos vacantes
         * @param emptyPlayersTo   limite superior del rango de puestos vacantes
         */
        void showAlarmEmptyPlayers(Long emptyPlayersFrom, Long emptyPlayersTo);

        /**
         * Invocado para limpiar de la interfaz los datos de la alarma
         */
        void clearUI();

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();

        /**
         * Invocado para indicarle a la Vista las instalaciones encontradas en la base de datos
         *
         * @param dataList lista de instalaciones
         */
        void retrieveFields(ArrayList<Field> dataList);
    }
}
