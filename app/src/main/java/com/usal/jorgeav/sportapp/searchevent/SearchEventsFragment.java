package com.usal.jorgeav.sportapp.searchevent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.adapters.SportSpinnerAdapter;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchEventsFragment extends BaseFragment implements SearchEventsContract.View,
        EventsAdapter.OnEventItemClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = SearchEventsFragment.class.getSimpleName();
    public static final String BUNDLE_SPORT = "BUNDLE_SPORT";

    String mSportIdSelected = "";
    SearchEventsContract.Presenter mSearchEventsPresenter;

    @BindView(R.id.search_events_button)
    Button searchEventsButton;
    @BindView(R.id.search_events_icon)
    ImageView searchEventsIcon;
    @BindView(R.id.search_events_sport)
    TextView searchEventsSportName;

    @BindView(R.id.search_events_list)
    RecyclerView searchEventsList;
    EventsAdapter mEventsRecyclerAdapter;
    @BindView(R.id.search_events_placeholder)
    ConstraintLayout searchEventsPlaceholder;

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
        mEventsRecyclerAdapter = new EventsAdapter(null, this, Glide.with(this));
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
            unsetSportSearched();
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
                createPickSportDialog();
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SPORT))
            setSportSearched(savedInstanceState.getString(BUNDLE_SPORT));

        return root;
    }

    private void createPickSportDialog() {
        ArrayList<String> sportsResources = new ArrayList<>(
                Arrays.asList(getResources().getStringArray(R.array.sport_id_values)));
        final SportSpinnerAdapter listAdapter = new SportSpinnerAdapter(getActivityContext(),
                R.layout.sport_spinner_item, sportsResources);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_sport);
        builder.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String sportId = (String) listAdapter.getItem(i);

                setSportSearched(sportId);
                mEventsRecyclerAdapter.replaceData(null);
                Bundle b = new Bundle();
                b.putString(BUNDLE_SPORT, sportId);
                mSearchEventsPresenter.loadNearbyEventsWithSport(getLoaderManager(), b);
            }
        });
        builder.create().show();
    }

    private void setSportSearched(String sportId) {
        mSportIdSelected = sportId;

        int sportStringResource = getResources()
                .getIdentifier(sportId, "string", getActivityContext().getPackageName());
        searchEventsSportName.setVisibility(View.VISIBLE);
        searchEventsSportName.setText(getString(sportStringResource));

        int sportDrawableResource = Utiles.getSportIconFromResource(sportId);
        searchEventsIcon.setVisibility(View.VISIBLE);
        Glide.with(this).load(sportDrawableResource).into(searchEventsIcon);
    }

    private void unsetSportSearched() {
        mSportIdSelected = "";

        searchEventsSportName.setVisibility(View.INVISIBLE);
        searchEventsIcon.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.search_events), this);
        mActionBarIconManagementListener.setToolbarAsNav();
    }

    @Override
    public void onStart() {
        super.onStart();
        mSearchEventsPresenter.loadNearbyEvents(getLoaderManager(), getArguments());
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mSportIdSelected))
            outState.putString(BUNDLE_SPORT, mSportIdSelected);
    }
}