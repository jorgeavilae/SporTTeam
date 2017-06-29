package com.usal.jorgeav.sportapp.profile.friendrequests;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 26/05/2017.
 */

public abstract class FriendRequestsContract {

    public interface Presenter {
        void loadFriendRequests(LoaderManager loaderManager, Bundle b);
    }

    public interface View {
        void showFriendRequests(Cursor cursor);
        Context getActivityContext();
    }
}
