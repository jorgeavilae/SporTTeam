package com.usal.jorgeav.sportapp.events;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.data.calendarevent.MyCalendarEvent;
import com.usal.jorgeav.sportapp.data.calendarevent.MyCalendarEventList;
import com.usal.jorgeav.sportapp.data.calendarevent.MyEventRenderer;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.events.addevent.SelectSportFragment;
import com.usal.jorgeav.sportapp.events.eventrequest.EventRequestsFragment;
import com.usal.jorgeav.sportapp.events.searchevent.SearchEventsFragment;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsFragment extends BaseFragment implements EventsContract.View, EventsAdapter.OnEventItemClickListener, CalendarPickerController {
    private static final String TAG = EventsFragment.class.getSimpleName();

    EventsContract.Presenter mEventsPresenter;

    @BindView(R.id.agenda_calendar_view)
    AgendaCalendarView eventsAgendaCalendarView;
    MyCalendarEventList mEventList;

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mEventsPresenter = new EventsPresenter(this);
        mEventList = new MyCalendarEventList(null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_events_calendar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        hideSoftKeyboard();
        if (item.getItemId() == R.id.action_new_event) {
            Log.d(TAG, "onOptionsItemSelected: New Event");
            Fragment fragment = SelectSportFragment.newInstance();
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        } else if (item.getItemId() == R.id.action_event_requests) {
            Log.d(TAG, "onOptionsItemSelected: Event Requests");
            Fragment fragment = EventRequestsFragment.newInstance();
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        } else if (item.getItemId() == R.id.action_search_events) {
            Log.d(TAG, "onOptionsItemSelected: Search Events");
            Fragment fragment = SearchEventsFragment.newInstance();
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this, root);

        initCalendar();

        return root;
    }

    private void initCalendar() {

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        Long minDateInMillis = mEventList.getMinDateInMillis();
        Long maxDateInMillis = mEventList.getMaxDateInMillis();

        if (minDateInMillis != null) minDate.setTimeInMillis(minDateInMillis);
        minDate.add(Calendar.MONTH, -2);

        if (maxDateInMillis != null) maxDate.setTimeInMillis(maxDateInMillis);
        maxDate.add(Calendar.MONTH, 2);

        Log.d(TAG, "initCalendar: "+minDate);
        Log.d(TAG, "initCalendar: "+maxDate);

        // Init is the only way to pass events to eventsCalendar
        eventsAgendaCalendarView.init(mEventList.getAsCalendarEventList(), minDate, maxDate, Locale.getDefault(), this);
        eventsAgendaCalendarView.addEventRenderer(new MyEventRenderer());
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
        if (mEventList != null)
            mEventList.clear();
    }

    @Override
    public void showMyOwnEvents(Cursor cursor) {
        mEventList.replaceOwnEvents(UtilesContentProvider.cursorToMultipleCalendarEvent(cursor,
                ContextCompat.getColor(getActivity(), R.color.colorLighter)));

        if (cursor != null && cursor.getCount() > 0)
            initCalendar();

        showContent();
    }

    @Override
    public void showParticipatesEvents(Cursor cursor) {
        mEventList.replaceParticipationEvents(UtilesContentProvider.cursorToMultipleCalendarEvent(cursor,
                ContextCompat.getColor(getActivity(), R.color.colorLighter)));

        if (cursor != null && cursor.getCount() > 0)
            initCalendar();

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
        MyCalendarEvent myCalendarEvent = mEventList.getItemAtPosition((int) event.getId());

        Fragment newFragment = DetailEventFragment.newInstance(myCalendarEvent.getEvent_id());
        mFragmentManagementListener.initFragment(newFragment, true);
    }

    @Override
    public void onScrollToDate(Calendar calendar) {
        Log.d(TAG, "onScrollToDate: "+calendar);
    }
}
