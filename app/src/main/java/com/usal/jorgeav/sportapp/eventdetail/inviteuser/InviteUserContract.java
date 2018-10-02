package com.usal.jorgeav.sportapp.eventdetail.inviteuser;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar la colección de
 * usuarios a los que enviar una invitación.
 */
abstract class InviteUserContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para iniciar el proceso de carga de la base de datos de los usuarios que pueden
         * recibir una invitación a este partido
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param bundle        contenedor de posibles parámetros utilizados en la consulta
         */
        void loadFriends(LoaderManager loaderManager, Bundle bundle);

        /**
         * Invocado para enviar una invitación para el evento indicado al usuario seleccionado.
         *
         * @param eventId identificador del evento al que se invita
         * @param uid     identificador del usuario que recibirá la invitación
         */
        void sendInvitationToThisEvent(String eventId, String uid);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Invocado para mostrar los usuarios a los que enviar invitaciones contenidas en el
         * {@link Cursor}. Serán todos los amigos del usuario actual menos los que ya tengan alguna
         * relación con el partido.
         *
         * @param cursor usuarios obtenidos en la consulta
         */
        void showFriends(Cursor cursor);

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();
    }
}
