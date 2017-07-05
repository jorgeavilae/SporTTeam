package com.usal.jorgeav.sportapp.mainactivities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.events.EventsFragment;
import com.usal.jorgeav.sportapp.events.addevent.selectfield.SelectFieldFragment;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class EventsActivity extends BaseActivity implements SelectFieldFragment.OnFieldSelected {
    private final static String TAG = EventsActivity.class.getSimpleName();

    private static final String INSTANCE_NEW_EVENT_FIELD = "INSTANCE_NEW_EVENT_FIELD";
    public String newEventFieldSelected = null;
    private static final String INSTANCE_NEW_EVENT_CITY = "INSTANCE_NEW_EVENT_CITY";
    public String newEventCitySelected = null;

    @Override
    public void startMainFragment() {
        super.startMainFragment();

        initFragment(EventsFragment.newInstance(), false);
        mNavigationView.setCheckedItem(R.id.nav_events);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_events && super.onNavigationItemSelected(item);
    }

    @Override
    public void retrieveFieldSelected(String fieldId) {
        newEventFieldSelected = fieldId;
    }
    public void setCity(String city) {
        newEventCitySelected = city;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (newEventFieldSelected != null)
            outState.putString(INSTANCE_NEW_EVENT_FIELD, newEventFieldSelected);
        if (newEventCitySelected != null)
            outState.putString(INSTANCE_NEW_EVENT_CITY, newEventCitySelected);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NEW_EVENT_FIELD))
            newEventFieldSelected = savedInstanceState.getString(INSTANCE_NEW_EVENT_FIELD);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NEW_EVENT_CITY))
            newEventCitySelected = savedInstanceState.getString(INSTANCE_NEW_EVENT_CITY);
    }
}
