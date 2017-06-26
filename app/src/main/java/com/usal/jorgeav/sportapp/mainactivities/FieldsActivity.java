package com.usal.jorgeav.sportapp.mainactivities;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.fields.FieldsFragment;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class FieldsActivity extends BaseActivity {

    @Override
    public void startMainFragment() {
        super.startMainFragment();

        initFragment(FieldsFragment.newInstance(), false);
        mNavigationView.setCheckedItem(R.id.nav_fields);
    }
}
