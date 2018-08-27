package com.usal.jorgeav.sportapp.mainactivities;

import android.support.v4.app.Fragment;

import com.usal.jorgeav.sportapp.BaseFragment;

import org.jetbrains.annotations.NotNull;

public abstract class ActivityContracts {

    public interface FragmentManagement {
        void startMainFragment();
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
