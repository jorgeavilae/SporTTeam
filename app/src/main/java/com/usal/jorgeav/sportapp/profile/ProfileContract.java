package com.usal.jorgeav.sportapp.profile;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
        void showContent();
        FragmentActivity getActivityContext();
        String getUserID();
        void uiSetupForUserRelation(@ProfilePresenter.UserRelationType int relation);
    }

    public interface Presenter {
       void openUser(LoaderManager loaderManager, Bundle b);
        void getRelationTypeBetweenThisUserAndI();
        void sendFriendRequest(String uid);
        void cancelFriendRequest(String uid);
        void acceptFriendRequest(String uid);
        void declineFriendRequest(String uid);
        void deleteFriend(String uid);
        void registerUserRelationObserver();
        void unregisterUserRelationObserver();
    }
}
