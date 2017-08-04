package com.usal.jorgeav.sportapp.mainactivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.SportSpinnerAdapter;
import com.usal.jorgeav.sportapp.adduser.sportpractice.SportsListFragment;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.MyPlace;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.fields.FieldsMapFragment;
import com.usal.jorgeav.sportapp.fields.addfield.NewFieldContract;
import com.usal.jorgeav.sportapp.fields.addfield.NewFieldFragment;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class FieldsActivity extends BaseActivity implements SportsListFragment.OnSportsSelected {
    public static final String TAG = FieldsActivity.class.getSimpleName();
    public static final String INTENT_EXTRA_CREATE_NEW_FIELD = "INTENT_EXTRA_CREATE_NEW_FIELD";
    public static final int REQUEST_CODE_ADDRESS_TO_RETRIEVE = 23;
    public static final int REQUEST_CODE_ADDRESS_TO_START_NEW_FRAGMENT = 24;

    private static final String INSTANCE_FIELD_ID_SELECTED = "INSTANCE_FIELD_ID_SELECTED";
    public String mFieldId;
    private static final String INSTANCE_ADDRESS_SELECTED = "INSTANCE_ADDRESS_SELECTED";
    public String mAddress;
    private static final String INSTANCE_CITY_SELECTED = "INSTANCE_CITY_SELECTED";
    public String mCity;
    private static final String INSTANCE_COORD_SELECTED = "INSTANCE_COORD_SELECTED";
    public LatLng mCoord;
    private static final String INSTANCE_SPORTS_COURT_SELECTED = "INSTANCE_SPORTS_COURT_SELECTED";
    public ArrayList<SportCourt> mSports;

    @Override
    public void startMainFragment() {
        super.startMainFragment();

        boolean createNewField = false;
        if (getIntent().hasExtra(INTENT_EXTRA_CREATE_NEW_FIELD)) createNewField = true;
        initFragment(FieldsMapFragment.newInstance(createNewField), false);

        mNavigationView.setCheckedItem(R.id.nav_fields);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_fields && super.onNavigationItemSelected(item);
    }

    public void startMapActivityForResult(ArrayList<Field> dataList, boolean startNewField) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(MapsActivity.INTENT_EXTRA_FIELD_LIST, dataList);
        intent.putExtra(MapsActivity.INTENT_EXTRA_PARENT_FIELDS_ACTIVITY, true);
        if (startNewField)
            startActivityForResult(intent, REQUEST_CODE_ADDRESS_TO_START_NEW_FRAGMENT);
        else
            startActivityForResult(intent, REQUEST_CODE_ADDRESS_TO_RETRIEVE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADDRESS_TO_RETRIEVE || requestCode == REQUEST_CODE_ADDRESS_TO_START_NEW_FRAGMENT) {
            if (resultCode == RESULT_OK) {
                // Expect a Field where to add a new Sport,
                // or an address (MyPlace) where to add a new Field
                if (data.hasExtra(MapsActivity.FIELD_SELECTED_EXTRA)) {
                    Field field = data.getParcelableExtra(MapsActivity.FIELD_SELECTED_EXTRA);
                    mFieldId = field.getId();
                    mAddress = field.getAddress();
                    mCity = field.getCity();
                    mCoord = new LatLng(field.getCoord_latitude(), field.getCoord_longitude());

                    //Start dialog to add sport to this Field
                    startDialogToAddSport(field);
                } else if (data.hasExtra(MapsActivity.PLACE_SELECTED_EXTRA)) {
                    MyPlace myPlace = data.getParcelableExtra(MapsActivity.PLACE_SELECTED_EXTRA);
                    mFieldId = null;
                    mAddress = myPlace.getAddress();
                    mCity = myPlace.getShortNameLocality();
                    mCoord = myPlace.getCoordinates();

                    //When select a place from FieldsFragment's addFieldButton
                    if (requestCode == REQUEST_CODE_ADDRESS_TO_START_NEW_FRAGMENT) {
                        //Select sports and later, in retrieveFields(), start new Field fragment flow
                        Fragment fragment = SportsListFragment.newInstance("", null);
                        initFragment(fragment, true);
                    } else { //When edit Field's place from NewFieldFragment's button
                        if (mDisplayedFragment instanceof NewFieldContract.View)
                            ((NewFieldContract.View) mDisplayedFragment).showFieldPlace(mAddress, mCity, mCoord);
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.toast_should_select_place), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startDialogToAddSport(final Field field) {
        // Prepare list
        List<String> sportsResources = Arrays.asList(getResources().getStringArray(R.array.sport_id_values));
        ArrayList<String> sportsLeft = new ArrayList<>();
        for (String sportId : sportsResources)
            if (!field.getSport().containsKey(sportId))
                sportsLeft.add(sportId);

        // Prepare view
        View view = getLayoutInflater().inflate(R.layout.add_sport_dialog, null);
        final Spinner sportSpinner = (Spinner) view.findViewById(R.id.add_sport_dialog_sport);
        sportSpinner.setAdapter(new SportSpinnerAdapter(this, R.layout.sport_spinner_item, sportsLeft));
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.add_sport_dialog_rate);

        // Prepare dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.add_sport_to_field_dialog_title));
        dialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String sportId = sportSpinner.getSelectedItem().toString();
                float punctuation = ratingBar.getRating();
                FirebaseActions.addFieldSport(field.getId(), new SportCourt(sportId, (double)punctuation, 1L));
                Toast.makeText(FieldsActivity.this, getString(R.string.toast_add_sport_success), Toast.LENGTH_SHORT).show();
                if (mDisplayedFragment != null) mDisplayedFragment.resetBackStack();
            }
        });
        dialog.setNegativeButton(android.R.string.cancel, null);
        dialog.setView(view);

        dialog.show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFieldId != null)
            outState.putString(INSTANCE_FIELD_ID_SELECTED, mFieldId);
        if (mAddress != null)
            outState.putString(INSTANCE_ADDRESS_SELECTED, mAddress);
        if (mCity != null)
            outState.putString(INSTANCE_CITY_SELECTED, mCity);
        if (mCoord != null)
            outState.putParcelable(INSTANCE_COORD_SELECTED, mCoord);
        if (mSports != null && mSports.size() > 0)
            outState.putParcelableArrayList(INSTANCE_SPORTS_COURT_SELECTED, mSports);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FIELD_ID_SELECTED))
            mFieldId = savedInstanceState.getString(INSTANCE_FIELD_ID_SELECTED);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ADDRESS_SELECTED))
            mAddress = savedInstanceState.getString(INSTANCE_ADDRESS_SELECTED);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_CITY_SELECTED))
            mCity = savedInstanceState.getString(INSTANCE_CITY_SELECTED);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_COORD_SELECTED))
            mCoord = savedInstanceState.getParcelable(INSTANCE_COORD_SELECTED);
        if (mDisplayedFragment instanceof NewFieldContract.View)
            ((NewFieldContract.View) mDisplayedFragment).showFieldPlace(mAddress, mCity, mCoord);


        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_SPORTS_COURT_SELECTED)) {
            mSports = savedInstanceState.getParcelableArrayList(INSTANCE_SPORTS_COURT_SELECTED);
            if (mDisplayedFragment instanceof NewFieldContract.View)
                ((NewFieldContract.View) mDisplayedFragment).setSportCourts(mSports);
        }
    }

    @Override
    public void retrieveSportsSelected(String fieldId, List<Sport> sportsSelected) {
        HashMap<String, Object> sportsMap = new HashMap<>();
        mSports = new ArrayList<>();
        if (sportsSelected != null) {
            for (Sport s : sportsSelected) {
                SportCourt sc = new SportCourt(s.getSportID(), (double) s.getPunctuation(), (long) s.getVotes());
                sportsMap.put(s.getSportID(), sc.toMap());
                mSports.add(sc);
            }

            if (fieldId != null && !TextUtils.isEmpty(fieldId)) {
                //Update sports (DetailFieldFragment update sports)
                FirebaseActions.updateFieldSports(fieldId, sportsMap);
                onBackPressed();
            } else {
                //Start NewFieldFragment with sport already selected
                Fragment fragment = NewFieldFragment.newInstance(null);
                initFragment(fragment, true);
            }
        }
    }
}
