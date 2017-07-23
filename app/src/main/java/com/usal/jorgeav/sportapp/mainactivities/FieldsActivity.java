package com.usal.jorgeav.sportapp.mainactivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.MyPlace;
import com.usal.jorgeav.sportapp.fields.FieldsFragment;
import com.usal.jorgeav.sportapp.fields.addfield.NewFieldContract;
import com.usal.jorgeav.sportapp.fields.addfield.NewFieldFragment;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class FieldsActivity extends BaseActivity {
    public static final String TAG = FieldsActivity.class.getSimpleName();
    public static final String INTENT_EXTRA_FIELD_LIST = "INTENT_EXTRA_FIELD_LIST";
    public static final int REQUEST_CODE_ADDRESS = 23;

    private static final String INSTANCE_FIELD_ID_SELECTED = "INSTANCE_FIELD_ID_SELECTED";
    public String mFieldId;
    private static final String INSTANCE_ADDRESS_SELECTED = "INSTANCE_ADDRESS_SELECTED";
    public String mAddress;
    private static final String INSTANCE_CITY_SELECTED = "INSTANCE_CITY_SELECTED";
    public String mCity;
    private static final String INSTANCE_COORD_SELECTED = "INSTANCE_COORD_SELECTED";
    public LatLng mCoord;

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
        if(requestCode == REQUEST_CODE_ADDRESS) {
            if (resultCode == RESULT_OK) {
                // Expect a Field where add a new Sport,
                // or an address (MyPlace) add a new Field
                if (data.hasExtra(MapsActivity.FIELD_SELECTED_EXTRA)) {
                    //TODO retrieve aqui tambien los deportes de este field
                    Field field = data.getParcelableExtra(MapsActivity.FIELD_SELECTED_EXTRA);
                    mFieldId = field.getId();
                    mAddress = field.getAddress();
                    mCity = field.getCity();
                    mCoord = new LatLng(field.getCoord_latitude(), field.getCoord_longitude());
                    
                    //Start dialog to add sport to this Field
                    startDialogToAddSport();
                } else if (data.hasExtra(MapsActivity.PLACE_SELECTED_EXTRA)) {
                    MyPlace myPlace = data.getParcelableExtra(MapsActivity.PLACE_SELECTED_EXTRA);
                    mFieldId = null;
                    mAddress = myPlace.getAddress();
                    mCity = myPlace.getShortNameLocality();
                    mCoord = myPlace.getCoordinates();

                    //Start new Field
                    Fragment fragment = NewFieldFragment.newInstance(null);
                    initFragment(fragment, true);
                }
                if (mDisplayedFragment instanceof NewFieldContract.View)
                    ((NewFieldContract.View) mDisplayedFragment).showFieldPlace(mAddress, mCity, mCoord);
            } else {
                Toast.makeText(this, "You should select a place", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startDialogToAddSport() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.add_sport_dialog, null);

        ArrayAdapter sportsAdapter = ArrayAdapter.createFromResource(this, R.array.sport_id, android.R.layout.simple_spinner_item);
        sportsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sportSpinner = (Spinner) view.findViewById(R.id.add_sport_dialog_sport);
        sportSpinner.setAdapter(sportsAdapter);

        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.add_sport_dialog_rate);
        dialog.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO completar
                Log.d(TAG, "onClick: añadir deporte "+sportSpinner.getSelectedItem().toString());
                Log.d(TAG, "onClick: añadir puntuacion "+ratingBar.getRating());
            }
        });
        dialog.setNegativeButton("Cancelar", null);

        dialog.setTitle("Add Sport to this Field");
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
    }
}
