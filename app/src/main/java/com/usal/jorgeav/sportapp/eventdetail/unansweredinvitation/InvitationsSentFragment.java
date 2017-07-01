package com.usal.jorgeav.sportapp.eventdetail.unansweredinvitation;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class InvitationsSentFragment extends BaseFragment implements InvitationsSentContract.View, UsersAdapter.OnUserItemClickListener {
    private static final String TAG = InvitationsSentFragment.class.getSimpleName();
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";

    private static String mEventId = "";
    InvitationsSentContract.Presenter mEventInvitationsPresenter;
    UsersAdapter mUsersAdapter;

    @BindView(R.id.recycler_list)
    RecyclerView userInvitationsSentList;

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
        mUsersAdapter = new UsersAdapter(null, this);
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
        userInvitationsSentList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Usuarios invitados", this);
        mActionBarIconManagementListener.setToolbarAsUp();
        mEventInvitationsPresenter.loadEventInvitationsSent(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mUsersAdapter.replaceData(null);
    }

    @Override
    public void showEventInvitationsSent(Cursor cursor) {
        // TODO: 01/07/2017 si es null o esta vacio mostrar placeholder
        mUsersAdapter.replaceData(cursor);
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onUserClick(final String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setPositiveButton("Delete Invitation", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mEventInvitationsPresenter.deleteInvitationToThisEvent(mEventId, uid);
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
