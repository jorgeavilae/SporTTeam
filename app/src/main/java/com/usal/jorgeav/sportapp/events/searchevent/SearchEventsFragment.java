package com.usal.jorgeav.sportapp.events.searchevent;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class SearchEventsFragment extends BaseFragment implements SearchEventsContract.View,
        EventsAdapter.OnEventItemClickListener, SportDialog.SportDialogListener {
    private static final String TAG = SearchEventsFragment.class.getSimpleName();
    public static final String BUNDLE_SPORT = "BUNDLE_SPORT";

    private SearchEventsFragment mThis;
    SearchEventsContract.Presenter mSearchEventsPresenter;
    EventsAdapter mEventsRecyclerAdapter;

    @BindView(R.id.search_events_list)
    RecyclerView searchEventsList;
    @BindView(R.id.search_events_placeholder)
    ConstraintLayout searchEventsPlaceholder;
    @BindView(R.id.search_events_button)
    Button searchEventsButton;

    public SearchEventsFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new SearchEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mSearchEventsPresenter = new SearchEventsPresenter(this);
        mEventsRecyclerAdapter = new EventsAdapter(null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_filters, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_clear_filter) {
            Log.d(TAG, "onOptionsItemSelected: Clear Filter");
            mSearchEventsPresenter.loadNearbyEvents(getLoaderManager(), getArguments());
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search_events, container, false);
        ButterKnife.bind(this, root);

        searchEventsList.setAdapter(mEventsRecyclerAdapter);
        searchEventsList.setHasFixedSize(true);
        searchEventsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        searchEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SportDialog dialog = new SportDialog();
                dialog.setTargetFragment(mThis, 1);
                dialog.show(getActivity().getSupportFragmentManager(), null);
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Buscar eventos", this);
        mActionBarIconManagementListener.setToolbarAsUp();
        mSearchEventsPresenter.loadNearbyEvents(getLoaderManager(), getArguments());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mThis = this;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mThis = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mEventsRecyclerAdapter.replaceData(null);
    }

    @Override
    public void showEvents(Cursor cursor) {
        mEventsRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            searchEventsList.setVisibility(View.VISIBLE);
            searchEventsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            searchEventsList.setVisibility(View.INVISIBLE);
            searchEventsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onEventClick(String eventId) {
        Fragment newFragment = DetailEventFragment.newInstance(eventId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }

    @Override
    public void onDialogSportClick(String sportId) {
        mEventsRecyclerAdapter.replaceData(null);
        Bundle b = new Bundle();
        b.putString(BUNDLE_SPORT, sportId);
        mSearchEventsPresenter.loadNearbyEventsWithSport(getLoaderManager(), b);
    }
}