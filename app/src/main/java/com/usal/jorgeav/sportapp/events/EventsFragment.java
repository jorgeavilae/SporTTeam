package com.usal.jorgeav.sportapp.events;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.EventsRepository;
import com.usal.jorgeav.sportapp.events.detail.DetailEventFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsFragment extends Fragment implements EventsContract.View, EventsAdapter.OnEventItemClickListener  {
    private static final String TAG = EventsFragment.class.getSimpleName();

    EventsContract.Presenter mEventsPresenter;
    EventsAdapter mEventsRecyclerAdapter;

    @BindView(R.id.events_list)
    RecyclerView eventsRecyclerList;

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mEventsPresenter = new EventsPresenter(new EventsRepository(), this);
        mEventsRecyclerAdapter = new EventsAdapter(null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this, root);

        eventsRecyclerList.setAdapter(mEventsRecyclerAdapter);
        eventsRecyclerList.setHasFixedSize(true);
        eventsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return root;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mEventsPresenter.loadEvents();
    }

    @Override
    public void showEvents(List<Event> events) {
        mEventsRecyclerAdapter.replaceData(events);
    }

    @Override
    public void onEventClick(Event event) {
        Log.d(TAG, "onEventClick");
        Fragment newFragment = DetailEventFragment.newInstance(event);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFrame, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
