package com.usal.jorgeav.sportapp.profile;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public abstract class ProfileContract {

    public interface View {
        void showUserImage(Bitmap image);
        void showUserName(String name);
        void showUserCity(String city);
        void showUserAge(String age);

        Context getContext();
    }

    public interface Presenter {
       void loadUser();
    }
}
