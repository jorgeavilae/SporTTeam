package com.usal.jorgeav.sportapp.events.detail;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Event;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailEventFragment extends Fragment implements DetailEventContract.View {
    private static final String TAG = DetailEventFragment.class.getSimpleName();

    private static final String ARG_EVENT = "param-event";

    private Event mEvent = null;
    private DetailEventContract.Presenter mPresenter;

    @BindView(R.id.event_detail_id)
    TextView textViewEventId;
    @BindView(R.id.event_detail_sport)
    TextView textViewEventSport;
    @BindView(R.id.event_detail_place)
    TextView textViewEventPlace;
    @BindView(R.id.event_detail_date)
    TextView textViewEventDate;
    @BindView(R.id.event_detail_time)
    TextView textViewEventTime;
    @BindView(R.id.event_detail_total)
    TextView textViewEventTotal;
    @BindView(R.id.event_detail_empty)
    TextView textViewEventEmpty;

    public DetailEventFragment() {
        // Required empty public constructor
    }

    public static DetailEventFragment newInstance(Event event) {
        Log.d(TAG, "newInstance");
        DetailEventFragment fragment = new DetailEventFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEvent = getArguments().getParcelable(ARG_EVENT);
        }

        mPresenter = new DetailEventPresenter(mEvent, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_detail_event, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mPresenter.openEvent();
    }

    @Override
    public void showEventId(String id) {
        this.textViewEventId.setText(id);
    }

    @Override
    public void showEventSport(String sport) {
        this.textViewEventSport.setText(sport);

    }

    @Override
    public void showEventPlace(String place) {
        this.textViewEventPlace.setText(place);

    }

    @Override
    public void showEventDate(String date) {
        this.textViewEventDate.setText(date);

    }

    @Override
    public void showEventTime(String time) {
        this.textViewEventTime.setText(time);

    }

    @Override
    public void showEventTotalPlayers(int totalPlayers) {
        this.textViewEventTotal.setText(String.format(Locale.getDefault(), "%2d", totalPlayers));

    }

    @Override
    public void showEventEmptyPlayers(int emptyPlayers) {
        this.textViewEventEmpty.setText(String.format(Locale.getDefault(), "%2d", emptyPlayers));

    }
}
