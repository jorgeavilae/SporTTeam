package com.usal.jorgeav.sportapp.profile.sendinvitation;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar la colección de
 * partidos para los que el usuario actual puede enviar una invitación al usuario mostrado.
 */
abstract class SendInvitationContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para iniciar el proceso de consulta a la base de datos sobre los partidos para
         * los que el usuario actual puede enviar una invitación al usuario mostrado.
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void loadEventsForInvitation(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para enviar una invitación al usuario mostrado para el partido seleccionado.
         *
         * @param eventId identificador del partido seleccionado
         * @param uid     identificador del usuario mostrado que recibirá la invitación
         */
        void sendInvitationToThisUser(String eventId, String uid);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Invocado para mostrar los partidos para los que el usuario actual puede enviar invitaciones,
         * contenidos en el {@link Cursor}. Serán todos los partidos en los que el usuario actual
         * participe menos los que ya tengan alguna relación con el usuario mostrado.
         *
         * @param cursor partidos obtenidos en la consulta
         */
        void showEventsForInvitation(Cursor cursor);

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();
    }
}
