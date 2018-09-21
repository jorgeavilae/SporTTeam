package com.usal.jorgeav.sportapp.eventdetail.simulateparticipant;

import android.content.Context;
import android.net.Uri;

import com.usal.jorgeav.sportapp.BaseFragment;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador
 * de la arquitectura utilizada Modelo - Vista - Presentador, para añadir usuarios simulados.
 */
public abstract class SimulateParticipantContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para añadir a la base de datos del servidor los datos proporcionados como
         * un usuario simulado del partido indicado
         *
         * @param eventId identificador del partido al que asistirá el usuario simulado
         * @param name    nombre del usuario simulado
         * @param photo   imagen del usuario simulado
         * @param ageStr  edad del usuario simulado en formato texto
         */
        void addSimulatedParticipant(String eventId, String name, Uri photo, String ageStr);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Invocado para comunicar a la Vista la ruta donde se ha almacenado la foto del usuario
         * simulado después de ser recortada con uCrop.
         *
         * @param photoCroppedUri ruta del archivo
         * @see <a href= "https://github.com/Yalantis/uCrop">uCrop (Github)</a>
         */
        void croppedResult(Uri photoCroppedUri);

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
         * Invocado para mostrar en la interfaz algún mensaje. No es necesario asegurarse de que la
         * ejecución de este método se produce dentro del hilo principal.
         *
         * @param msgResource identificador del recurso de texto correspondiente al mensaje que se
         *                    quiere mostrar
         */
        void showMsgFromUIThread(int msgResource);

        /**
         * Muestra el contenido de la interfaz que maneja la Vista
         */
        void showContent();

        /**
         * Oculta el contenido de la interfaz que maneja la Vista
         */
        void hideContent();

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
