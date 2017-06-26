package com.usal.jorgeav.sportapp.mainactivities;

import android.os.Bundle;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.alarms.AlarmsFragment;
import com.usal.jorgeav.sportapp.events.addevent.selectfield.SelectFieldFragment;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class AlarmsActivity extends BaseActivity implements SelectFieldFragment.OnFieldSelected {

    private static final String INSTANCE_NEW_ALARM_FIELD = "INSTANCE_NEW_ALARM_FIELD";
    public String newAlarmFieldSelected = null;

    @Override
    public void startMainFragment() {
        super.startMainFragment();

        initFragment(AlarmsFragment.newInstance(), false);
        mNavigationView.setCheckedItem(R.id.nav_alarms);
    }

    @Override
    public void retrieveFieldSelected(String fieldId) {
        newAlarmFieldSelected = fieldId;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (newAlarmFieldSelected != null)
            outState.putString(INSTANCE_NEW_ALARM_FIELD, newAlarmFieldSelected);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NEW_ALARM_FIELD))
            newAlarmFieldSelected = savedInstanceState.getString(INSTANCE_NEW_ALARM_FIELD);
    }
}
