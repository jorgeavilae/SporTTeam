package com.usal.jorgeav.sportapp.mainactivities;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    public static final String TAG = ProfileActivity.class.getSimpleName();

    @Override
    public void startMainFragment() {
        super.startMainFragment();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();

        initFragment(ProfileFragment.newInstance(myUserID), false);
        mNavigationView.setCheckedItem(R.id.nav_profile);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_profile && super.onNavigationItemSelected(item);
    }

    @Override
    public void retrieveSportsSelected(String myUserID, List<Sport> sportsSelected) {
        if (myUserID != null && !TextUtils.isEmpty(myUserID)) {
            HashMap<String, Float> sportsMap = new HashMap<>();
            if (sportsSelected != null)
                for (Sport sport : sportsSelected)
                    sportsMap.put(sport.getName(), sport.getPunctuation());
            FirebaseActions.updateSports(myUserID, sportsMap);
        }
        onBackPressed();
    }
}
