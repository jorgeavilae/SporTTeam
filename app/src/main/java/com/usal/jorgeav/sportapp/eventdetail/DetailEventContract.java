package com.usal.jorgeav.sportapp.eventdetail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.Invitation;
import com.usal.jorgeav.sportapp.data.SimulatedUser;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador,
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar los detalles de partidos.
 */
public abstract class DetailEventContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para iniciar el proceso de carga del partido de la base de datos
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void openEvent(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para obtener la relación que el usuario actual mantiene con el partido que
         * se muestra
         */
        //todo debería aceptar un listener como parámetro donde se especificara loq hace con la relation
        void getRelationTypeBetweenThisEventAndI();

        /**
         * Invocado cuando el usuario quiere enviar una petición de participación al partido
         *
         * @param eventId identificador del partido
         */
        void sendEventRequest(String eventId);

        /**
         * Invocado cuando el usuario quiere cancelar una petición de participación enviada al
         * partido previamente
         *
         * @param eventId identificador del partido
         */
        void cancelEventRequest(String eventId);

        /**
         * Invocado cuando el usuario quiere aceptar la invitación al partido que ha recibido
         *
         * @param eventId identificador del partido
         * @param sender  identificador del usuario que envió la petición
         */
        void acceptEventInvitation(String eventId, String sender);

        /**
         * Invocado cuando el usuario quiere rechazar la invitación al partido que ha recibido
         *
         * @param eventId identificador del partido
         * @param sender  identificador del usuario que envió la petición
         */
        void declineEventInvitation(String eventId, String sender);

        /**
         * Invocado cuando el usuario ya no quiere asistir al partido
         *
         * @param eventId                     identificador del partido
         * @param deleteSimulatedParticipants true si se desea borrar también los usuarios simulados
         *                                    {@link SimulatedUser} añadidos por el usuario que ya
         *                                    no asistirá, false en otro caso
         */
        void quitEvent(String eventId, boolean deleteSimulatedParticipants);

        /**
         * Invocado cuando el creador del partido desea eliminarlo de la base de datos
         *
         * @param eventId identificador del partido
         */
        void deleteEvent(String eventId);

        /**
         * Invocado para obtener la posible invitación al partido recibida por el usuario
         *
         * @return la invitación recibida o null
         */
        Invitation getEventInvitation();

        /**
         * Invocado para añadir al Presentador como Observer sobre la relación del usuario
         * actual con el partido
         */
        void registerUserRelationObserver();

        /**
         * Invocado para borrar al Presentador como Observer sobre la relación del usuario
         * actual con el partido
         */
        void unregisterUserRelationObserver();
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Invocado para mostrar en la interfaz el deporte que se juega en este partido
         *
         * @param sport identificador del deporte
         */
        void showEventSport(String sport);

        /**
         * Invocado para mostrar en la interfaz la instalación donde se juega este partido
         *
         * @param field   instalación con sus parámetros. Puede ser null, si el deporte no requiere
         *                instalación
         * @param address dirección postal donde se juega. Coincide con la dirección de la
         *                instalación si la hay.
         * @param coord   coordenadas sobre el mapa del lugar del partido. Coincide con las coordenadas
         *                de la instalación si la hay.
         */
        void showEventField(Field field, String address, LatLng coord);

        /**
         * Invocado para mostrar en la interfaz el nombre asociado al partido
         *
         * @param name nombre
         */
        void showEventName(String name);

        /**
         * Invocado para mostrar en la interfaz la fecha y la hora del partido
         *
         * @param date fecha y hora en milisegundos
         */
        void showEventDate(long date);

        /**
         * Invocado para mostrar en la interfaz el creador del partido
         *
         * @param owner identificador del usuario creador del partido
         */
        void showEventOwner(String owner);

        /**
         * Invocado para mostrar en la interfaz el número de puestos totales y vacantes
         *
         * @param emptyPlayers número de puestos vacantes
         * @param totalPlayers número de puestos totales
         */
        void showEventPlayers(int emptyPlayers, int totalPlayers);

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
         * Invocado para limpiar la interfaz de los datos del partido
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
         *
         * @return BaseFragment que implementa este método
         */
        BaseFragment getThis();

        /**
         * Invocado para obtener el identificador del partido que se está mostrando
         *
         * @return identificador del partido
         */
        String getEventID();

        /**
         * Invocado para modificar el aspecto de la interfaz en base a la relación especificada
         *
         * @param relation tipo de relación entre el usuario actual y el partido que se está
         *                 mostrando
         */
        void uiSetupForEventRelation(@DetailEventPresenter.EventRelationType int relation);
    }
}
