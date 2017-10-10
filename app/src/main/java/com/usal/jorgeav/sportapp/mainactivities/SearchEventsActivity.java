package com.usal.jorgeav.sportapp.mainactivities;

import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.searchevent.SearchEventsMapFragment;

public class SearchEventsActivity extends BaseActivity {
    public static final String TAG = SearchEventsActivity.class.getSimpleName();

    @Override
    public void startMainFragment() {
        super.startMainFragment();

        initFragment(SearchEventsMapFragment.newInstance(), false);

        mNavigationView.setCheckedItem(R.id.nav_search_events);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_search_events && super.onNavigationItemSelected(item);
    }
}
