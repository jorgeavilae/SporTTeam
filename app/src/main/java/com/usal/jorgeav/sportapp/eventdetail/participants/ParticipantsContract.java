package com.usal.jorgeav.sportapp.eventdetail.participants;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar la colección de
 * participantes, usuarios normales y usuarios simulados.
 */
abstract class ParticipantsContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para iniciar el proceso de carga de la base de datos de los usuarios que
         * participan en el partido
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b contenedor de posibles parámetros utilizados en la consulta
         */
        void loadParticipants(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para iniciar el proceso de carga de la base de datos de los usuarios simulados
         * que participan en el partido
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b contenedor de posibles parámetros utilizados en la consulta
         */
        void loadSimulatedParticipants(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para borrar al usuario como participante del partido. Se incluye un parámetro
         * booleano para borrar (o no) a los usuarios simulados creados por ese usuario.
         *
         * @param userId identificador del usuario que va a ser eliminado como participante del
         *               partido
         * @param eventId identificador del partido
         * @param deleteSimulatedParticipant true si se quieren borrar también los usuarios
         *                                   simulados que <var>userId</var> haya podido añadir,
         *                                   false en otro caso
         */
        void quitEvent(String userId, String eventId, boolean deleteSimulatedParticipant);

        /**
         * Invocado para borrar un usuario simulado del partido.
         *
         * @param simulatedUserId identificador del usuario simulado que va a ser eliminado del
         *                        partido
         * @param eventId identificador del partido
         */
        void deleteSimulatedUser(String simulatedUserId, String eventId);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {

        /**
         * Invocado para mostrar los usuarios participantes del partido contenidos en el
         * {@link Cursor}
         *
         * @param cursor usuarios obtenidos en la consulta
         */
        void showParticipants(Cursor cursor);

        /**
         * Invocado para mostrar los usuarios simulados del partido contenidos en el {@link Cursor}
         *
         * @param cursor usuarios simulados obtenidos en la consulta
         */
        void showSimulatedParticipants(Cursor cursor);

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();
    }
}
