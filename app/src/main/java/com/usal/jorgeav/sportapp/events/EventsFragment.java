package com.usal.jorgeav.sportapp.events;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.events.addevent.SelectSportFragment;
import com.usal.jorgeav.sportapp.events.eventrequest.EventRequestsFragment;
import com.usal.jorgeav.sportapp.events.searchevent.SearchEventsFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsFragment extends BaseFragment implements EventsContract.View, EventsAdapter.OnEventItemClickListener, CalendarPickerController {
    private static final String TAG = EventsFragment.class.getSimpleName();

    EventsContract.Presenter mEventsPresenter;

    @BindView(R.id.events_create_button)
    Button eventsCreateButton;
    @BindView(R.id.events_requests_button)
    Button eventsRequestsButton;
    @BindView(R.id.events_search_button)
    Button eventsSearchButton;
    @BindView(R.id.agenda_calendar_view)
    AgendaCalendarView eventsAgendaCalendarView;
//    EventsAdapter mMyOwnEventsRecyclerAdapter;
//    @BindView(R.id.my_own_events_list)
//    RecyclerView myOwnEventsRecyclerList;
//    @BindView(R.id.my_own_events_placeholder)
//    ConstraintLayout myOwnEventsPlaceholder;
//    EventsAdapter mEventsParticipationRecyclerAdapter;
//    @BindView(R.id.my_events_participation_list)
//    RecyclerView eventsParticipationRecyclerList;
//    @BindView(R.id.my_events_participation_placeholder)
//    ConstraintLayout eventsParticipationPlaceholder;

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
//        mMyOwnEventsRecyclerAdapter = new EventsAdapter(null, this);
//        mEventsParticipationRecyclerAdapter = new EventsAdapter(null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this, root);

        initCalendar();
//        myOwnEventsRecyclerList.setAdapter(mMyOwnEventsRecyclerAdapter);
//        myOwnEventsRecyclerList.setHasFixedSize(true);
//        myOwnEventsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//
//        eventsParticipationRecyclerList.setAdapter(mEventsParticipationRecyclerAdapter);
//        eventsParticipationRecyclerList.setHasFixedSize(true);
//        eventsParticipationRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        eventsCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = SelectSportFragment.newInstance();
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

    private void initCalendar() {

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);

        List<CalendarEvent> eventList = new ArrayList<>();
        mockList(eventList);

        eventsAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
    }
    private void mockList(List<CalendarEvent> eventList) {
        Calendar startTime1 = Calendar.getInstance();
        Calendar endTime1 = Calendar.getInstance();
        endTime1.add(Calendar.MONTH, 1);
        BaseCalendarEvent event1 = new BaseCalendarEvent("Thibault travels in Iceland", "A wonderful journey!", "Iceland",
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark), startTime1, endTime1, true);
        eventList.add(event1);

        Calendar startTime2 = Calendar.getInstance();
        startTime2.add(Calendar.DAY_OF_YEAR, 1);
        Calendar endTime2 = Calendar.getInstance();
        endTime2.add(Calendar.DAY_OF_YEAR, 3);
        BaseCalendarEvent event2 = new BaseCalendarEvent("Visit to Dalvík", "A beautiful small town", "Dalvík",
                ContextCompat.getColor(getActivity(), R.color.colorAccent), startTime2, endTime2, true);
        eventList.add(event2);

        // Example on how to provide your own layout
        Calendar startTime3 = Calendar.getInstance();
        Calendar endTime3 = Calendar.getInstance();
        startTime3.set(Calendar.HOUR_OF_DAY, 14);
        startTime3.set(Calendar.MINUTE, 0);
        endTime3.set(Calendar.HOUR_OF_DAY, 15);
        endTime3.set(Calendar.MINUTE, 0);
        DrawableCalendarEvent event3 = new DrawableCalendarEvent("Visit of Harpa", "", "Dalvík",
                ContextCompat.getColor(getActivity(), R.color.colorAccent), startTime3, endTime3, false, R.layout.events_item_list);
        eventList.add(event3);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.events), this);
        mActionBarIconManagementListener.setToolbarAsNav();
    }

    @Override
    public void onStart() {
        super.onStart();
        mEventsPresenter.loadEvents(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
//        mMyOwnEventsRecyclerAdapter.replaceData(null);
//        mEventsParticipationRecyclerAdapter.replaceData(null);
    }

    @Override
    public void showMyOwnEvents(Cursor cursor) {
//        mMyOwnEventsRecyclerAdapter.replaceData(cursor);
//        if (cursor != null && cursor.getCount() > 0) {
//            myOwnEventsRecyclerList.setVisibility(View.VISIBLE);
//            myOwnEventsPlaceholder.setVisibility(View.INVISIBLE);
//        } else {
//            myOwnEventsRecyclerList.setVisibility(View.INVISIBLE);
//            myOwnEventsPlaceholder.setVisibility(View.VISIBLE);
//        }
        showContent();
    }

    @Override
    public void showParticipatesEvents(Cursor cursor) {
//        mEventsParticipationRecyclerAdapter.replaceData(cursor);
//        if (cursor != null && cursor.getCount() > 0) {
//            eventsParticipationRecyclerList.setVisibility(View.VISIBLE);
//            eventsParticipationPlaceholder.setVisibility(View.INVISIBLE);
//        } else {
//            eventsParticipationRecyclerList.setVisibility(View.INVISIBLE);
//            eventsParticipationPlaceholder.setVisibility(View.VISIBLE);
//        }
        showContent();
    }

    @Override
    public void onEventClick(String eventId) {
        Fragment newFragment = DetailEventFragment.newInstance(eventId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }

    @Override
    public void onDaySelected(DayItem dayItem) {
        Log.d(TAG, "onDaySelected: "+dayItem);
    }

    @Override
    public void onEventSelected(CalendarEvent event) {
        Log.d(TAG, "onEventSelected: "+event);
    }

    @Override
    public void onScrollToDate(Calendar calendar) {
        Log.d(TAG, "onScrollToDate: "+calendar);
    }
}
