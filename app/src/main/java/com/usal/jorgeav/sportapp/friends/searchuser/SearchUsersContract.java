package com.usal.jorgeav.sportapp.friends.searchuser;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 04/06/2017.
 */

public class SearchUsersContract {

    public interface Presenter {
        void loadUsers();
        LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance();
    }

    public interface View {
        void showUsers(Cursor cursor);
        Context getActivityContext();
        SearchUsersFragment getThis();
    }
}
