package com.usal.jorgeav.sportapp.friends;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

abstract class FriendsContract {

    public interface Presenter {
        void loadFriend(LoaderManager loaderManager, Bundle b);
    }

    public interface View {
        void showFriends(Cursor cursor);
        Context getActivityContext();
    }
}
