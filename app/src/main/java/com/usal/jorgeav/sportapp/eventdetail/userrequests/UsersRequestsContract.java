package com.usal.jorgeav.sportapp.eventdetail.userrequests;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public abstract class UsersRequestsContract {

    public interface Presenter {
        void loadUsersRequests();
        LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance();
    }

    public interface View {
        void showUsersRequests(Cursor cursor);
        Context getActivityContext();
        UsersRequestsFragment getThis();
    }
}
