package com.usal.jorgeav.sportapp.mainactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.SelectSportsAdapter;
import com.usal.jorgeav.sportapp.alarms.AlarmsFragment;
import com.usal.jorgeav.sportapp.alarms.addalarm.NewAlarmContract;
import com.usal.jorgeav.sportapp.alarms.addalarm.NewAlarmFragment;
import com.usal.jorgeav.sportapp.alarms.alarmdetail.DetailAlarmFragment;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.Sport;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class AlarmsActivity extends BaseActivity implements SelectSportsAdapter.OnSelectSportClickListener {
    private final static String TAG = AlarmsActivity.class.getSimpleName();

    public static final String ALARMID_PENDING_INTENT_EXTRA = "ALARMID_PENDING_INTENT_EXTRA";

    public static final int REQUEST_CODE_ADDRESS = 23;

    private static final String INSTANCE_FIELD_ID_SELECTED = "INSTANCE_FIELD_ID_SELECTED";
    public String mFieldId;
    private static final String INSTANCE_CITY_SELECTED = "INSTANCE_CITY_SELECTED";
    public String mCity;

    @Override
    public void startMainFragment() {
        super.startMainFragment();
        String alarmId = getIntent().getStringExtra(ALARMID_PENDING_INTENT_EXTRA);

        initFragment(AlarmsFragment.newInstance(), false);
        if (alarmId != null) {
            initFragment(DetailAlarmFragment.newInstance(alarmId), true);
        }
        mNavigationView.setCheckedItem(R.id.nav_alarms);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_alarms && super.onNavigationItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFieldId != null)
            outState.putString(INSTANCE_FIELD_ID_SELECTED, mFieldId);
        if (mCity != null)
            outState.putString(INSTANCE_CITY_SELECTED, mCity);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FIELD_ID_SELECTED))
            mFieldId = savedInstanceState.getString(INSTANCE_FIELD_ID_SELECTED);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_CITY_SELECTED))
            mCity = savedInstanceState.getString(INSTANCE_CITY_SELECTED);
    }

    @Override
    public void onSportClick(Sport sport) {
        Fragment fragment = NewAlarmFragment.newInstance(null, sport.getSportID());
        initFragment(fragment, true);
    }

    public void startMapActivityForResult(ArrayList<Field> dataList) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(MapsActivity.INTENT_EXTRA_FIELD_LIST, dataList);
        startActivityForResult(intent, REQUEST_CODE_ADDRESS);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADDRESS)
            if (resultCode == RESULT_OK) {
                // Expect a Field where set a new Alarm
                if (data.hasExtra(MapsActivity.FIELD_SELECTED_EXTRA)) {
                    Field field = data.getParcelableExtra(MapsActivity.FIELD_SELECTED_EXTRA);
                    mFieldId = field.getId();
                    mCity = field.getCity();
                    Log.d(TAG, "onActivityResult: " + field);
                } else if (data.hasExtra(MapsActivity.PLACE_SELECTED_EXTRA))
                    Toast.makeText(this, "You should select a Field", Toast.LENGTH_SHORT).show();

                if (mDisplayedFragment instanceof NewAlarmContract.View)
                    ((NewAlarmContract.View) mDisplayedFragment).showAlarmField(mFieldId, mCity);
            } else
                Toast.makeText(this, "You didn't select anything", Toast.LENGTH_SHORT).show();
    }
}
