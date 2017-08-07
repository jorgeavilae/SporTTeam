package com.usal.jorgeav.sportapp.mainactivities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.SelectSportsAdapter;
import com.usal.jorgeav.sportapp.alarms.AlarmsFragment;
import com.usal.jorgeav.sportapp.alarms.addalarm.NewAlarmContract;
import com.usal.jorgeav.sportapp.alarms.addalarm.NewAlarmFragment;
import com.usal.jorgeav.sportapp.alarms.alarmdetail.DetailAlarmFragment;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.utils.Utiles;

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
    private static final String INSTANCE_COORD_SELECTED = "INSTANCE_COORD_SELECTED";
    public LatLng mCoord;

    @Override
    public void startMainFragment() {
        super.startMainFragment();
        String alarmId = getIntent().getStringExtra(ALARMID_PENDING_INTENT_EXTRA);

        initFragment(AlarmsFragment.newInstance(), false);

        // Open an alarm detail right after alarm list because this Activity is due to a notification
        if (alarmId != null)
            initFragment(DetailAlarmFragment.newInstance(alarmId), true);

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
        if (mCoord != null)
            outState.putParcelable(INSTANCE_COORD_SELECTED, mCoord);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FIELD_ID_SELECTED))
            mFieldId = savedInstanceState.getString(INSTANCE_FIELD_ID_SELECTED);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_CITY_SELECTED))
            mCity = savedInstanceState.getString(INSTANCE_CITY_SELECTED);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_COORD_SELECTED))
            mCoord = savedInstanceState.getParcelable(INSTANCE_COORD_SELECTED);
    }

    @Override
    public void onSportClick(Sport sport) {
        Fragment fragment = NewAlarmFragment.newInstance(null, sport.getSportID());
        initFragment(fragment, true);
    }

    public void startMapActivityForResult(ArrayList<Field> dataList) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(MapsActivity.INTENT_EXTRA_FIELD_LIST, dataList);
        intent.putExtra(MapsActivity.INTENT_EXTRA_ONLY_FIELDS, true);
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
                    mCoord = new LatLng(field.getCoord_latitude(), field.getCoord_longitude());
                    Log.d(TAG, "onActivityResult: " + field);
                } else if (data.hasExtra(MapsActivity.PLACE_SELECTED_EXTRA)) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_title_you_must_select_place)
                            .setMessage(R.string.dialog_msg_you_must_select_place)
                            .create().show();
                } else if (data.hasExtra(MapsActivity.ADD_FIELD_SELECTED_EXTRA)) {
                    Utiles.startFieldsActivityAndNewField(this);
                    Log.d(TAG, "onActivityResult: ADD_FIELD_SELECTED_EXTRA");
                }

                if (mDisplayedFragment instanceof NewAlarmContract.View)
                    ((NewAlarmContract.View) mDisplayedFragment).showAlarmField(mFieldId, mCity);
            } else
                Toast.makeText(this, R.string.no_field_selection, Toast.LENGTH_SHORT).show();
    }
}
