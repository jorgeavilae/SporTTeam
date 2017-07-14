package com.usal.jorgeav.sportapp.mainactivities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.fields.FieldsFragment;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class FieldsActivity extends BaseActivity implements OnMapReadyCallback {
    public static final String TAG = FieldsActivity.class.getSimpleName();
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        FrameLayout fl = (FrameLayout) findViewById(R.id.map_container);
        fl.setVisibility(View.VISIBLE);
    }
}
