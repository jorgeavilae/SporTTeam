package com.usal.jorgeav.sportapp.profile;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public abstract class ProfileContract {

    public interface View {
        void showUserImage(String image);
        void showUserName(String name);
        void showUserCity(String city);
        void showUserAge(int age);
        void showSports(Cursor cursor);

        Context getContext();
    }

    public interface Presenter {
       void loadUser();
        LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance();
    }
}
