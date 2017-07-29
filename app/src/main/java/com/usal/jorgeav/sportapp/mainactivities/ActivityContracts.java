package com.usal.jorgeav.sportapp.mainactivities;

import android.support.v4.app.Fragment;

import com.usal.jorgeav.sportapp.BaseFragment;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Jorge Avila on 28/04/2017.
 */

public abstract class ActivityContracts {

    public interface FragmentManagement {
        void startMainFragment();
        void initFragment(@NotNull Fragment fragment, boolean isOnBackStack);
        void setCurrentDisplayedFragment(String title, BaseFragment fragment);
        void showContent();
        void hideContent();
    }

    public interface ActionBarIconManagement {
        void setToolbarAsNav();
        void setToolbarAsUp();
        void setActionBarTitle(String title);
    }
}
