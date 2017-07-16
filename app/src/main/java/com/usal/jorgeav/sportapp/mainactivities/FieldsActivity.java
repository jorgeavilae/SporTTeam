package com.usal.jorgeav.sportapp.mainactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.MyPlace;
import com.usal.jorgeav.sportapp.fields.FieldsFragment;
import com.usal.jorgeav.sportapp.fields.addfield.NewFieldContract;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class FieldsActivity extends BaseActivity {
    public static final String TAG = FieldsActivity.class.getSimpleName();
    public static final String INTENT_EXTRA_FIELD_LIST = "INTENT_EXTRA_FIELD_LIST";
    public static final int REQUEST_CODE_ADDRESS = 23;

    private static final String INSTANCE_PLACE_SELECTED = "INSTANCE_PLACE_SELECTED";
    public MyPlace mPlaceSelected;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADDRESS && resultCode == RESULT_OK) {
            mPlaceSelected = data.getParcelableExtra(MapsActivity.PLACE_SELECTED_EXTRA);
            if (mDisplayedFragment instanceof NewFieldContract.View)
                ((NewFieldContract.View)mDisplayedFragment).showFieldPlace(
                        mPlaceSelected.getAddress(),
                        mPlaceSelected.getShortNameLocality(),
                        mPlaceSelected.getCoordinates());
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPlaceSelected != null)
            outState.putParcelable(INSTANCE_PLACE_SELECTED, mPlaceSelected);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_PLACE_SELECTED)) {
            mPlaceSelected = savedInstanceState.getParcelable(INSTANCE_PLACE_SELECTED);

            if (mPlaceSelected != null && mDisplayedFragment instanceof NewFieldContract.View)
                ((NewFieldContract.View) mDisplayedFragment).showFieldPlace(
                        mPlaceSelected.getAddress(),
                        mPlaceSelected.getShortNameLocality(),
                        mPlaceSelected.getCoordinates());
        }
    }
}
