package com.usal.jorgeav.sportapp.eventdetail.userrequests;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersRequestsFragment extends BaseFragment implements UsersRequestsContract.View {
    @SuppressWarnings("unused")
    private static final String TAG = UsersRequestsFragment.class.getSimpleName();
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";

    UsersRequestsContract.Presenter mUsersRequestsPresenter;

    private static String mEventId = "";

    UsersAdapter mUsersRequestRecyclerAdapter;
    @BindView(R.id.user_requests_list)
    RecyclerView usersRequestsList;
    @BindView(R.id.user_requests_placeholder)
    ConstraintLayout userRequestPlaceholder;

    UsersAdapter mUsersRejectedRecyclerAdapter;
    @BindView(R.id.user_rejected_list)
    RecyclerView usersRejectedList;
    @BindView(R.id.user_rejected_placeholder)
    ConstraintLayout usersRejectedPlaceholder;

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
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                        .setMessage(R.string.dialog_msg_accept_participant)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mUsersRequestsPresenter.acceptUserRequestToThisEvent(mEventId, uid);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mUsersRequestsPresenter.declineUserRequestToThisEvent(mEventId, uid);
                            }
                        })
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
        }, Glide.with(this));
        mUsersRejectedRecyclerAdapter = new UsersAdapter(null, new UsersAdapter.OnUserItemClickListener() {
            @Override
            public void onUserClick(final String uid) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                        .setMessage(R.string.dialog_msg_unblock_participant)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mUsersRequestsPresenter.unblockUserParticipationRejectedToThisEvent(mEventId, uid);
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
        }, Glide.with(this));
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
        usersRequestsList.setLayoutManager(new GridLayoutManager(getActivityContext(),
                Utiles.calculateNoOfColumns(getActivityContext())));

        usersRejectedList.setAdapter(mUsersRejectedRecyclerAdapter);
        usersRejectedList.setHasFixedSize(true);
        usersRejectedList.setLayoutManager(new GridLayoutManager(getActivityContext(),
                Utiles.calculateNoOfColumns(getActivityContext())));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.user_request), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
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
        mUsersRequestRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            usersRequestsList.setVisibility(View.VISIBLE);
            userRequestPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            usersRequestsList.setVisibility(View.INVISIBLE);
            userRequestPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void showRejectedUsers(Cursor cursor) {
        mUsersRejectedRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            usersRejectedList.setVisibility(View.VISIBLE);
            usersRejectedPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            usersRejectedList.setVisibility(View.INVISIBLE);
            usersRejectedPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void showMsgFromBackgroundThread(final int msgResource) {
        /* Perform UI actions (like display a Toast or press back) need to happen in UI thread
         * https://stackoverflow.com/a/3875204/4235666
         * https://developer.android.com/reference/android/app/Activity.html#runOnUiThread(java.lang.Runnable)
         */
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msgResource, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
