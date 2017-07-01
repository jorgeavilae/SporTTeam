package com.usal.jorgeav.sportapp.profile.friendrequests;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 25/05/2017.
 */

public class FriendRequestsFragment extends BaseFragment implements FriendRequestsContract.View,
        UsersAdapter.OnUserItemClickListener {
    private static final String TAG = FriendRequestsFragment.class.getSimpleName();

    FriendRequestsContract.Presenter mFriendRequestsPresenter;
    UsersAdapter mUsersRecyclerAdapter;

    @BindView(R.id.recycler_list)
    RecyclerView friendRequestsList;

    public FriendRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mFriendRequestsPresenter = new FriendRequestsPresenter(this);
        mUsersRecyclerAdapter = new UsersAdapter(null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, root);

        friendRequestsList.setAdapter(mUsersRecyclerAdapter);
        friendRequestsList.setHasFixedSize(true);
        friendRequestsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Peticiones de amistad", this);
        mActionBarIconManagementListener.setToolbarAsUp();
        mFriendRequestsPresenter.loadFriendRequests(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mUsersRecyclerAdapter.replaceData(null);
    }

    @Override
    public void onUserClick(String uid) {
        Fragment newFragment = ProfileFragment.newInstance(uid);
        mFragmentManagementListener.initFragment(newFragment, true);
    }

    @Override
    public void showFriendRequests(Cursor cursor) {
        // TODO: 01/07/2017 si es null o esta vacio mostrar placeholder
        mUsersRecyclerAdapter.replaceData(cursor);
        mFragmentManagementListener.showContent();
    }
}
