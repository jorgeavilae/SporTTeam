package com.usal.jorgeav.sportapp.mainactivities;

import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.friends.FriendsFragment;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;

public class FriendsActivity extends BaseActivity {
    public static final String TAG = FriendsActivity.class.getSimpleName();
    public static final String USERID_PENDING_INTENT_EXTRA = "USERID_PENDING_INTENT_EXTRA";

    @Override
    public void startMainFragment() {
        super.startMainFragment();
        String userId = getIntent().getStringExtra(USERID_PENDING_INTENT_EXTRA);

        initFragment(FriendsFragment.newInstance(), false);
        if (userId != null) {
            initFragment(ProfileFragment.newInstance(userId), true);
        }
        mNavigationView.setCheckedItem(R.id.nav_friends);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_friends && super.onNavigationItemSelected(item);
    }
}
