package com.usal.jorgeav.sportapp.eventdetail.inviteuser;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
 * Created by Jorge Avila on 29/05/2017.
 */

public class InviteUserFragment extends BaseFragment implements InviteUserContract.View, UsersAdapter.OnUserItemClickListener {
    private static final String TAG = InviteUserFragment.class.getSimpleName();
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";

    private static String mEvent = "";
    InviteUserContract.Presenter mSendInvitationPresenter;
    UsersAdapter mSendInvitationRecyclerAdapter;

    @BindView(R.id.recycler_list)
    RecyclerView sendInvitationRecyclerList;
    @BindView(R.id.list_placeholder)
    ConstraintLayout sendInvitationPlaceholder;

    public InviteUserFragment() {
        // Required empty public constructor
    }

    public static InviteUserFragment newInstance(@NonNull String eventId) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_EVENT_ID, eventId);
        InviteUserFragment fragment = new InviteUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mSendInvitationPresenter = new InviteUserPresenter(this);
        mSendInvitationRecyclerAdapter = new UsersAdapter(null, this);
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

        if (getArguments() != null && getArguments().containsKey(BUNDLE_EVENT_ID))
            mEvent = getArguments().getString(BUNDLE_EVENT_ID);

        sendInvitationRecyclerList.setAdapter(mSendInvitationRecyclerAdapter);
        sendInvitationRecyclerList.setHasFixedSize(true);
        sendInvitationRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Selecciona usuario", this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        mSendInvitationPresenter.loadFriends(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mSendInvitationRecyclerAdapter.replaceData(null);
    }

    @Override
    public void showFriends(Cursor cursor) {
        mSendInvitationRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            sendInvitationRecyclerList.setVisibility(View.VISIBLE);
            sendInvitationPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            sendInvitationRecyclerList.setVisibility(View.INVISIBLE);
            sendInvitationPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onUserClick(final String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setPositiveButton("Send Invitation", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mSendInvitationPresenter.sendInvitationToThisEvent(mEvent, uid);
                    }
                })
                .setNeutralButton("See details", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Fragment newFragment = ProfileFragment.newInstance(uid);
                        mFragmentManagementListener.initFragment(newFragment, true);
                    }
                });
        builder.create().show();
    }
}
