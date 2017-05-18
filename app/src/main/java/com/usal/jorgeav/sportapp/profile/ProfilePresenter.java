package com.usal.jorgeav.sportapp.profile;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class ProfilePresenter implements ProfileContract.Presenter {
    private ProfileContract.View mUserView;

    public ProfilePresenter(ProfileContract.View userView) {
        mUserView = userView;
    }

    @Override
    public void loadUser() {
        mUsuarioManager.loadNewUser(mUserView.getContext());

        mUserView.showUserImage(mUsuarioManager.User().getImageProfile());
        mUserView.showUserName(mUsuarioManager.User().getName());
        mUserView.showUserCity(mUsuarioManager.User().getCity());
        mUserView.showUserAge(mUsuarioManager.User().getAge());
    }
}
