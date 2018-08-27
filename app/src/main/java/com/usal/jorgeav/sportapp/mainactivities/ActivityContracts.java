package com.usal.jorgeav.sportapp.mainactivities;

import android.support.v4.app.Fragment;

import com.usal.jorgeav.sportapp.BaseFragment;

import org.jetbrains.annotations.NotNull;

/**
 * Clase abstracta para alojar las interfaces que deben implementar las Actividades que
 * deben proveer, a los Fragmentos, control sobre algunos de los elementos que alojan.
 */
public abstract class ActivityContracts {

    /**
     * Esta interfaz proporciona control sobre los Fragmentos que está alojando la Actividad.
     * Inicia transiciones y establece el estado del Fragmento actual.
     */
    public interface FragmentManagement {
        /**
         * Inicia la transición hacia el Fragmento especificado y lo almacena en
         * la pila de de Fragmentos si corresponde
         *
         * @param fragment Fragmento que va a mostrarse
         * @param addToBackStack true si debe almacenarse en la pila de Fragmentos
         */
        void initFragment(@NotNull Fragment fragment, boolean addToBackStack);
        void initFragment(@NotNull Fragment fragment, boolean addToBackStack, String tag);
        void setCurrentDisplayedFragment(String title, BaseFragment fragment);
        void showContent();
        void hideContent();
        void signOut();
    }

    /**
     * Esta interfaz porporciona control sobre el menú lateral de navegación. Establece su
     * aspecto o actualiza la información que muestra.
     */
    public interface NavigationDrawerManagement {
        void setToolbarAsNav();
        void setToolbarAsUp();
        void setActionBarTitle(String title);
        void setUserInfoInNavigationDrawer();
        void simulateNavigationItemSelected(int menuItemId, String intentExtraKey, String intentExtraValue);
    }
}
