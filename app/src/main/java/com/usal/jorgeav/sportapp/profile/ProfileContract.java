package com.usal.jorgeav.sportapp.profile;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.firebase.database.ValueEventListener;

public abstract class ProfileContract {

    public interface View {
        void croppedResult(Uri photoCroppedUri);

        void showUserImage(String image);
        void showUserName(String name);
        void showUserAge(int age);
        void showUserCity(String city);
        void showSports(Cursor cursor);
        void showContent();
        void clearUI();

        Context getActivityContext();
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

        void checkUserName(String s, ValueEventListener listener);
        void updateUserName(String s);
        void updateUserAge(int age);
        void updateUserPhoto(Uri photoCroppedUri);
    }
}
