package com.usal.jorgeav.sportapp.eventdetail.unansweredinvitation;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

public class InvitationsSentFragment extends BaseFragment implements InvitationsSentContract.View, UsersAdapter.OnUserItemClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = InvitationsSentFragment.class.getSimpleName();
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";

    InvitationsSentContract.Presenter mEventInvitationsPresenter;

    private static String mEventId = "";

    @BindView(R.id.recycler_list)
    RecyclerView userInvitationsSentList;
    UsersAdapter mUsersAdapter;
    @BindView(R.id.list_placeholder)
    ConstraintLayout userInvitationsSentPlaceholder;

    public InvitationsSentFragment() {
        // Required empty public constructor
    }

    public static InvitationsSentFragment newInstance(@NonNull String eventId) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_EVENT_ID, eventId);
        InvitationsSentFragment fragment = new InvitationsSentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mEventInvitationsPresenter = new InvitationsSentPresenter(this);
        mUsersAdapter = new UsersAdapter(null, this, Glide.with(this));
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
            mEventId = getArguments().getString(BUNDLE_EVENT_ID);

        userInvitationsSentList.setAdapter(mUsersAdapter);
        userInvitationsSentList.setHasFixedSize(true);
        userInvitationsSentList.setLayoutManager(new GridLayoutManager(getActivityContext(),
                Utiles.calculateNoOfColumns(getActivityContext())));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.action_unanswered_invitations), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        mEventInvitationsPresenter.loadEventInvitationsSent(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mUsersAdapter.replaceData(null);
    }

    @Override
    public void showEventInvitationsSent(Cursor cursor) {
        mUsersAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            userInvitationsSentList.setVisibility(View.VISIBLE);
            userInvitationsSentPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            userInvitationsSentList.setVisibility(View.INVISIBLE);
            userInvitationsSentPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onUserClick(final String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                .setMessage(R.string.dialog_msg_cancel_invitation)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mEventInvitationsPresenter.deleteInvitationToThisEvent(mEventId, uid);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setNeutralButton(R.string.see_details, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Fragment newFragment = ProfileFragment.newInstance(uid);
                        mFragmentManagementListener.initFragment(newFragment, true);
                    }
                });
        builder.create().show();
    }

    @Override
    public boolean onUserLongClick(String uid) {
        return false;
    }
}
