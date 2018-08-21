package com.usal.jorgeav.sportapp.profile.sendinvitation;

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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendInvitationFragment extends BaseFragment implements SendInvitationContract.View, EventsAdapter.OnEventItemClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = SendInvitationFragment.class.getSimpleName();
    public static final String BUNDLE_INSTANCE_UID = "BUNDLE_INSTANCE_UID";

    private String mUserId;
    SendInvitationContract.Presenter mSendInvitationPresenter;

    @BindView(R.id.recycler_list)
    RecyclerView sendInvitationList;
    EventsAdapter mEventsRecyclerAdapter;
    @BindView(R.id.list_placeholder)
    ConstraintLayout sendInvitationPlaceholder;

    public SendInvitationFragment() {
        // Required empty public constructor
    }

    public static SendInvitationFragment newInstance(@NonNull String uid) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_INSTANCE_UID, uid);
        SendInvitationFragment fragment = new SendInvitationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mSendInvitationPresenter = new SendInvitationPresenter(this);
        mEventsRecyclerAdapter = new EventsAdapter(null, this, Glide.with(this));
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

        if (getArguments() != null && getArguments().containsKey(BUNDLE_INSTANCE_UID))
            mUserId = getArguments().getString(BUNDLE_INSTANCE_UID);

        sendInvitationList.setAdapter(mEventsRecyclerAdapter);
        sendInvitationList.setHasFixedSize(true);
        sendInvitationList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.pick_event), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        mSendInvitationPresenter.loadEventsForInvitation(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mEventsRecyclerAdapter.replaceData(null);
    }

    @Override
    public void showEventsForInvitation(Cursor cursor) {
        mEventsRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            sendInvitationList.setVisibility(View.VISIBLE);
            sendInvitationPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            sendInvitationList.setVisibility(View.INVISIBLE);
            sendInvitationPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onEventClick(final String eventId) {
        String userName = UtilesContentProvider.getUserNameFromContentProvider(mUserId);
        if (userName != null && !TextUtils.isEmpty(userName)) {
            String msg = getString(R.string.dialog_msg_send_invitation_to_user);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                    .setMessage(String.format(msg, userName))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mSendInvitationPresenter.sendInvitationToThisUser(eventId, mUserId);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setNeutralButton(R.string.see_details, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Fragment newFragment = DetailEventFragment.newInstance(eventId);
                            mFragmentManagementListener.initFragment(newFragment, true);
                        }
                    });
            builder.create().show();
        }
    }
}
