package com.usal.jorgeav.sportapp.mainactivities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.fields.FieldsFragment;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class FieldsActivity extends BaseActivity {
    public static final String TAG = FieldsActivity.class.getSimpleName();
    public static final String INTENT_EXTRA_FIELD_LIST = "INTENT_EXTRA_FIELD_LIST";
    public static final int REQUEST_CODE_ADDRESS = 23;

    @Override
    public void startMainFragment() {
        super.startMainFragment();

        initFragment(FieldsFragment.newInstance(), false);
        mNavigationView.setCheckedItem(R.id.nav_fields);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_fields && super.onNavigationItemSelected(item);
    }

    public void startMapActivityForResult(ArrayList<Field> dataList) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(INTENT_EXTRA_FIELD_LIST, dataList);
        startActivityForResult(intent, REQUEST_CODE_ADDRESS);
    }
}
