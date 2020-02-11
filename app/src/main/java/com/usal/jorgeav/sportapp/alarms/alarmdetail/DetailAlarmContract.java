package com.usal.jorgeav.sportapp.alarms.alarmdetail;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.data.Field;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador,
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar los detalles de alarmas.
 */
abstract class DetailAlarmContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Inicia el proceso de carga de la alarma de la base de datos
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void openAlarm(LoaderManager loaderManager, Bundle b);

        /**
         * Inicia el proceso de borrado de la alarma de la base de datos cuyos detalles se están
         * mostrando
         *
         * @param b contenedor de posibles parámetros utilizados en el borrado
         */
        void deleteAlarm(Bundle b);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Invocado para mostrar el deporte en la interfaz
         *
         * @param sport identificador del deporte
         */
        void showAlarmSport(String sport);

        /**
         * Invocado para mostrar el lugar de la alarma en la interfaz
         *
         * @param field  instalación
         * @param city   ciudad
         * @param coords coordenadas del lugar
         */
        void showAlarmPlace(Field field, String city, LatLng coords);

        /**
         * Invocado para mostrar en la interfaz el rango de fechas en las que está establecida
         * la alarma
         *
         * @param dateFrom limite inferior del rango de fechas
         * @param dateTo   limite superior del rango de fechas
         */
        void showAlarmDate(Long dateFrom, Long dateTo);

        /**
         * Invocado para mostrar en la interfaz el rango de puestos totales establecidos para la
         * alarma
         *
         * @param totalPlayersFrom limite inferior del rango de puestos totales
         * @param totalPlayersTo   limite superior del rango de puestos totales
         */
        void showAlarmTotalPlayers(Long totalPlayersFrom, Long totalPlayersTo);

        /**
         * Invocado para mostrar en la interfaz el rango de puestos vacantes establecidos para la
         * alarma
         *
         * @param emptyPlayersFrom limite inferior del rango de puestos vacantes
         * @param emptyPlayersTo   limite superior del rango de puestos vacantes
         */
        void showAlarmEmptyPlayers(Long emptyPlayersFrom, Long emptyPlayersTo);

        /**
         * Invocado para mostrar en la interfaz los partidos encontrados que coinciden con la
         * alarma
         *
         * @param data conjunto de partidos encontrados en la base de datos
         */
        void showEvents(Cursor data);

        /**
         * Invocado para limpiar los elementos de la interfaz de los datos especificados
         */
        void clearUI();

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();

        /**
         * Invocado para obtener una referencia al {@link BaseFragment} que implementa este método
         * *
         *
         * @return BaseFragment que implementa este método
         */
        BaseFragment getThis();
    }
}
