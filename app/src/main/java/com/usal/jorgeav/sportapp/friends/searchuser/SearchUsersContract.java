package com.usal.jorgeav.sportapp.friends.searchuser;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 04/06/2017.
 */

public abstract class SearchUsersContract {

    public interface Presenter {
        void loadNearbyUsers(LoaderManager loaderManager, Bundle b);
        void loadNearbyUsersWithName(LoaderManager loaderManager, Bundle b);
    }

    public interface View {
        void showUsers(Cursor cursor);
        Context getActivityContext();
    }
}
