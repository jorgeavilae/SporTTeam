package com.usal.jorgeav.sportapp.events;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.events.addevent.NewEventFragment;
import com.usal.jorgeav.sportapp.events.eventrequest.EventRequestsFragment;
import com.usal.jorgeav.sportapp.events.searchevent.SearchEventsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsFragment extends BaseFragment implements EventsContract.View, EventsAdapter.OnEventItemClickListener  {
    private static final String TAG = EventsFragment.class.getSimpleName();

    EventsContract.Presenter mEventsPresenter;

    @BindView(R.id.events_create_button)
    Button eventsCreateButton;
    @BindView(R.id.events_requests_button)
    Button eventsRequestsButton;
    @BindView(R.id.events_search_button)
    Button eventsSearchButton;
    EventsAdapter mMyOwnEventsRecyclerAdapter;
    @BindView(R.id.my_own_events_list)
    RecyclerView myOwnEventsRecyclerList;
    EventsAdapter mEventsParticipationRecyclerAdapter;
    @BindView(R.id.my_events_participation_list)
    RecyclerView eventsParticipationRecyclerList;

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEventsPresenter = new EventsPresenter(this);
        mMyOwnEventsRecyclerAdapter = new EventsAdapter(null, this);
        mEventsParticipationRecyclerAdapter = new EventsAdapter(null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this, root);

        myOwnEventsRecyclerList.setAdapter(mMyOwnEventsRecyclerAdapter);
        myOwnEventsRecyclerList.setHasFixedSize(true);
        myOwnEventsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        eventsParticipationRecyclerList.setAdapter(mEventsParticipationRecyclerAdapter);
        eventsParticipationRecyclerList.setHasFixedSize(true);
        eventsParticipationRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        eventsCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = NewEventFragment.newInstance();
                mFragmentManagementListener.initFragment(fragment, true);
            }
        });
        eventsRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = EventRequestsFragment.newInstance();
                mFragmentManagementListener.initFragment(fragment, true);
            }
        });
        eventsSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = SearchEventsFragment.newInstance();
                mFragmentManagementListener.initFragment(fragment, true);

            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.events), this);
        mActionBarIconManagementListener.setToolbarAsNav();
        mEventsPresenter.loadEvents(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mMyOwnEventsRecyclerAdapter.replaceData(null);
        mEventsParticipationRecyclerAdapter.replaceData(null);
    }

    @Override
    public void showMyOwnEvents(Cursor cursor) {
        mMyOwnEventsRecyclerAdapter.replaceData(cursor);
        if (cursor != null) mFragmentManagementListener.showContent();
    }

    @Override
    public void showParticipatesEvents(Cursor cursor) {
        mEventsParticipationRecyclerAdapter.replaceData(cursor);
        if (cursor != null) mFragmentManagementListener.showContent();
    }

    @Override
    public void onEventClick(String eventId) {
        Fragment newFragment = DetailEventFragment.newInstance(eventId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
