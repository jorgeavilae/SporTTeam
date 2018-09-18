package com.usal.jorgeav.sportapp.adduser;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.data.Sport;

import java.util.ArrayList;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador,
 * de la arquitectura utilizada Modelo - Vista - Presentador, para la creación de usuarios.
 */
public class NewUserContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para comprobar la existencia del email en el proceso de creación de usuario.
         *
         * @param email dirección de email introducida
         */
        void checkUserEmailExists(String email);

        /**
         * Invocado para comprobar la existencia del nombre en el proceso de creación de usuario.
         *
         * @param name nombre introducido
         */
        void checkUserNameExists(String name);

        /**
         * Invocado para crear el usuario con los parámetros dados
         *
         * @param email dirección de email
         * @param pass contraseña
         * @param name nombre
         * @param croppedImageFileSystemUri ruta del archivo de imagen utilizado como foto de perfil
         * @param age edad
         * @param city ciudad
         * @param coords coordenadas de la ciudad
         * @param sportsList lista de {@link Sport} que practica el usuario
         * @return true si los argumentos son válidos, false en caso contrario.
         */
        boolean createAuthUser(String email, String pass, String name,
                               Uri croppedImageFileSystemUri, String age,
                               String city, LatLng coords, ArrayList<Sport> sportsList);
    }

    /**
     * Interfaz de la Vista con los métodos utilizados para comunicarse con ella
     */
    public interface View {
        /**
         * Invocado al finalizar el proceso de recortar la foto de perfil con uCrop
         * para indicar la ruta del archivo de imagen resultante.
         *
         * @param photoCroppedUri ruta del archivo de imagen de la foto de perfil
         *
         * @see
         * <a href= "https://github.com/Yalantis/uCrop">
         *     uCrop (Github)
         * </a>
         */
        void croppedResult(Uri photoCroppedUri);

        /**
         * Invocado para establecer un error en la dirección de email introducida
         *
         * @param stringRes identificador del recurso de texto utilizado para indicar el error
         */
        void setEmailError(int stringRes);

        /**
         * Invocado para establecer un error en el nombre introducido
         *
         * @param stringRes identificador del recurso de texto utilizado para indicar el error
         */
        void setNameError(int stringRes);

        /**
         * Invocado para provocar que el contenido del fragmento se vuelva visible
         */
        void showContent();

        /**
         * Invocado para obtener una referencia a este {@link BaseFragment}
         *
         * @return referencia a la Fragmento que implementa este método
         */
        BaseFragment getThis();

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();

        /**
         * Invocado para obtener una referencia a la {@link Activity} contenedora
         *
         * @return Actividad contenedora
         */
        Activity getHostActivity();
    }
}
