package com.usal.jorgeav.sportapp.profile.friendrequests;

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
import android.widget.Toast;

import com.usal.jorgeav.sportapp.MainActivityContract;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.data.User;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 25/05/2017.
 */

public class FriendRequestsFragment extends Fragment implements FriendRequestsContract.View,
        UsersAdapter.OnUserItemClickListener {
    private static final String TAG = FriendRequestsFragment.class.getSimpleName();
    public static final int LOADER_FRIENDS_REQUESTS_ID = 5000;

    private MainActivityContract.ActionBarIconManagement mActionBarIconManagementListener;
    private MainActivityContract.FragmentManagement mFragmentManagementListener;
    FriendRequestsContract.Presenter mFriendRequestsPresenter;
    UsersAdapter mUsersRecyclerAdapter;

    @BindView(R.id.recycler_list)
    RecyclerView friendRequestsList;

    public FriendRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFriendRequestsPresenter = new FriendRequestsPresenter(this);
        mUsersRecyclerAdapter = new UsersAdapter(null, this);
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
        mFragmentManagementListener.setCurrentDisplayedFragment(FriendRequestsFragment.class.getSimpleName(), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onResume() {
        super.onResume();
//        mFriendRequestsPresenter.loadFriendRequests();
        getLoaderManager().initLoader(LOADER_FRIENDS_REQUESTS_ID, null, mFriendRequestsPresenter.getLoaderInstance());
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
    public void onUserClick(User user) {
        //TODO manage this properly
        Toast.makeText(getActivity(), "onUserClick not implemented yet.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFriendRequests(Cursor cursor) {
        mUsersRecyclerAdapter.replaceData(cursor);
        mFragmentManagementListener.showContent();
    }

    @Override
    public Context getActivityContext() {
        return getActivity();
    }

}
