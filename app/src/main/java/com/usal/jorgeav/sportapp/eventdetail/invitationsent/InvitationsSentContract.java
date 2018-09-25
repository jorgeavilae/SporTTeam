package com.usal.jorgeav.sportapp.eventdetail.invitationsent;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar la colección de
 * invitaciones enviadas por el usuario actual.
 */
abstract class InvitationsSentContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para iniciar el proceso de carga de la base de datos de los usuarios que recibieron las
         * invitaciones enviadas por el usuario actual
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param bundle        contenedor de posibles parámetros utilizados en la consulta
         */
        void loadEventInvitationsSent(LoaderManager loaderManager, Bundle bundle);

        /**
         * Invocado para borrar una de las invitaciones de la base de datos que hayan sido enviadas
         * por el usuario actual.
         *
         * @param eventId identificador del evento al que hace referencia la invitación
         * @param uid     identificador del usuario que recibe la invitación
         */
        void deleteInvitationToThisEvent(String eventId, String uid);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Muestra las invitaciones enviadas por el usuario actual contenidas en el {@link Cursor}
         *
         * @param cursor invitaciones obtenidas en la consulta
         */
        void showEventInvitationsSent(Cursor cursor);

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();
    }
}
