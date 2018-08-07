package com.usal.jorgeav.sportapp.searchevent;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.usal.jorgeav.sportapp.adapters.MapEventMarkerInfoAdapter;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.mainactivities.ActivityContracts;
import com.usal.jorgeav.sportapp.mainactivities.EventsActivity;
import com.usal.jorgeav.sportapp.mainactivities.SearchEventsActivity;
import com.usal.jorgeav.sportapp.searchevent.advancedsearch.SearchEventsFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

import java.util.ArrayList;

public class EventsMapFragment extends SupportMapFragment
        implements EventsMapContract.View,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = EventsMapFragment.class.getSimpleName();

    protected ActivityContracts.FragmentManagement mFragmentManagementListener;
    protected ActivityContracts.ActionBarIconManagement mActionBarIconManagementListener;

    private GoogleMap mMap;
    EventsMapContract.Presenter mEventsMapPresenter;

    ArrayList<Marker> mMarkersList;
    ArrayList<Event> mEventsList;

    public EventsMapFragment() {
        // Required empty public constructor
    }

    public static EventsMapFragment newInstance() {
        return new EventsMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mEventsMapPresenter = new EventsMapPresenter(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search_events, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_advanced_search) {
            mFragmentManagementListener.initFragment(SearchEventsFragment.newInstance(), true);
            return true;
        } else if (item.getItemId() == R.id.action_add_event) {
            Intent intent = new Intent(getActivity(), EventsActivity.class);
            intent.putExtra(EventsActivity.CREATE_NEW_EVENT_INTENT_EXTRA, "dummy");
            startActivity(intent);
            return true;
        }
        return false;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.search_events), null);
        mActionBarIconManagementListener.setToolbarAsNav();
    }

    @Override
    public void onStart() {
        super.onStart();

        getMapAsync(this);

        Bundle args = createBundleWithParams();
        mEventsMapPresenter.loadNearbyEvents(getLoaderManager(), args);
    }

    private Bundle createBundleWithParams() {
        Bundle args = new Bundle();
        if (getActivity() instanceof SearchEventsActivity) {
            SearchEventsActivity searchEventsActivity = (SearchEventsActivity) getActivity();
            if (searchEventsActivity.mSportId != null)
                args.putString(SearchEventsActivity.INSTANCE_SPORTID_SELECTED, searchEventsActivity.mSportId);

            if (searchEventsActivity.mDateFrom != null)
                args.putLong(SearchEventsActivity.INSTANCE_DATE_FROM_SELECTED, searchEventsActivity.mDateFrom);
            if (searchEventsActivity.mDateTo != null)
                args.putLong(SearchEventsActivity.INSTANCE_DATE_TO_SELECTED, searchEventsActivity.mDateTo);

            if (searchEventsActivity.mTotalFrom != null)
                args.putInt(SearchEventsActivity.INSTANCE_TOTAL_FROM_SELECTED, searchEventsActivity.mTotalFrom);
            if (searchEventsActivity.mTotalTo != null)
                args.putInt(SearchEventsActivity.INSTANCE_TOTAL_TO_SELECTED, searchEventsActivity.mTotalTo);

            if (searchEventsActivity.mEmptyFrom != null)
                args.putInt(SearchEventsActivity.INSTANCE_EMPTY_FROM_SELECTED, searchEventsActivity.mEmptyFrom);
            if (searchEventsActivity.mEmptyTo != null)
                args.putInt(SearchEventsActivity.INSTANCE_EMPTY_TO_SELECTED, searchEventsActivity.mEmptyTo);
        }
        return args;
    }


    @Override
    public void showEvents(Cursor cursor) {
        mEventsList = UtilesContentProvider.cursorToMultipleEvent(cursor);

        // Remove markers from map with remove() and clear marker list with a new ArrayList
        if (mMarkersList != null) for (Marker m : mMarkersList) m.remove();
        mMarkersList = new ArrayList<>();

        //Populate map with Events
        if (mMap != null) populateMap();
    }

    private void populateMap() {
        mMap.setInfoWindowAdapter(new MapEventMarkerInfoAdapter(getActivity().getLayoutInflater(), mEventsList));
        for (int i = 0; i < mEventsList.size(); i++) {
            Event event = mEventsList.get(i);
            LatLng latLong = new LatLng(event.getCoord_latitude(), event.getCoord_longitude());

            // Add marker to map
            float hue = Utiles.getFloatFromResources(getResources(), R.dimen.hue_of_colorSportteam_logo);
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(latLong)
                    .title(event.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));

            // If there was already a marker in that position,
            // the new one is offset so that it appears next to it.
            // While loop: when there are multiple events in same position,
            // should apply multiple offsets.
            while (lookForMarkerInSamePosition(m, mMarkersList) > -1) {
                LatLng newLatLong = new LatLng(m.getPosition().latitude, m.getPosition().longitude+0.00005);
                m.setPosition(newLatLong);
            }

            // Store marker
            m.setTag(i);
            mMarkersList.add(m);
        }
    }

    private int lookForMarkerInSamePosition(Marker m, ArrayList<Marker> markersList) {
        for (int i = 0; i < markersList.size(); i++)
            if (markersList.get(i).getPosition().equals(m.getPosition()))
                return i;
        return -1;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        // Move Camera
        centerCameraOnInit();
    }

    private void centerCameraOnInit() {
        showContent();
        mMap.setMinZoomPreference(20); //Buildings
        mMap.setMinZoomPreference(5); //Continent
        String myUserId = Utiles.getCurrentUserId();
        if (myUserId != null) {
            LatLng myCityLatLong = UtilesPreferences.getCurrentUserCityCoords(getActivityContext());

            if (myCityLatLong.latitude == 0 && myCityLatLong.longitude == 0)
                myCityLatLong = new LatLng(UtilesPreferences.CACERES_LATITUDE, UtilesPreferences.CACERES_LONGITUDE);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(myCityLatLong));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer position = (Integer) marker.getTag();
        if (position != null) {
            Event event = mEventsList.get(position);

            // Move camera
            LatLng southwest = new LatLng(event.getCoord_latitude()-0.00135, event.getCoord_longitude()-0.00135);
            LatLng northeast = new LatLng(event.getCoord_latitude()+0.00135, event.getCoord_longitude()+0.00135);
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
            Event event = mEventsList.get(position);

            // Open event's details
            Fragment newFragment = DetailEventFragment.newInstance(event.getEvent_id());
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
        if (context instanceof ActivityContracts.ActionBarIconManagement)
            mActionBarIconManagementListener = (ActivityContracts.ActionBarIconManagement) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideSoftKeyboard();
        mFragmentManagementListener = null;
        mActionBarIconManagementListener = null;
    }

    public void showContent() {
        if (mFragmentManagementListener != null)
            mFragmentManagementListener.showContent();
    }

    public void hideContent() {
        if (mFragmentManagementListener != null)
            mFragmentManagementListener.hideContent();
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
