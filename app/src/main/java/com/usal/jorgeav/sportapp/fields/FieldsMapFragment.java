package com.usal.jorgeav.sportapp.fields;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
import com.usal.jorgeav.sportapp.adapters.MapFieldMarkerInfoAdapter;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.fields.fielddetail.DetailFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.ActivityContracts;
import com.usal.jorgeav.sportapp.mainactivities.FieldsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class FieldsMapFragment extends SupportMapFragment
        implements FieldsContract.View, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = FieldsMapFragment.class.getSimpleName();

    protected ActivityContracts.FragmentManagement mFragmentManagementListener;
    protected ActivityContracts.NavigationDrawerManagement mNavigationDrawerManagementListener;

    private GoogleMap mMap;
    FieldsContract.Presenter mFieldsPresenter;

    ArrayList<Marker> mMarkersList;
    ArrayList<Field> mFieldsList;
    private static boolean sInitialize;

    public FieldsMapFragment() {
        // Required empty public constructor
    }

    public static FieldsMapFragment newInstance(boolean createNewField) {
        Bundle b = new Bundle();
        //Is necessary a NewFieldFragment initialization programmatically?
        if (createNewField)
            b.putString(FieldsActivity.INTENT_EXTRA_CREATE_NEW_FIELD, "");
        FieldsMapFragment fragment = new FieldsMapFragment();
        fragment.setArguments(b);
        sInitialize = false;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mFieldsPresenter = new FieldsPresenter(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        hideSoftKeyboard();
        if (item.getItemId() == R.id.action_add) {
            if (mFieldsList != null)
                ((FieldsActivity)getActivity()).startMapActivityForResult(mFieldsList, true);
            return true;
        }
        return false;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.fields), null);
        mNavigationDrawerManagementListener.setToolbarAsNav();
    }

    @Override
    public void onStart() {
        super.onStart();

        getMapAsync(this);

        mFieldsPresenter.loadNearbyFields(getLoaderManager(), getArguments());
    }

    @Override
    public void showFields(Cursor cursor) {
        mFieldsList = UtilesContentProvider.cursorToMultipleField(cursor);

        // Remove markers from map with remove() and clear marker list with a new ArrayList
        if (mMarkersList != null) for (Marker m : mMarkersList) m.remove();
        mMarkersList = new ArrayList<>();

        //Is necessary a NewFieldFragment initialization programmatically?
        if (!sInitialize
                && getArguments() != null && getArguments().containsKey(FieldsActivity.INTENT_EXTRA_CREATE_NEW_FIELD)) {
            if (mFieldsList != null)
                ((FieldsActivity)getActivity()).startMapActivityForResult(mFieldsList, true);
            sInitialize = true;
            return;
        }

        //Populate map with Fields. Field's list not empty. Check if map is ready
        if (mMap != null) populateMap();
    }

    private void populateMap() {
        mMap.setInfoWindowAdapter(new MapFieldMarkerInfoAdapter(getActivity().getLayoutInflater(), mFieldsList));
        for (int i = 0; i < mFieldsList.size(); i++) {
            Field f = mFieldsList.get(i);
            LatLng latLong = new LatLng(f.getCoord_latitude(), f.getCoord_longitude());

            float hue = Utiles.getFloatFromResources(getResources(), R.dimen.hue_of_colorSportteam_logo);
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(latLong)
                    .title(f.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));
            m.setTag(i);
            mMarkersList.add(m);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setIndoorEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setMaxZoomPreference(19);
        mMap.setMinZoomPreference(10);

        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        // Move Camera
        centerCameraOnInit();

        //Populate map with Fields. Map is ready. Check if Field's list are empty
        if (mFieldsList != null && mFieldsList.size() > 0) populateMap();
    }

    private void centerCameraOnInit() {
        String myUserId = Utiles.getCurrentUserId();
        if (myUserId != null) {
            LatLng myCityLatLong = UtilesPreferences.getCurrentUserCityCoords(getActivityContext());

            if (myCityLatLong.latitude == 0 && myCityLatLong.longitude == 0)
                myCityLatLong = new LatLng(UtilesPreferences.CACERES_LATITUDE, UtilesPreferences.CACERES_LONGITUDE);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(myCityLatLong));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
        showContent();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer position = (Integer) marker.getTag();
        if (position != null) {
            Field f = mFieldsList.get(position);

            // Move camera
            LatLng southwest = new LatLng(f.getCoord_latitude()-0.00135, f.getCoord_longitude()-0.00135);
            LatLng northeast = new LatLng(f.getCoord_latitude()+0.00135, f.getCoord_longitude()+0.00135);
            LatLngBounds llb = new LatLngBounds(southwest, northeast);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(llb, 0));
            marker.showInfoWindow();

            return true;
        }
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Integer position = (Integer) marker.getTag();
        if (position != null) {
            Field f = mFieldsList.get(position);

            // Open Detail Field
            Fragment newFragment = DetailFieldFragment.newInstance(f.getId(), false);
            mFragmentManagementListener.initFragment(newFragment, true);

            marker.hideInfoWindow();
        }
    }

    @Override
    public Context getActivityContext() {
        return getActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActivityContracts.FragmentManagement)
            mFragmentManagementListener = (ActivityContracts.FragmentManagement) context;
        if (context instanceof ActivityContracts.NavigationDrawerManagement)
            mNavigationDrawerManagementListener = (ActivityContracts.NavigationDrawerManagement) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideSoftKeyboard();
        mFragmentManagementListener = null;
        mNavigationDrawerManagementListener = null;
    }

    public void showContent() {
        if (mFragmentManagementListener != null)
            mFragmentManagementListener.showContent();
    }

    public void hideContent() {
        if (mFragmentManagementListener != null)
            mFragmentManagementListener.hideContent();
    }

    public void resetBackStack() {
        getActivity().getSupportFragmentManager().popBackStack(
                getActivity().getSupportFragmentManager().getBackStackEntryAt(0).getId(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /* https://stackoverflow.com/a/1109108/4235666 */
    public void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
