package com.usal.jorgeav.sportapp;

import android.support.v4.app.Fragment;

/**
 * Created by Jorge Avila on 28/04/2017.
 */

public abstract class MainActivityContract {

    public interface FragmentManagement {
        void initFragment(Fragment fragment, boolean isOnBackStack);
        void setCurrentDisplayedFragment(String title, Fragment fragment);
    }

    public interface ActionBarChangeIcon {
        void setToolbarAsNav();
        void setToolbarAsUp();
    }
}
