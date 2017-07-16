package com.usal.jorgeav.sportapp.mainactivities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.MyPlace;
import com.usal.jorgeav.sportapp.network.HttpRequestTask;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
    public static final String TAG = MapsActivity.class.getSimpleName();
    public static final String PLACE_SELECTED_EXTRA = "PLACE_SELECTED_EXTRA";

    private GoogleMap mMap;
    ArrayList<Field> mFieldsList;
    Toolbar mToolbar;
    MyPlace mPlaceSelected;
    Marker mMarkerSelectedPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        startMapFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ok, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_ok) {
            Log.d(TAG, "onOptionsItemSelected: Ok");

            if (mPlaceSelected.isSucceed()) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(PLACE_SELECTED_EXTRA, mPlaceSelected);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "No place selected", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void startMapFragment() {
        mFieldsList = getIntent().getParcelableArrayListExtra(FieldsActivity.INTENT_EXTRA_FIELD_LIST);
        Log.d(TAG, "startMapFragment: "+mFieldsList);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Maps");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);

        for (Field f : mFieldsList) {
            LatLng latLong = f.getLatLong();
            if (latLong != null)
                mMap.addMarker(new MarkerOptions().position(latLong).title(f.getmName() + " " + f.getmSport()));
        }
        mMap.setMinZoomPreference(20); //Buildings
        mMap.setMinZoomPreference(5); //Continent

        String myUserId = Utiles.getCurrentUserId();
        if (myUserId != null) {
            LatLng myCityLatLong = UtilesPreferences.getCurrentUserCityCoords(this);

            if (myCityLatLong.latitude == 0 && myCityLatLong.longitude == 0)
                myCityLatLong = new LatLng(UtilesPreferences.CACERES_LATITUDE, UtilesPreferences.CACERES_LONGITUDE);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(myCityLatLong));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick: "+latLng);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "onMapClick: "+latLng);

       new AsyncTask<LatLng, Void, MyPlace>(){
            @Override
            protected MyPlace doInBackground(LatLng... latLng) {
                return HttpRequestTask.syncWeather(getApplicationContext(), latLng[0]);
            }

            @Override
            protected void onPostExecute(MyPlace place) {
                updateSelectedPlace(place);
            }
        }.execute(latLng);
    }

    private void updateSelectedPlace(MyPlace selectedPlace) {
        Log.d(TAG, "updateSelectedPlace: "+selectedPlace);
        mPlaceSelected = selectedPlace;
        if (mPlaceSelected.isSucceed()) {

            if (mMarkerSelectedPlace != null) mMarkerSelectedPlace.remove();

            mMarkerSelectedPlace = null;
            mMarkerSelectedPlace = mMap.addMarker(new MarkerOptions()
                    .position(selectedPlace.getCoordinates())
                    .title(selectedPlace.getAddress()));
        } else {
            switch (mPlaceSelected.getStatus()) {
                case "OK":
                    break;
                case "ZERO_RESULTS":
                    /* Maybe latlng in a remote location */
                    Toast.makeText(this, "Maybe latlng in a remote location", Toast.LENGTH_SHORT).show();
                    break;
                case "OVER_QUERY_LIMIT":
                    /* Over your quota. */
                    Toast.makeText(this, "Over quota", Toast.LENGTH_SHORT).show();
                    break;
                case "REQUEST_DENIED":
                    /* API key invalid */
                    Toast.makeText(this, "API key invalid", Toast.LENGTH_SHORT).show();
                    break;
                case "INVALID_REQUEST":
                    /* Missing latlng or error in result_type */
                    Toast.makeText(this, "Missing latlng or error in result_type", Toast.LENGTH_SHORT).show();
                    break;
                case "UNKNOWN_ERROR":
                    /* Probably a bad connection */
                    Toast.makeText(this, "Probably a bad connection", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Probably a bad connection", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
