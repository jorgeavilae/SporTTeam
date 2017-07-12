package com.usal.jorgeav.sportapp.friends;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.friends.searchuser.SearchUsersFragment;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 26/05/2017.
 */

public class FriendsFragment extends BaseFragment implements FriendsContract.View, UsersAdapter.OnUserItemClickListener {
    private static final String TAG = FriendsFragment.class.getSimpleName();

    FriendsContract.Presenter mFriendsPresenter;
    UsersAdapter mFriendsRecyclerAdapter;

    @BindView(R.id.friends_search)
    Button friendsSearchButton;
    @BindView(R.id.friends_list)
    RecyclerView friendsRecyclerList;
    @BindView(R.id.friends_placeholder)
    ConstraintLayout friendsPlaceholder;

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

        friendsSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SearchUsersFragment();
                mFragmentManagementListener.initFragment(fragment, true);
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.friends), this);
        mActionBarIconManagementListener.setToolbarAsNav();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFriendsPresenter.loadFriend(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mFriendsRecyclerAdapter.replaceData(null);
    }

    @Override
    public void showFriends(Cursor cursor) {
        mFriendsRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            friendsRecyclerList.setVisibility(View.VISIBLE);
            friendsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            friendsRecyclerList.setVisibility(View.INVISIBLE);
            friendsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onUserClick(String uid) {
        Fragment newFragment = ProfileFragment.newInstance(uid);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
