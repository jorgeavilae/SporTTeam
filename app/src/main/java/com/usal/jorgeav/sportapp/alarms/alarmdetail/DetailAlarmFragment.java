package com.usal.jorgeav.sportapp.alarms.alarmdetail;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.alarms.addalarm.NewAlarmFragment;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.fields.detail.DetailFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.BaseActivity;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailAlarmFragment extends BaseFragment implements DetailAlarmContract.View, EventsAdapter.OnEventItemClickListener {
    private static final String TAG = DetailAlarmFragment.class.getSimpleName();
    public static final String BUNDLE_ALARM_ID = "BUNDLE_ALARM_ID";

    private static String mAlarmId = "";
    private DetailAlarmContract.Presenter mPresenter;

    @BindView(R.id.alarm_detail_id)
    TextView textViewAlarmId;
    @BindView(R.id.alarm_detail_sport)
    TextView textViewAlarmSport;
    @BindView(R.id.alarm_detail_place)
    Button buttonAlarmPlace;
    @BindView(R.id.alarm_detail_date)
    TextView textViewAlarmDate;
    @BindView(R.id.alarm_detail_total)
    TextView textViewAlarmTotal;
    @BindView(R.id.alarm_detail_empty)
    TextView textViewAlarmEmpty;
    @BindView(R.id.alarm_detail_events_coincidence_list)
    RecyclerView eventsCoincidenceList;
    EventsAdapter eventsAdapter;

    public DetailAlarmFragment() {
        // Required empty public constructor
    }

    public static DetailAlarmFragment newInstance(@NonNull String alarmId) {
        DetailAlarmFragment fragment = new DetailAlarmFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_ALARM_ID, alarmId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPresenter = new DetailAlarmPresenter(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_edit_delete, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_edit) {
            Log.d(TAG, "onOptionsItemSelected: Edit");
            Fragment fragment = NewAlarmFragment.newInstance(mAlarmId);
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            Log.d(TAG, "onOptionsItemSelected: Delete");
            mPresenter.deleteAlarm(getArguments());
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detail_alarm, container, false);
        ButterKnife.bind(this, root);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_ALARM_ID))
            mAlarmId = getArguments().getString(BUNDLE_ALARM_ID);

        eventsAdapter = new EventsAdapter(null, null);
        eventsCoincidenceList.setAdapter(eventsAdapter);
        eventsCoincidenceList.setHasFixedSize(true);
        eventsCoincidenceList.setLayoutManager(new LinearLayoutManager(getActivityContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(mAlarmId, this);
        mActionBarIconManagementListener.setToolbarAsUp();
        mPresenter.openAlarm(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        eventsAdapter.replaceData(null);
    }

    @Override
    public void showAlarmId(String id) {
        if (id != null) {
            ((BaseActivity) getActivity()).showContent();
            this.textViewAlarmId.setText(id);
        }
    }

    @Override
    public void showAlarmSport(String sport) {
        if (sport != null) {
            ((BaseActivity) getActivity()).showContent();
            this.textViewAlarmSport.setText(sport);
        }

    }

    @Override
    public void showAlarmPlace(final String place, final String sport) {
        if (place != null && sport != null) {
            ((BaseActivity) getActivity()).showContent();
            this.buttonAlarmPlace.setText(place);
            buttonAlarmPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Fragment newFragment = DetailFieldFragment.newInstance(place, sport);
                mFragmentManagementListener.initFragment(newFragment, true);
                }
            });
        }
    }

    @Override
    public void showAlarmDate(Long dateFrom, Long dateTo) {
        if (dateFrom != null && dateFrom > 0) {
            ((BaseActivity) getActivity()).showContent();
            this.textViewAlarmDate.setText(UtilesTime.millisToDateString(dateFrom));
        }
        if (dateTo != null && dateTo > 0)
            this.textViewAlarmDate.setText(
                    this.textViewAlarmDate.getText() + " - " + UtilesTime.millisToDateString(dateTo));
    }

    @Override
    public void showAlarmTotalPlayers(Long totalPlayersFrom, Long totalPlayersTo) {
        if (totalPlayersFrom != null && totalPlayersFrom > -1) {
            ((BaseActivity) getActivity()).showContent();
            this.textViewAlarmTotal.setText(String.format(Locale.getDefault(),
                    "%2d", totalPlayersFrom));
        }
        if (totalPlayersTo != null && totalPlayersTo > -1) {
            this.textViewAlarmTotal.setText(String.format(Locale.getDefault(),
                    this.textViewAlarmTotal.getText() + " - %2d", totalPlayersTo));
        }
    }

    @Override
    public void showAlarmEmptyPlayers(Long emptyPlayersFrom, Long emptyPlayersTo) {
        if (emptyPlayersFrom != null && emptyPlayersFrom > -1) {
            ((BaseActivity) getActivity()).showContent();
            this.textViewAlarmEmpty.setText(String.format(Locale.getDefault(),
                    "%2d", emptyPlayersFrom));
        }
        if (emptyPlayersTo != null && emptyPlayersTo > -1) {
            this.textViewAlarmEmpty.setText(String.format(Locale.getDefault(),
                    this.textViewAlarmEmpty.getText() + " - %2d", emptyPlayersTo));
        }
    }

    @Override
    public void showEvents(Cursor data) {
        eventsAdapter.replaceData(data);
    }

    @Override
    public void onEventClick(String eventId) {
        Fragment newFragment = DetailEventFragment.newInstance(eventId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
