package com.usal.jorgeav.sportapp.events.eventrequest;


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

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventRequestsFragment extends BaseFragment implements EventRequestsContract.View, EventsAdapter.OnEventItemClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = EventRequestsFragment.class.getSimpleName();

    EventRequestsContract.Presenter mEventRequestsPresenter;

    @BindView(R.id.recycler_list)
    RecyclerView eventRequestsList;
    EventsAdapter mEventsRecyclerAdapter;
    @BindView(R.id.list_placeholder)
    ConstraintLayout eventRequestsPlaceholder;

    public EventRequestsFragment() {
        // Required empty public constructor
    }

    public static EventRequestsFragment newInstance() {
        return new EventRequestsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mEventRequestsPresenter = new EventRequestsPresenter(this);
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

        eventRequestsList.setAdapter(mEventsRecyclerAdapter);
        eventRequestsList.setHasFixedSize(true);
        eventRequestsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.action_event_requests), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        mEventRequestsPresenter.loadEventRequests(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mEventsRecyclerAdapter.replaceData(null);
    }

    @Override
    public void showEventRequests(Cursor cursor) {
        mEventsRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            eventRequestsList.setVisibility(View.VISIBLE);
            eventRequestsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            eventRequestsList.setVisibility(View.INVISIBLE);
            eventRequestsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onEventClick(String eventId) {
        Fragment newFragment = DetailEventFragment.newInstance(eventId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
