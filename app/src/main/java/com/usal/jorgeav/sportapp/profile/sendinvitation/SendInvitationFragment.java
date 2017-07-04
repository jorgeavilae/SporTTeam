package com.usal.jorgeav.sportapp.profile.sendinvitation;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public class SendInvitationFragment extends BaseFragment implements SendInvitationContract.View, EventsAdapter.OnEventItemClickListener {
    private static final String TAG = SendInvitationFragment.class.getSimpleName();

    public static final String BUNDLE_INSTANCE_UID = "BUNDLE_INSTANCE_UID";
    SendInvitationContract.Presenter mSendInvitationPresenter;
    EventsAdapter mEventsRecyclerAdapter;

    @BindView(R.id.recycler_list)
    RecyclerView sendInvitationList;
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
        mEventsRecyclerAdapter = new EventsAdapter(null, this);
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

        sendInvitationList.setAdapter(mEventsRecyclerAdapter);
        sendInvitationList.setHasFixedSize(true);
        sendInvitationList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Selecciona evento", this);
        mActionBarIconManagementListener.setToolbarAsUp();
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
    public void onEventClick(String eventId) {
        Fragment newFragment = DetailEventFragment.newInstance(eventId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
