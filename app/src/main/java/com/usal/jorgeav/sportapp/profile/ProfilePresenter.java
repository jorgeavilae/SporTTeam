package com.usal.jorgeav.sportapp.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Usuario;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class ProfilePresenter implements ProfileContract.Presenter {
    private ProfileContract.View mUserView;
    private Usuario mCurrentUser;

    public ProfilePresenter(ProfileContract.View userView) {
        mUserView = userView;
    }

    @Override
    public void loadUser() {
        loadNewUser(mUserView.getContext());

        mUserView.showUserImage(mCurrentUser.getImageProfile());
        mUserView.showUserName(mCurrentUser.getName());
        mUserView.showUserCity(mCurrentUser.getCity());
        mUserView.showUserAge(mCurrentUser.getAge());
    }

    private void loadNewUser(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.android_cheer);
        mCurrentUser = new Usuario(bitmap, "Nombre Apellidos", "Ciudad, País", "30");
    }
}
