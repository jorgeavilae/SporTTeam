package com.usal.jorgeav.sportapp.searchevent.advancedsearch;

import android.content.Context;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador,
 * de la arquitectura utilizada Modelo - Vista - Presentador, para el establecimiento de filtros
 * de búsqueda de partidos.
 */
abstract class SearchEventsContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para validar los parámetros de búsqueda establecidos en la Vista.
         *
         * @param dateFrom  limite inferior del período de fechas
         * @param dateTo    límite superior del período de fechas
         * @param totalFrom límite inferior del rango de puestos totales
         * @param totalTo   límite superior del rango de puestos totales
         * @param emptyFrom límite inferior del rango de puestos vacantes
         * @param emptyTo   límite superior del rango de puestos vacantes
         * @return true si todos los parámetros son válidos, false en caso contrario
         */
        boolean validateData(Long dateFrom, Long dateTo, int totalFrom, int totalTo,
                             int emptyFrom, int emptyTo);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();
    }
}
