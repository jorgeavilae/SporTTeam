package com.usal.jorgeav.sportapp.profile.friendrequests;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendRequestsFragment extends BaseFragment implements FriendRequestsContract.View,
        UsersAdapter.OnUserItemClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = FriendRequestsFragment.class.getSimpleName();

    FriendRequestsContract.Presenter mFriendRequestsPresenter;
    @BindView(R.id.recycler_list)
    RecyclerView friendRequestsList;
    UsersAdapter mUsersRecyclerAdapter;
    @BindView(R.id.list_placeholder)
    ConstraintLayout friendRequestsPlaceholder;

    public FriendRequestsFragment() {
        // Required empty public constructor
    }

    public static FriendRequestsFragment newInstance() {
        return new FriendRequestsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mFriendRequestsPresenter = new FriendRequestsPresenter(this);
        mUsersRecyclerAdapter = new UsersAdapter(null, this, Glide.with(this));
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
        friendRequestsList.setLayoutManager(new GridLayoutManager(getActivityContext(),
                Utiles.calculateNoOfColumns(getActivityContext())));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.friend_requests_received), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
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
    public boolean onUserLongClick(String uid) {
        return false;
    }

    @Override
    public void showFriendRequests(Cursor cursor) {
        mUsersRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            friendRequestsList.setVisibility(View.VISIBLE);
            friendRequestsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            friendRequestsList.setVisibility(View.INVISIBLE);
            friendRequestsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }
}
