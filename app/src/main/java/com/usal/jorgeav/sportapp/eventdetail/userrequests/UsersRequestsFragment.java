package com.usal.jorgeav.sportapp.eventdetail.userrequests;

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

public class UsersRequestsFragment extends BaseFragment implements UsersRequestsContract.View {
    private static final String TAG = UsersRequestsFragment.class.getSimpleName();
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";

    private static String mEventId = "";
    UsersRequestsContract.Presenter mUsersRequestsPresenter;

    UsersAdapter mUsersRequestRecyclerAdapter;
    @BindView(R.id.user_requests_list)
    RecyclerView usersRequestsList;
    UsersAdapter mUsersRejectedRecyclerAdapter;
    @BindView(R.id.user_rejected_list)
    RecyclerView usersRejectedList;

    public UsersRequestsFragment() {
        // Required empty public constructor
    }

    public static UsersRequestsFragment newInstance(@NonNull String eventID) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_EVENT_ID, eventID);
        UsersRequestsFragment fragment = new UsersRequestsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mUsersRequestsPresenter = new UsersRequestsPresenter(this);
        mUsersRequestRecyclerAdapter = new UsersAdapter(null, new UsersAdapter.OnUserItemClickListener() {
            @Override
            public void onUserClick(final String uid) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                builder.setMessage("Quieres aceptarlo como asistente?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mUsersRequestsPresenter.acceptUserRequestToThisEvent(mEventId, uid);
                            }
                        })
                        .setNegativeButton("Denegar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mUsersRequestsPresenter.declineUserRequestToThisEvent(mEventId, uid);
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
        });
        mUsersRejectedRecyclerAdapter = new UsersAdapter(null, new UsersAdapter.OnUserItemClickListener() {
            @Override
            public void onUserClick(final String uid) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                builder.setMessage("Quieres desbloquearlo?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mUsersRequestsPresenter.unblockUserParticipationRejectedToThisEvent(mEventId, uid);
                            }
                        })
                        .setNegativeButton("No", null)
                        .setNeutralButton("See details", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Fragment newFragment = ProfileFragment.newInstance(uid);
                                mFragmentManagementListener.initFragment(newFragment, true);
                            }
                        });
                builder.create().show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_request, container, false);
        ButterKnife.bind(this, root);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_EVENT_ID))
            mEventId = getArguments().getString(BUNDLE_EVENT_ID);

        usersRequestsList.setAdapter(mUsersRequestRecyclerAdapter);
        usersRequestsList.setHasFixedSize(true);
        usersRequestsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        usersRejectedList.setAdapter(mUsersRejectedRecyclerAdapter);
        usersRejectedList.setHasFixedSize(true);
        usersRejectedList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Peticiones para participar", this);
        mActionBarIconManagementListener.setToolbarAsUp();
        mUsersRequestsPresenter.loadUsersRequests(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mUsersRequestRecyclerAdapter.replaceData(null);
        mUsersRejectedRecyclerAdapter.replaceData(null);
    }

    @Override
    public void showUsersRequests(Cursor cursor) {
        // TODO: 01/07/2017 si es null o esta vacio mostrar placeholder
        mUsersRequestRecyclerAdapter.replaceData(cursor);
        mFragmentManagementListener.showContent();
    }

    @Override
    public void showRejectedUsers(Cursor cursor) {
        // TODO: 01/07/2017 si es null o esta vacio mostrar placeholder
        mUsersRejectedRecyclerAdapter.replaceData(cursor);
        mFragmentManagementListener.showContent();
    }
}
