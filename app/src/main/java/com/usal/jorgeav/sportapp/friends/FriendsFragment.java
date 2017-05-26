package com.usal.jorgeav.sportapp.friends;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usal.jorgeav.sportapp.MainActivityContract;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.data.User;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 26/05/2017.
 */

public class FriendsFragment extends Fragment implements FriendsContract.View, UsersAdapter.OnUserItemClickListener {
    private static final String TAG = FriendsFragment.class.getSimpleName();
    public static final int LOADER_FRIENDS_ID = 6000;

    FriendsContract.Presenter mFriendsPresenter;
    UsersAdapter mFriendsRecyclerAdapter;
    private MainActivityContract.FragmentManagement mFragmentManagementListener;
    private MainActivityContract.ActionBarIconManagement mActionBarIconManagementListener;

    @BindView(R.id.friends_list)
    RecyclerView friendsRecyclerList;

    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance() {
        return new FriendsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFriendsPresenter = new FriendsPresenter(this);
        mFriendsRecyclerAdapter = new UsersAdapter(null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_users, container, false);
        ButterKnife.bind(this, root);

        friendsRecyclerList.setAdapter(mFriendsRecyclerAdapter);
        friendsRecyclerList.setHasFixedSize(true);
        friendsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.friends), this);
        mActionBarIconManagementListener.setToolbarAsNav();
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(LOADER_FRIENDS_ID, null, mFriendsPresenter.getLoaderInstance());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivityContract.FragmentManagement)
            mFragmentManagementListener = (MainActivityContract.FragmentManagement) context;
        if (context instanceof MainActivityContract.ActionBarIconManagement)
            mActionBarIconManagementListener = (MainActivityContract.ActionBarIconManagement) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentManagementListener = null;
        mActionBarIconManagementListener = null;
    }

    @Override
    public void showFriends(Cursor cursor) {

    }

    @Override
    public Context getActivityContext() {
        return getActivity();
    }

    @Override
    public Fragment getThis() {
        return this;
    }

    @Override
    public void onUserClick(User user) {

    }
}
