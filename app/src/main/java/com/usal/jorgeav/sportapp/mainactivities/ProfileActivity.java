package com.usal.jorgeav.sportapp.mainactivities;

import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adduser.sportpractice.SportsListFragment;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class ProfileActivity extends BaseActivity implements SportsListFragment.OnSportsSelected {

    @Override
    public void startMainFragment() {
        super.startMainFragment();

        String userId = null;
        try { userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) { e.printStackTrace(); }

        initFragment(ProfileFragment.newInstance(userId), false);
        mNavigationView.setCheckedItem(R.id.nav_profile);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_profile && super.onNavigationItemSelected(item);
    }

    @Override
    public void retrieveSportsSelected(List<Sport> sportsSelected) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String, Float> sportsMap = new HashMap<>();
        if (sportsSelected != null)
            for (Sport sport : sportsSelected)
                sportsMap.put(sport.getmName(), sport.getmLevel());
        FirebaseActions.updateSports(userId, sportsMap);
    }
}
