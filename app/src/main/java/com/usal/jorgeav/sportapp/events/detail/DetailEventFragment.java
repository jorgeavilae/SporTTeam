package com.usal.jorgeav.sportapp.events.detail;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usal.jorgeav.sportapp.MainActivity;
import com.usal.jorgeav.sportapp.MainActivityContract;
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
    private MainActivityContract.ActionBarIconManagement mActionBarIconManagementListener;
    private MainActivityContract.FragmentManagement mFragmentManagementListener;

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
        DetailEventFragment fragment = new DetailEventFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEvent = getArguments().getParcelable(ARG_EVENT);
        }

        mPresenter = new DetailEventPresenter(mEvent, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detail_event, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //TODO should I add event.name?
        mFragmentManagementListener.setCurrentDisplayedFragment(mEvent.getmId(), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActionBarIconManagementListener.setToolbarAsNav();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivityContract.ActionBarIconManagement) {
            mActionBarIconManagementListener = (MainActivityContract.ActionBarIconManagement) context;
            mFragmentManagementListener = (MainActivityContract.FragmentManagement) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActionBarIconManagementListener = null;
        mFragmentManagementListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.openEvent();
    }

    @Override
    public void showEventId(String id) {
        ((MainActivity)getActivity()).showContent();
        this.textViewEventId.setText(id);
    }

    @Override
    public void showEventSport(String sport) {
        ((MainActivity)getActivity()).showContent();
        this.textViewEventSport.setText(sport);

    }

    @Override
    public void showEventPlace(String place) {
        ((MainActivity)getActivity()).showContent();
        this.textViewEventPlace.setText(place);

    }

    @Override
    public void showEventDate(String date) {
        ((MainActivity)getActivity()).showContent();
        this.textViewEventDate.setText(date);

    }

    @Override
    public void showEventTime(String time) {
        ((MainActivity)getActivity()).showContent();
        this.textViewEventTime.setText(time);

    }

    @Override
    public void showEventTotalPlayers(int totalPlayers) {
        ((MainActivity)getActivity()).showContent();
        this.textViewEventTotal.setText(String.format(Locale.getDefault(), "%2d", totalPlayers));

    }

    @Override
    public void showEventEmptyPlayers(int emptyPlayers) {
        ((MainActivity)getActivity()).showContent();
        this.textViewEventEmpty.setText(String.format(Locale.getDefault(), "%2d", emptyPlayers));

    }
}
