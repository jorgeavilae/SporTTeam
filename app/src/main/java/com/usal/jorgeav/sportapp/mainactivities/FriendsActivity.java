package com.usal.jorgeav.sportapp.mainactivities;

import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.friends.FriendsFragment;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class FriendsActivity extends BaseActivity {

    @Override
    public void startMainFragment() {
        super.startMainFragment();

        initFragment(FriendsFragment.newInstance(), false);
        mNavigationView.setCheckedItem(R.id.nav_friends);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_friends && super.onNavigationItemSelected(item);
    }
}
