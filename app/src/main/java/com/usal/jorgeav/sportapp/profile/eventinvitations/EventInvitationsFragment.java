package com.usal.jorgeav.sportapp.profile.eventinvitations;


import android.database.Cursor;
import android.os.Bundle;
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

public class EventInvitationsFragment extends BaseFragment implements EventInvitationsContract.View, EventsAdapter.OnEventItemClickListener {
    private static final String TAG = EventInvitationsFragment.class.getSimpleName();

    EventInvitationsContract.Presenter mEventInvitationsPresenter;
    EventsAdapter mEventsRecyclerAdapter;

    @BindView(R.id.recycler_list)
    RecyclerView eventInvitationsList;
    @BindView(R.id.list_placeholder)
    ConstraintLayout eventInvitationsPlaceholder;

    public EventInvitationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mEventInvitationsPresenter = new EventInvitationsPresenter(this);
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

        eventInvitationsList.setAdapter(mEventsRecyclerAdapter);
        eventInvitationsList.setHasFixedSize(true);
        eventInvitationsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Invitaciones recibidas", this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        mEventInvitationsPresenter.loadEventInvitations(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mEventsRecyclerAdapter.replaceData(null);
    }

    @Override
    public void showEventInvitations(Cursor cursor) {
        mEventsRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            eventInvitationsList.setVisibility(View.VISIBLE);
            eventInvitationsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            eventInvitationsList.setVisibility(View.INVISIBLE);
            eventInvitationsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onEventClick(String eventId) {
        Fragment newFragment = DetailEventFragment.newInstance(eventId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
