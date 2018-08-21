package com.usal.jorgeav.sportapp.mainactivities;

import android.support.v4.app.Fragment;

import com.usal.jorgeav.sportapp.BaseFragment;

import org.jetbrains.annotations.NotNull;

public abstract class ActivityContracts {

    public interface FragmentManagement {
        void startMainFragment();
        void initFragment(@NotNull Fragment fragment, boolean isOnBackStack);
        void initFragment(@NotNull Fragment fragment, boolean isOnBackStack, String tag);
        void setCurrentDisplayedFragment(String title, BaseFragment fragment);
        void showContent();
        void hideContent();
        void signOut();
    }

    public interface NavigationDrawerManagement {
        void setToolbarAsNav();
        void setToolbarAsUp();
        void setActionBarTitle(String title);
        void setUserInfoInNavigationDrawer();
        void simulateNavigationItemSelected(int menuItemId, String intentExtraKey, String intentExtraValue);
    }
}
