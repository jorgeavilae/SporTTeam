package com.usal.jorgeav.sportapp.mainactivities;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class ProfileActivity extends BaseActivity {

    @Override
    public void startMainFragment() {
        super.startMainFragment();

        String userId = null;
        try { userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) { e.printStackTrace(); }

        initFragment(ProfileFragment.newInstance(userId), false);
        mNavigationView.setCheckedItem(R.id.nav_profile);
    }
}