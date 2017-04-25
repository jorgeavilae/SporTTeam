package com.usal.jorgeav.sportapp.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.UsuarioManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity implements ProfileContract.View {

    private ProfileContract.Presenter mUserPresenter;
    @BindView(R.id.user_image)
    ImageView userImage;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_city)
    TextView userCity;
    @BindView(R.id.user_age)
    TextView userAge;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        mUserPresenter = new ProfilePresenter(new UsuarioManager(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserPresenter.loadUser();
    }

    @Override
    public void showUserImage(Bitmap image) {
        userImage.setImageDrawable(new BitmapDrawable(getResources(), image));
    }

    @Override
    public void showUserName(String name) {
        userName.setText(name);
    }

    @Override
    public void showUserCity(String city) {
        userCity.setText(city);

    }

    @Override
    public void showUserAge(String age) {
        userAge.setText(age);

    }

    @Override
    public Context getContext() {
        return this;
    }
}
