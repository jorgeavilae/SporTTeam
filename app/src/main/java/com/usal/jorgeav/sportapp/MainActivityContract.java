package com.usal.jorgeav.sportapp;

import android.support.v4.app.Fragment;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Jorge Avila on 28/04/2017.
 */

public abstract class MainActivityContract {

    public interface FragmentManagement {
        void initFragment(@NotNull Fragment fragment, boolean isOnBackStack);
        void setCurrentDisplayedFragment(String title, Fragment fragment);
        void showContent();
        void hideContent();
    }

    public interface ActionBarIconManagement {
        void setToolbarAsNav();
        void setToolbarAsUp();
    }
}
