package com.usal.jorgeav.sportapp.profile;

import com.usal.jorgeav.sportapp.data.UserManager;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class ProfilePresenter implements ProfileContract.Presenter {
    private UserManager mUserManager;
    private ProfileContract.View mUserView;

    public ProfilePresenter(UserManager userManager, ProfileContract.View userView) {
        mUserManager = userManager;
        mUserView = userView;
    }

    @Override
    public void loadUser() {
        mUserManager.loadNewUser(mUserView.getContext());

        mUserView.showUserImage(mUserManager.User().getImageProfile());
        mUserView.showUserName(mUserManager.User().getName());
        mUserView.showUserCity(mUserManager.User().getCity());
        mUserView.showUserAge(mUserManager.User().getAge());
    }
}
