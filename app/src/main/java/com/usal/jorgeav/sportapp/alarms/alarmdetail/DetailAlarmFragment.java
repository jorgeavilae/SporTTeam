package com.usal.jorgeav.sportapp.alarms.alarmdetail;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.ActivityContracts;
import com.usal.jorgeav.sportapp.MainActivity;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.fields.detail.DetailFieldFragment;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailAlarmFragment extends Fragment implements DetailAlarmContract.View, EventsAdapter.OnEventItemClickListener {
    private static final String TAG = DetailAlarmFragment.class.getSimpleName();
    public static final String BUNDLE_ALARM_ID = "BUNDLE_ALARM_ID";

    private static String mAlarmId = "";
    private DetailAlarmContract.Presenter mPresenter;
    private ActivityContracts.ActionBarIconManagement mActionBarIconManagementListener;
    private ActivityContracts.FragmentManagement mFragmentManagementListener;

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

        mPresenter = new DetailAlarmPresenter(this);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActivityContracts.FragmentManagement)
            mFragmentManagementListener = (ActivityContracts.FragmentManagement) context;
        if (context instanceof ActivityContracts.ActionBarIconManagement)
            mActionBarIconManagementListener = (ActivityContracts.ActionBarIconManagement) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentManagementListener = null;
        mActionBarIconManagementListener = null;
    }

    @Override
    public void showAlarmId(String id) {
        if (id != null) {
            ((MainActivity) getActivity()).showContent();
            this.textViewAlarmId.setText(id);
        }
    }

    @Override
    public void showAlarmSport(String sport) {
        if (sport != null) {
            ((MainActivity) getActivity()).showContent();
            this.textViewAlarmSport.setText(sport);
        }

    }

    @Override
    public void showAlarmPlace(String place) {
        if (place != null) {
            ((MainActivity) getActivity()).showContent();
            this.buttonAlarmPlace.setText(place);
            buttonAlarmPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Cursor c = getActivity().getContentResolver().query(
                            SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                            SportteamContract.FieldEntry.FIELDS_COLUMNS,
                            SportteamContract.FieldEntry.FIELD_ID + " = ? AND " + SportteamContract.FieldEntry.SPORT + " = ? ",
                            new String[]{buttonAlarmPlace.getText().toString(), textViewAlarmSport.getText().toString()},
                            null);
                    if (c != null && c.moveToFirst()) {
                        String fieldId = c.getString(SportteamContract.FieldEntry.COLUMN_FIELD_ID);
                        String sportId = c.getString(SportteamContract.FieldEntry.COLUMN_SPORT);
                        Fragment newFragment = DetailFieldFragment.newInstance(fieldId, sportId);
                        mFragmentManagementListener.initFragment(newFragment, true);
                        c.close();
                    }
                }
            });
        }
    }

    @Override
    public void showAlarmDate(Long dateFrom, Long dateTo) {
        if (dateFrom != null && dateFrom > 0) {
            ((MainActivity) getActivity()).showContent();
            this.textViewAlarmDate.setText(
                    UtilesTime.millisToDateTimeString(dateFrom));
        }
        if (dateTo != null && dateTo > 0)
            this.textViewAlarmDate.setText(
                    this.textViewAlarmDate.getText() + " - " + UtilesTime.millisToDateTimeString(dateTo));
    }

    @Override
    public void showAlarmTotalPlayers(Long totalPlayersFrom, Long totalPlayersTo) {
        if (totalPlayersFrom != null && totalPlayersFrom > -1) {
            ((MainActivity) getActivity()).showContent();
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
            ((MainActivity) getActivity()).showContent();
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
    public FragmentActivity getActivityContext() {
        return getActivity();
    }

    @Override
    public Fragment getThis() {
        return this;
    }

    @Override
    public void onEventClick(String eventId) {
        Fragment newFragment = DetailEventFragment.newInstance(eventId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
