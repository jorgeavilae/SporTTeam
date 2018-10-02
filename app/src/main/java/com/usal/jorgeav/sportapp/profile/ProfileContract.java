package com.usal.jorgeav.sportapp.profile;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.data.Sport;

/**
 * Clase abstracta donde se declaran las interfaces por las que se comunican Vista y Presentador,
 * de la arquitectura utilizada Modelo - Vista - Presentador, para mostrar los detalles del
 * usuario actual o de un usuario cualquiera.
 */
public abstract class ProfileContract {

    /**
     * Interfaz del Presentador con los métodos utilizados para comunicarse con él
     */
    public interface Presenter {
        /**
         * Invocado para iniciar el proceso de consulta a la base de datos sobre los datos del usuario
         *
         * @param loaderManager objeto {@link LoaderManager} utilizado para consultar el Proveedor
         *                      de Contenido
         * @param b             contenedor de posibles parámetros utilizados en la consulta
         */
        void openUser(LoaderManager loaderManager, Bundle b);

        /**
         * Invocado para obtener la relación que el usuario actual mantiene con el usuario que
         * se muestra
         */
        //todo debería aceptar un listener como parámetro donde se especificara loq hace con la relation"
        void getRelationTypeBetweenThisUserAndI();

        /**
         * Invocado cuando el usuario actual quiere enviar una petición de amistad al usuario mostrado
         *
         * @param uid identificador del usuario mostrado
         */
        void sendFriendRequest(String uid);

        /**
         * Invocado cuando el usuario actual quiere cancelar la petición de amistad enviada al
         * usuario mostrado
         *
         * @param uid identificador del usuario mostrado
         */
        void cancelFriendRequest(String uid);

        /**
         * Invocado cuando el usuario actual quiere aceptar la petición de amistad recibida por
         * el usuario mostrado
         *
         * @param uid identificador del usuario mostrado
         */
        void acceptFriendRequest(String uid);

        /**
         * Invocado cuando el usuario actual quiere rechazar la petición de amistad recibida por
         * el usuario mostrado
         *
         * @param uid identificador del usuario mostrado
         */
        void declineFriendRequest(String uid);

        /**
         * Invocado cuando el usuario actual quiere eliminar al usuario mostrado como uno de sus
         * amigos
         *
         * @param uid identificador del usuario mostrado
         */
        void deleteFriend(String uid);

        /**
         * Invocado para añadir al Presentador como Observer de la relación del usuario actual con
         * el usuario mostrado
         */
        void registerUserRelationObserver();

        /**
         * Invocado para borrar al Presentador como Observer de la relación del usuario actual con
         * el usuario mostrado
         */
        void unregisterUserRelationObserver();

        /**
         * Invocado para buscar y comprobar la existencia, en la base de datos del servidor, de
         * algún usuario con el nombre proporcionado antes de cambiar el nombre del usuario actual
         * (los nombres deben ser únicos en el sistema).
         *
         * @param newName  nombre del que se comprueba la existencia
         * @param listener Listener con el que se especifica la acción a realizar una vez se
         *                 realice la consulta.
         */
        void checkUserName(String newName, ValueEventListener listener);

        /**
         * Invocado para establecer el nombre proporcionado como nuevo nombre del usuario actual
         *
         * @param name nuevo nombre del usuario actual
         */
        void updateUserName(String name);

        /**
         * Invocado para establecer la nueva edad proporcionada como nueva edad del usuario actual
         *
         * @param age nueva edad del usuario actual
         */
        void updateUserAge(int age);

        /**
         * Invocado para establecer la imagen de perfil proporcionada como nueva imagen de perfil
         * del usuario actual
         *
         * @param photoCroppedUri ruta dentro del sistema de archivos del dispositivo a la nueva
         *                        imagen de perfil del usuario actual
         */
        void updateUserPhoto(Uri photoCroppedUri);
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
         * @see <a href= "https://github.com/Yalantis/uCrop">uCrop (Github)</a>
         */
        void croppedResult(Uri photoCroppedUri);

        /**
         * Invocado para mostrar en la interfaz la imagen de perfil del usuario
         *
         * @param image ruta hacia la imagen de perfil dentro de Firebase Storage
         * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/storage/FirebaseStorage">
         * FirebaseStorage</a>
         */
        void showUserImage(String image);

        /**
         * Invocado para mostrar en la interfaz el nombre del usuario
         *
         * @param name nombre del usuario
         */
        void showUserName(String name);

        /**
         * Invocado para mostrar en la interfaz la edad del usuario
         *
         * @param age edad del usuario
         */
        void showUserAge(int age);

        /**
         * Invocado para mostrar en la interfaz la ciudad del usuario
         *
         * @param city ciudad el usuario
         */
        void showUserCity(String city);

        /**
         * Invocado para mostrar en la interfaz la lista de deportes practicados por el usuario y
         * su nivel de juego en ellos.
         *
         * @param cursor colección de {@link Sport} del usuario
         */
        void showSports(Cursor cursor);

        /**
         * Invocado para mostrar el contenido del Fragmento
         */
        void showContent();

        /**
         * Invocado para limpiar la interfaz de los datos del usuario
         */
        void clearUI();

        /**
         * Invocado para obtener una referencia al {@link Context} de la Actividad contenedora
         *
         * @return Context de la Actividad contenedora
         */
        Context getActivityContext();

        /**
         * Invocado para obtener el identificador del usuario que se está mostrando
         *
         * @return identificador del usuario
         */
        String getUserID();

        /**
         * Invocado para modificar el aspecto de la interfaz en base a la relación especificada
         *
         * @param relation tipo de relación entre el usuario actual y el usuario mostrado
         */
        void uiSetupForUserRelation(@ProfilePresenter.UserRelationType int relation);
    }
}
