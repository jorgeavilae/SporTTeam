package com.usal.jorgeav.sportapp.events.addevent;

import android.content.Context;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SimulatedUser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador,
 * de la arquitectura utilizada Modelo - Vista - Presentador, para la creación y edición de partidos.
 */
public abstract class NewEventContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para crear el partido en la base de datos con los parámetros especificados
         *
         * @param id                    identificador del partido que se está editando, o null si es
         *                              un proceso de creación
         * @param sport                 deporte del partido
         * @param field                 identificador de la instalación donde se juega el partido, o
         *                              null si el deporte no necesita una instalación
         * @param address               dirección del lugar donde se juega el partido. Coincide con
         *                              el de la instalación, si la hay.
         * @param coord                 coordenadas del lugar donde se juega el partido. Coincide
         *                              con las de la instalación, si la hay.
         * @param name                  nombre del partido
         * @param city                  ciudad donde se juega el partido. Coincide con la de la
         *                              instalación, si la hay.
         * @param date                  fecha del partido
         * @param time                  hora del partido
         * @param total                 número total de jugadores para el partido
         * @param empty                 número de puestos vacantes
         * @param participants          listado de participantes ya inscritos en el momento de la
         *                              edición, o null en el caso de la creación.
         * @param simulatedParticipants listado de participantes simulados ya inscritos en el
         *                              momento de la edición, o null en el caso de la creación.
         * @param friendsId             listado de amigos del usuario creador del partido, a los que
         *                              avisar de la creación del partido.
         * @param isEditFromPast        true si se está editando un partido del pasado, en cuyo caso
         *                              se copia a uno nuevo en vez de modificarlo
         */
        void addEvent(String id, String sport, String field, String address, LatLng coord, String name, String city,
                      String date, String time, String total, String empty,
                      HashMap<String, Boolean> participants,
                      HashMap<String, SimulatedUser> simulatedParticipants,
                      ArrayList<String> friendsId, boolean isEditFromPast);

        /**
         * Invocado para iniciar el proceso de consulta del partido de la base de datos que se
         * quiere editar
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void openEvent(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para iniciar el proceso de consulta de participantes y participantes simulados
         * del partido de la base de datos que se quiere editar
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void loadParticipants(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para iniciar el proceso de consulta de instalaciones de la base de datos donde
         * situar el partido
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void loadFields(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para iniciar el proceso de consulta de la base de datos de amigos del usuario
         * actual a los que se debe avisar de la creación del partido
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void loadFriends(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para detener el proceso de consulta a la base de datos del partido que se
         * quiere editar
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para detener la consulta al
         *                      Proveedor de Contenido
         */
        void destroyOpenEventLoader(LoaderManager loaderManager);

        /**
         * Invocado para detener el proceso de consulta de la base de datos de instalaciones
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para detener la consulta al
         *                      Proveedor de Contenido
         */
        void stopLoadFields(LoaderManager loaderManager);

        /**
         * Invocado para detener el proceso de consulta de la base de datos de amigos
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para detener la consulta al
         *                      Proveedor de Contenido
         */
        void stopLoadFriends(LoaderManager loaderManager);
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
        void showEventSport(String sport);

        /**
         * Invocado para mostrar el lugar del encuentro en el mapa de la interfaz
         *
         * @param fieldId     identificador de la instalación o null
         * @param address     dirección del partido
         * @param city        ciudad del partido
         * @param coordinates coordenadas del partido, correspondientes a la dirección anterior
         */
        void showEventField(String fieldId, String address, String city, LatLng coordinates);

        /**
         * Invocado para mostrar el nombre del encuentro en la interfaz
         *
         * @param name nombre del partido
         */
        void showEventName(String name);

        /**
         * Invocado para mostrar la fecha y la hora del encuentro en la interfaz
         *
         * @param date fecha y hora del partido en milisegundos
         */
        void showEventDate(long date);

        /**
         * Invocado para mostrar el número total de jugadores del partido en la interfaz
         *
         * @param totalPlayers número de puestos totales en el partido
         */
        void showEventTotalPlayers(int totalPlayers);

        /**
         * Invocado para mostrar el número de puestos vacantes del partido en la interfaz
         *
         * @param emptyPlayers número de puestos vacantes en el partido
         */
        void showEventEmptyPlayers(int emptyPlayers);

        /**
         * Invocado para establecer en la Vista el listado de participantes al partido
         *
         * @param participants mapa de participantes. La clave es el identificador de usuario, el
         *                     valor es un booleano que indica si asiste al partido o está bloqueado
         */
        void setParticipants(HashMap<String, Boolean> participants);

        /**
         * Invocado para establecer en la Vista el listado de participantes simulados al partido
         *
         * @param simulatedParticipants mapa de participantes simulados. La clave es el
         *                              identificador del usuario simulado, el valor es el usuario
         *                              simulado.
         */
        void setSimulatedParticipants(HashMap<String, SimulatedUser> simulatedParticipants);

        /**
         * Invocado para indicarle a la Vista las instalaciones encontradas en la base de datos
         *
         * @param fieldList lista de instalaciones
         */
        void retrieveFields(ArrayList<Field> fieldList);

        /**
         * Invocado para indicarle a la Vista los usuarios amigos del actual encontrados en la base
         * de datos
         *
         * @param friendsIdList lista de amigos
         */
        void retrieveFriendsID(ArrayList<String> friendsIdList);

        /**
         * Invocado para limpiar de la interfaz los datos del partido
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
    }
}
