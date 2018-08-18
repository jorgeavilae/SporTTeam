package com.usal.jorgeav.sportapp.mainactivities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.searchevent.EventsMapFragment;
import com.usal.jorgeav.sportapp.searchevent.advancedsearch.SearchEventsFragment;

public class SearchEventsActivity extends BaseActivity implements SearchEventsFragment.OnSearchEventFilter {
    public static final String TAG = SearchEventsActivity.class.getSimpleName();

    public static final String INSTANCE_SPORTID_SELECTED = "INSTANCE_SPORTID_SELECTED";
    public String mSportId;
    public static final String INSTANCE_DATE_FROM_SELECTED = "INSTANCE_DATE_FROM_SELECTED";
    public Long mDateFrom;
    public static final String INSTANCE_DATE_TO_SELECTED = "INSTANCE_DATE_TO_SELECTED";
    public Long mDateTo;
    public static final String INSTANCE_TOTAL_FROM_SELECTED = "INSTANCE_TOTAL_FROM_SELECTED";
    public Integer mTotalFrom;
    public static final String INSTANCE_TOTAL_TO_SELECTED = "INSTANCE_TOTAL_TO_SELECTED";
    public Integer mTotalTo;
    public static final String INSTANCE_EMPTY_FROM_SELECTED = "INSTANCE_EMPTY_FROM_SELECTED";
    public Integer mEmptyFrom;
    public static final String INSTANCE_EMPTY_TO_SELECTED = "INSTANCE_EMPTY_TO_SELECTED";
    public Integer mEmptyTo;

    @Override
    public void startMainFragment() {
        initFragment(EventsMapFragment.newInstance(), false);

        mNavigationView.setCheckedItem(R.id.nav_search_events);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_search_events && super.onNavigationItemSelected(item);
    }

    @Override
    public void onFilterSet(String sportId, Long dateFrom, Long dateTo, int totalFrom,
                            int totalTo, int emptyFrom, int emptyTo) {
        Log.d(TAG, "onFilterSet: "+sportId);
        mSportId = sportId;
        Log.d(TAG, "onFilterSet: "+dateFrom);
        mDateFrom = dateFrom;
        Log.d(TAG, "onFilterSet: "+dateTo);
        mDateTo = dateTo;
        Log.d(TAG, "onFilterSet: "+totalFrom);
        mTotalFrom = totalFrom;
        Log.d(TAG, "onFilterSet: "+totalTo);
        mTotalTo = totalTo;
        Log.d(TAG, "onFilterSet: "+emptyFrom);
        mEmptyFrom = emptyFrom;
        Log.d(TAG, "onFilterSet: "+emptyTo);
        mEmptyTo = emptyTo;

        onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSportId != null)
            outState.putString(INSTANCE_SPORTID_SELECTED, mSportId);

        if (mDateFrom != null)
            outState.putLong(INSTANCE_DATE_FROM_SELECTED, mDateFrom);
        if (mDateTo != null)
            outState.putLong(INSTANCE_DATE_TO_SELECTED, mDateTo);

        if (mTotalFrom != null)
            outState.putInt(INSTANCE_TOTAL_FROM_SELECTED, mTotalFrom);
        if (mTotalTo != null)
            outState.putInt(INSTANCE_TOTAL_TO_SELECTED, mTotalTo);

        if (mEmptyFrom != null)
            outState.putInt(INSTANCE_EMPTY_FROM_SELECTED, mEmptyFrom);
        if (mEmptyTo != null)
            outState.putInt(INSTANCE_EMPTY_TO_SELECTED, mEmptyTo);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_SPORTID_SELECTED))
            mSportId = savedInstanceState.getString(INSTANCE_SPORTID_SELECTED);

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_DATE_FROM_SELECTED))
            mDateFrom = savedInstanceState.getLong(INSTANCE_DATE_FROM_SELECTED);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_DATE_TO_SELECTED))
            mDateTo = savedInstanceState.getLong(INSTANCE_DATE_TO_SELECTED);

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TOTAL_FROM_SELECTED))
            mTotalFrom = savedInstanceState.getInt(INSTANCE_TOTAL_FROM_SELECTED);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TOTAL_TO_SELECTED))
            mTotalTo = savedInstanceState.getInt(INSTANCE_TOTAL_TO_SELECTED);

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_EMPTY_FROM_SELECTED))
            mEmptyFrom = savedInstanceState.getInt(INSTANCE_EMPTY_FROM_SELECTED);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_EMPTY_TO_SELECTED))
            mEmptyTo = savedInstanceState.getInt(INSTANCE_EMPTY_TO_SELECTED);
    }
}
