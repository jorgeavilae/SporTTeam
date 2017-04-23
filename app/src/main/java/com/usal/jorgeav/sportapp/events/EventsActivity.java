package com.usal.jorgeav.sportapp.events;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.EventsRepository;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsActivity extends AppCompatActivity implements EventsContract.View {
    EventsContract.Presenter mEventsPresenter;
    EventsAdapter mEventsRecyclerAdapter;

    @BindView(R.id.events_list)
    RecyclerView eventsRecyclerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        ButterKnife.bind(this);

        mEventsPresenter = new EventsPresenter(new EventsRepository(), this);
        mEventsRecyclerAdapter = new EventsAdapter(null);

        eventsRecyclerList.setAdapter(mEventsRecyclerAdapter);
        eventsRecyclerList.setHasFixedSize(true);
        eventsRecyclerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEventsPresenter.loadEvents();
    }

    @Override
    public void showEvents(List<Event> events) {
        mEventsRecyclerAdapter.replaceData(events);
    }
}
