package com.usal.jorgeav.sportapp.friends;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 26/05/2017.
 */

public abstract class FriendsContract {

    public interface Presenter {
        void loadFriend(LoaderManager loaderManager, Bundle b);
    }

    public interface View {
        void showFriends(Cursor cursor);
        Context getActivityContext();
        Fragment getThis();
    }
}
