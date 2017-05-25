package com.usal.jorgeav.sportapp.profile;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.MainActivityContract;
import com.usal.jorgeav.sportapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment implements ProfileContract.View {
    private final static String TAG = ProfileFragment.class.getSimpleName();

    public static final int LOADER_MYPROFILE_ID = 1001;
    private ProfileContract.Presenter mProfilePresenter;
    private MainActivityContract.FragmentManagement mFragmentManagementListener;

    @BindView(R.id.user_event_requests)
    Button userEventRequestsButton;
    @BindView(R.id.user_friend_requests)
    Button userFriendRequestsButton;
    @BindView(R.id.user_image)
    ImageView userImage;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_city)
    TextView userCity;
    @BindView(R.id.user_age)
    TextView userAge;
    @BindView(R.id.user_sport_list)
    RecyclerView userSportList;
    @BindView(R.id.user_edit_sport)
    Button userEditSportListButton;

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
        final Fragment f = this;
        userEventRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new EventRequestsFragment();
                mFragmentManagementListener.initFragment(fragment, true);
            }
        });
        userFriendRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FriendRequestsFragment();
                mFragmentManagementListener.initFragment(fragment, true);
            }
        });
        userEditSportListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SportsListFragment();
                mFragmentManagementListener.initFragment(fragment, true);
            }
        });


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG+" onActivityCreated", getActivity().toString());
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.profile), this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        mProfilePresenter.loadUser();
        getLoaderManager().initLoader(LOADER_MYPROFILE_ID, null, mProfilePresenter.getLoaderInstance());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG+" onAttach", getActivity().toString());
        if (context instanceof MainActivityContract.FragmentManagement)
            mFragmentManagementListener = (MainActivityContract.FragmentManagement) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentManagementListener = null;
    }

    @Override
    public void showUserImage(String image) {
        mFragmentManagementListener.showContent();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.android_cheer);
        userImage.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
    }

    @Override
    public void showUserName(String name) {
        userName.setText(name);
        mFragmentManagementListener.showContent();
    }

    @Override
    public void showUserCity(String city) {
        userCity.setText(city);
        mFragmentManagementListener.showContent();

    }

    @Override
    public void showUserAge(String age) {
        userAge.setText(age);
        mFragmentManagementListener.showContent();

    }

    @Override
    public Context getContext() {
        return getActivity();
    }

}
