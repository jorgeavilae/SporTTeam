package com.usal.jorgeav.sportapp.events;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usal.jorgeav.sportapp.MainActivityContract;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.events.detail.DetailEventFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsFragment extends Fragment implements EventsContract.View, EventsAdapter.OnEventItemClickListener  {
    private static final String TAG = EventsFragment.class.getSimpleName();
    public static final int LOADER_EVENTS_ID = 2000;

    EventsContract.Presenter mEventsPresenter;
    private MainActivityContract.FragmentManagement mFragmentManagementListener;
    private MainActivityContract.ActionBarIconManagement mActionBarIconManagementListener;

    EventsAdapter mMyOwnEventsRecyclerAdapter;
    @BindView(R.id.my_own_events_list)
    RecyclerView myOwnEventsRecyclerList;
    //TODO add another list & adapter for myAssistantsEvents

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this, root);

        myOwnEventsRecyclerList.setAdapter(mMyOwnEventsRecyclerAdapter);
        myOwnEventsRecyclerList.setHasFixedSize(true);
        myOwnEventsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.events), this);
        mActionBarIconManagementListener.setToolbarAsNav();
    }

    @Override
    public void onResume() {
        super.onResume();
//        mEventsPresenter.loadEvents();
        getLoaderManager().initLoader(LOADER_EVENTS_ID, null, mEventsPresenter.getLoaderInstance());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivityContract.FragmentManagement)
            mFragmentManagementListener = (MainActivityContract.FragmentManagement) context;
        if (context instanceof MainActivityContract.ActionBarIconManagement)
            mActionBarIconManagementListener = (MainActivityContract.ActionBarIconManagement) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentManagementListener = null;
        mActionBarIconManagementListener = null;
    }

    @Override
    public void showMyOwnEvents(Cursor cursor) {
        mMyOwnEventsRecyclerAdapter.replaceData(cursor);
        if (cursor != null) mFragmentManagementListener.showContent();
    }

    @Override
    public void showMyOtherEvents(Cursor cursor) {
        //TODO
//        mMyOwnEventsRecyclerAdapter.replaceData(cursor);
//        if (cursor != null) mFragmentManagementListener.showContent();
    }

    @Override
    public void onEventClick(Event event) {
        Fragment newFragment = DetailEventFragment.newInstance(event);
        mFragmentManagementListener.initFragment(newFragment, true);
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}
