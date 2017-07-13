package com.usal.jorgeav.sportapp.mainactivities;

import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.notifications.NotificationsFragment;

/**
 * Created by Jorge Avila on 13/07/2017.
 */

public class NotificationsActivity extends BaseActivity {

    @Override
    public void startMainFragment() {
        super.startMainFragment();

        initFragment(NotificationsFragment.newInstance(), false);
        mNavigationView.setCheckedItem(R.id.nav_notifications);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_notifications && super.onNavigationItemSelected(item);
    }
}
