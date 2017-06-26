package com.usal.jorgeav.sportapp.mainactivities;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.alarms.AlarmsFragment;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class AlarmsActivity extends BaseActivity {

    @Override
    public void startMainFragment() {
        super.startMainFragment();

        initFragment(AlarmsFragment.newInstance(), false);
        mNavigationView.setCheckedItem(R.id.nav_alarms);
    }
}
