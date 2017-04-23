package com.usal.jorgeav.sportapp;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class UserPresenter implements UserContract.Presenter {
    private UserManager mUserManager;
    private UserContract.View mUserView;

    public UserPresenter(UserManager userManager, UserContract.View userView) {
        mUserManager = userManager;
        mUserView = userView;
    }

    @Override
    public void loadUser() {
        mUserManager.loadNewUser(mUserView.getContext());

        mUserView.showUserImage(mUserManager.User().imageProfile);
        mUserView.showUserName(mUserManager.User().name);
        mUserView.showUserCity(mUserManager.User().city);
        mUserView.showUserAge(mUserManager.User().age);
    }
}
