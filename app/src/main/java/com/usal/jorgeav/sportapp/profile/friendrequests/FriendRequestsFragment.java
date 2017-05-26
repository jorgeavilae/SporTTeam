package com.usal.jorgeav.sportapp.profile.friendrequests;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usal.jorgeav.sportapp.MainActivityContract;
import com.usal.jorgeav.sportapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 25/05/2017.
 */

public class FriendRequestsFragment extends Fragment implements FriendRequestsContract.View {

    private MainActivityContract.ActionBarIconManagement mActionBarIconManagementListener;
    private MainActivityContract.FragmentManagement mFragmentManagementListener;

    @BindView(R.id.recycler_list)
    RecyclerView friendRequestsList;

    public FriendRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//TODO friend requests
//        mFieldsPresenter = new FieldsPresenter(this);
//        mFieldsRecyclerAdapter = new FieldsAdapter(null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, root);

        //TODO friend requests
//        fieldsRecyclerList.setAdapter(mFieldsRecyclerAdapter);
//        fieldsRecyclerList.setHasFixedSize(true);
//        fieldsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

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
        //TODO friend request
//        mFieldsPresenter.loadFields();
//        getLoaderManager().initLoader(LOADER_FIELDS_ID, null, mFieldsPresenter.getLoaderInstance());
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
    public void showFriendRequests(Cursor cursor) {
        //TODO friend request

        mFragmentManagementListener.showContent();
    }

    @Override
    public Context getActivityContext() {
        return getActivity();
    }
}
