package com.usal.jorgeav.sportapp.mainactivities;

import android.support.v4.app.Fragment;

import com.usal.jorgeav.sportapp.BaseFragment;

import org.jetbrains.annotations.NotNull;

/**
 * Clase abstracta para alojar las interfaces. Deben ser implementadas por las Actividades
 * si quieren proveer a los Fragmentos control sobre algunos de los elementos que alojan.
 */
public abstract class ActivityContracts {

    /**
     * Esta interfaz proporciona control sobre los Fragmentos que está alojando la Actividad.
     * Inicia transiciones y establece el estado del Fragmento actual.
     */
    public interface FragmentManagement {

        /**
         * Debe invocar {@link #initFragment(Fragment, boolean, String)} con un tag nulo.
         *
         * @param fragment       Fragmento que va a mostrarse
         * @param addToBackStack true si debe almacenarse en la pila de Fragmentos
         */
        void initFragment(@NotNull Fragment fragment, boolean addToBackStack);

        /**
         * Inicia la transición hacia el Fragmento especificado y lo almacena en
         * la pila de de Fragmentos si corresponde. Asocia una etiqueta al Fragmento para
         * poder ser encontrado posteriormente con
         * {@link android.support.v4.app.FragmentManager#findFragmentByTag(String)}
         *
         * @param fragment       Fragmento que va a mostrarse
         * @param addToBackStack true si debe almacenarse en la pila de Fragmentos
         * @param tag            etiqueta asociada al Fragmento en la transición
         */
        void initFragment(@NotNull Fragment fragment, boolean addToBackStack, String tag);

        /**
         * El Fragmento invoca este método para indicar que se está mostrando actualmente.
         * Así, la Actividad pueda actuar en consecuencia.
         *
         * @param title    título del Fragmento
         * @param fragment referencia al Fragmento
         */
        void setCurrentDisplayedFragment(String title, BaseFragment fragment);

        /**
         * Indica que el contenido del Fragmento está listo para ser mostrado
         */
        void showContent();

        /**
         * Indica que el contenido del Fragmento debe ocultarse
         */
        void hideContent();

        /**
         * Método para cerrar la sesión del usuario identificado desde el Fragmento
         */
        void signOut();
    }

    /**
     * Esta interfaz proporciona control sobre el menú lateral de navegación. Establece su
     * aspecto o actualiza la información que muestra.
     */
    public interface NavigationDrawerManagement {
        /**
         * Establece el comportamiento de la Toolbar para cuando debe mostrarse como raíz
         * de navegación. Esto ocurre cuando se muestra el Fragmento principal de las opciones
         * del menú lateral de navegación.
         */
        void setToolbarAsNav();

        /**
         * Establece el comportamiento de la Toolbar para cuando debe mostrarse como hijo
         * de la navegación. Esto ocurre cuando se muestran los Fragmentos secundarios de
         * las opciones del menú lateral de navegación.
         */
        void setToolbarAsUp();

        /**
         * Invocado para establecer el título de la Toolbar
         *
         * @param title Cadena de texto del título
         */
        void setActionBarTitle(String title);

        /**
         * Invocado para notificar cambios sobre los datos del usuario que se muestran en
         * la cabecera del menú lateral de navegación.
         */
        void setUserInfoInNavigationDrawer();

        /**
         * Invocado para simular la pulsación del usuario sobre una de las entradas del
         * menú lateral de navegación. Esto servirá para iniciar desde el Fragmento la
         * navegación desde una Actividad a otra, mediante un {@link android.content.Intent}.
         *
         * @param menuItemId       identificador de la entrada del menú que simula ser pulsada
         * @param intentExtraKey   clave del valor pasado a la nueva Actividad mediante el Intent.
         *                         También puede ser null.
         * @param intentExtraValue valor pasado a la nueva Actividad mediante el Intent. También
         *                         puede ser null.
         */
        void simulateNavigationItemSelected(int menuItemId, String intentExtraKey, String intentExtraValue);
    }
}
