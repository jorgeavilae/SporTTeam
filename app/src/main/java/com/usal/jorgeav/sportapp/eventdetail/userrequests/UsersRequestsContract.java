package com.usal.jorgeav.sportapp.eventdetail.userrequests;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.usal.jorgeav.sportapp.BaseFragment;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar la colección de
 * usuarios que han enviado una petición de amistad y de usuarios rechazados.
 */
public abstract class UsersRequestsContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para iniciar el proceso de carga de la base de datos de los usuarios que
         * han enviado una petición de participación al partido
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void loadUsersRequests(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para iniciar el proceso de carga de la base de datos de los usuarios que
         * enviaron una petición de participación y fue rechazada
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void loadUsersRejected(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para aceptar la petición de participación del usuario y añadirlo como
         * participante del partido.
         *
         * @param eventId identificador del partido
         * @param uid     identificador del usuario que va a ser aceptado como participante
         */
        void acceptUserRequestToThisEvent(String eventId, String uid);

        /**
         * Invocado para rechazar la petición de participación del usuario y añadirlo como
         * usuario bloqueado al partido.
         *
         * @param eventId identificador del partido
         * @param uid     identificador del usuario que va a ser rechazado como participante
         */
        void declineUserRequestToThisEvent(String eventId, String uid);

        /**
         * Invocado para desbloquear a un usuario bloqueado cuya petición de participación fue
         * rechazada.
         *
         * @param eventId identificador del partido
         * @param uid     identificador del usuario que va a ser aceptado como participante
         */
        void unblockUserParticipationRejectedToThisEvent(String eventId, String uid);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Invocado para mostrar los usuarios que enviaron peticiones de participación al partido
         * contenidos en el {@link Cursor}
         *
         * @param cursor usuarios obtenidos en la consulta
         */
        void showUsersRequests(Cursor cursor);

        /**
         * Invocado para mostrar que enviaron una petición de participación y fue rechazada
         * contenidos en el {@link Cursor}
         *
         * @param cursor usuarios obtenidos en la consulta
         */
        void showRejectedUsers(Cursor cursor);

        /**
         * Invocado para mostrar en la interfaz algún mensaje. Debe asegurar que, aunque la llamada
         * se produzca desde otro hilo, la operación sobre la interfaz para mostrar el mensaje se
         * ejecute desde el hilo principal.
         *
         * @param msgResource identificador del recurso de texto correspondiente al mensaje que se
         *                    quiere mostrar
         */
        void showMsgFromBackgroundThread(int msgResource);

        /**
         * Invocado para obtener una referencia al {@link BaseFragment} que implementa este método
         *
         * @return BaseFragment que implementa este método
         */
        BaseFragment getThis();

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();
    }
}
