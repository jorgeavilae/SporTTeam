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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.MapMarkerInfoAdapter;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.MyPlace;
import com.usal.jorgeav.sportapp.network.HttpRequestTask;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {
    public static final String TAG = MapsActivity.class.getSimpleName();
    public static final String PLACE_SELECTED_EXTRA = "PLACE_SELECTED_EXTRA";
    public static final String FIELD_SELECTED_EXTRA = "FIELD_SELECTED_EXTRA";

    private GoogleMap mMap;
    ArrayList<Field> mFieldsList;
    ArrayList<Marker> mMarkersList;
    Toolbar mToolbar;
    MyPlace mPlaceSelected;
    Marker mMarkerSelectedPlace;
    Field mFieldSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* NotFoundException: Resource ID #0x7f07000e */
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
            //Field first in case MyPlace isn't succeeded
            if (mFieldSelected != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(FIELD_SELECTED_EXTRA, mFieldSelected);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else if (mPlaceSelected != null && mPlaceSelected.isSucceed()) {
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

    public void startMapFragment() {
        mMarkersList = new ArrayList<>();
        mFieldsList = getIntent().getParcelableArrayListExtra(FieldsActivity.INTENT_EXTRA_FIELD_LIST);
        if (mFieldsList == null) mFieldsList = new ArrayList<>();

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
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(new MapMarkerInfoAdapter(getLayoutInflater(), mFieldsList));

        //Populate map with Fields
        for (int i = 0; i < mFieldsList.size(); i++) {
            Field f = mFieldsList.get(i);
            LatLng latLong = new LatLng(f.getCoord_latitude(), f.getCoord_longitude());
            if (latLong != null) {
                float hue = Utiles.getFloatFromResources(getResources(), R.dimen.hue_of_colorSportteam_logo);
                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(latLong)
                        .title(f.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(hue)));
                m.setTag(i);
                mMarkersList.add(m);
            }
        }

        // Move Camera
        centerCameraOnInit();
    }

    private void centerCameraOnInit() {
        mMap.setMinZoomPreference(20); //Buildings
        mMap.setMinZoomPreference(5); //Continent
        String myUserId = Utiles.getCurrentUserId();
        if (myUserId != null) {
            LatLng myCityLatLong = UtilesPreferences.getCurrentUserCityCoords(this);

            if (myCityLatLong.latitude == 0 && myCityLatLong.longitude == 0)
                myCityLatLong = new LatLng(UtilesPreferences.CACERES_LATITUDE, UtilesPreferences.CACERES_LONGITUDE);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(myCityLatLong));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "onMapClick: "+latLng);

        //TODO if latlng esta cerca de un field de mFieldList: onMarkerCLick(mMarkerList.get(fieldPosition))
        new AsyncTask<LatLng, Void, MyPlace>(){
            @Override
            protected MyPlace doInBackground(LatLng... latLng) {
                String apiKey = getResources().getString(R.string.google_maps_key);
                return HttpRequestTask.placeInLatLngLocation(apiKey, latLng[0]);
            }

            @Override
            protected void onPostExecute(MyPlace place) {
                updateSelectedPlace(place);
            }
        }.execute(latLng);
    }

    private void updateSelectedPlace(MyPlace selectedPlace) {
        if (selectedPlace.isSucceed()) {
            int position = Utiles.searchCoordinatesInFieldList(mFieldsList, selectedPlace.getCoordinates());
            if (position > -1) {
                // mPlaceSelected is already a Field todo should move this to before asynctask.execute, finding near Latlng
                onMarkerClick(mMarkersList.get(position));
            } else {
                //MyPlace selected: invalid Field selected
                mFieldSelected = null;

                mPlaceSelected = selectedPlace;

                if (mMarkerSelectedPlace != null) mMarkerSelectedPlace.remove();
                mMarkerSelectedPlace = null;

                float hue = Utiles.getFloatFromResources(getResources(), R.dimen.hue_of_colorSportteam_logo);
                mMarkerSelectedPlace = mMap.addMarker(new MarkerOptions()
                        .position(mPlaceSelected.getCoordinates())
                        .title(mPlaceSelected.getAddress())
                        .icon(BitmapDescriptorFactory.defaultMarker(hue)));
                LatLngBounds llb = new LatLngBounds(mPlaceSelected.getViewPortSouthwest(),
                        mPlaceSelected.getViewPortNortheast());
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(llb, 0));
            }
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer position = (Integer) marker.getTag();
        if (position != null) {
            //Field selected: invalid MyPlace selected
            if (mMarkerSelectedPlace != null) mMarkerSelectedPlace.remove();
            mMarkerSelectedPlace = null;
            mPlaceSelected = null;

            mFieldSelected = mFieldsList.get(position);

            // Move camera
            LatLng southwest = new LatLng(mFieldSelected.getCoord_latitude()-0.00135, mFieldSelected.getCoord_longitude()-0.00135);
            LatLng northeast = new LatLng(mFieldSelected.getCoord_latitude()+0.00135, mFieldSelected.getCoord_longitude()+0.00135);
            LatLngBounds llb = new LatLngBounds(southwest, northeast);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(llb, 0));
            marker.showInfoWindow();

            Log.d(TAG, "onMarkerClick: " + mFieldSelected);
            return true;
        }
        return false;
    }
}
