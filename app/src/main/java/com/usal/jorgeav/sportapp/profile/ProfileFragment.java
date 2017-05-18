package com.usal.jorgeav.sportapp.profile;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.MainActivityContract;
import com.usal.jorgeav.sportapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment implements ProfileContract.View {

    private ProfileContract.Presenter mProfilePresenter;
    private MainActivityContract.FragmentManagement mFragmentManagementListener;

    @BindView(R.id.user_image)
    ImageView userImage;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_city)
    TextView userCity;
    @BindView(R.id.user_age)
    TextView userAge;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProfilePresenter = new ProfilePresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, root);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.profile), this);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mProfilePresenter.loadUser();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivityContract.ActionBarChangeIcon)
            mFragmentManagementListener = (MainActivityContract.FragmentManagement) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentManagementListener = null;
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
        return getActivity();
    }

}
