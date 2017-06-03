package com.usal.jorgeav.sportapp.eventdetail.sendinvitation;

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

import com.usal.jorgeav.sportapp.ActivityContracts;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public class SendInvitationFragment extends Fragment implements SendInvitationContract.View, UsersAdapter.OnUserItemClickListener {
    private static final String TAG = SendInvitationFragment.class.getSimpleName();
    public static final int LOADER_FRIENDS_ID = 6000;
    public static final int LOADER_FRIENDS_AS_USERS_ID = 6001;

    SendInvitationContract.Presenter mSendInvitationPresenter;
    UsersAdapter mSendInvitationRecyclerAdapter;
    private ActivityContracts.FragmentManagement mFragmentManagementListener;
    private ActivityContracts.ActionBarIconManagement mActionBarIconManagementListener;

    @BindView(R.id.recycler_list)
    RecyclerView sendInvitationRecyclerList;

    public SendInvitationFragment() {
        // Required empty public constructor
    }

    public static SendInvitationFragment newInstance() {
        return new SendInvitationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSendInvitationPresenter = new SendInvitationPresenter(this);
        mSendInvitationRecyclerAdapter = new UsersAdapter(null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, root);

        sendInvitationRecyclerList.setAdapter(mSendInvitationRecyclerAdapter);
        sendInvitationRecyclerList.setHasFixedSize(true);
        sendInvitationRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getClass().getSimpleName(), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(LOADER_FRIENDS_ID, null, mSendInvitationPresenter.getLoaderInstance());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActivityContracts.FragmentManagement)
            mFragmentManagementListener = (ActivityContracts.FragmentManagement) context;
        if (context instanceof ActivityContracts.ActionBarIconManagement)
            mActionBarIconManagementListener = (ActivityContracts.ActionBarIconManagement) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentManagementListener = null;
        mActionBarIconManagementListener = null;
    }

    @Override
    public void showFriends(Cursor cursor) {
        mSendInvitationRecyclerAdapter.replaceData(cursor);
        mFragmentManagementListener.showContent();
    }

    @Override
    public Context getActivityContext() {
        return getActivity();
    }

    @Override
    public SendInvitationFragment getThis() {
        return this;
    }

    @Override
    public void onUserClick(String uid) {
        Fragment newFragment = ProfileFragment.newInstance(uid);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
