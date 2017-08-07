package com.usal.jorgeav.sportapp.alarms.alarmdetail;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.alarms.addalarm.NewAlarmFragment;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.fields.detail.DetailFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.BaseActivity;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailAlarmFragment extends BaseFragment implements DetailAlarmContract.View, EventsAdapter.OnEventItemClickListener {
    private static final String TAG = DetailAlarmFragment.class.getSimpleName();
    public static final String BUNDLE_ALARM_ID = "BUNDLE_ALARM_ID";

    private static String mAlarmId = "";
    private static String mSportId = "";
    private DetailAlarmContract.Presenter mPresenter;

    @BindView(R.id.alarm_detail_map)
    MapView alarmMap;
    private GoogleMap mMap;
    @BindView(R.id.alarm_detail_sport)
    ImageView imageViewAlarmSport;
    @BindView(R.id.alarm_detail_place)
    TextView textViewAlarmPlace;
    @BindView(R.id.alarm_detail_place_icon)
    ImageView textViewAlarmPlaceIcon;
    @BindView(R.id.alarm_detail_date_from)
    TextView textViewAlarmDateFrom;
    @BindView(R.id.alarm_detail_date_to)
    TextView textViewAlarmDateTo;
    @BindView(R.id.alarm_detail_total_from)
    TextView textViewAlarmTotalFrom;
    @BindView(R.id.alarm_detail_total_to)
    TextView textViewAlarmTotalTo;
    @BindView(R.id.alarm_detail_empty_from)
    TextView textViewAlarmEmptyFrom;
    @BindView(R.id.alarm_detail_empty_to)
    TextView textViewAlarmEmptyTo;
    @BindView(R.id.alarm_detail_events_coincidence_list)
    RecyclerView eventsCoincidenceList;
    EventsAdapter eventsAdapter;
    @BindView(R.id.alarm_detail_events_placeholder)
    ConstraintLayout eventsCoincidencePlaceholder;

    public DetailAlarmFragment() {
        // Required empty public constructor
    }

    public static DetailAlarmFragment newInstance(@NonNull String alarmId) {
        DetailAlarmFragment fragment = new DetailAlarmFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_ALARM_ID, alarmId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPresenter = new DetailAlarmPresenter(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_edit_delete, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_edit) {
            if (mAlarmId != null && !TextUtils.isEmpty(mAlarmId)
                    && mSportId != null && !TextUtils.isEmpty(mSportId)) {
                Fragment fragment = NewAlarmFragment.newInstance(mAlarmId, mSportId);
                mFragmentManagementListener.initFragment(fragment, true);
            }
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            mPresenter.deleteAlarm(getArguments());
            resetBackStack();
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail_alarm, container, false);
        ButterKnife.bind(this, root);

        //Need to be MapView, not SupportMapFragment https://stackoverflow.com/a/19354359/4235666
        alarmMap.onCreate(savedInstanceState);
        alarmMap.onResume(); // needed to get the map to display immediately
        try { MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) { e.printStackTrace(); }
        alarmMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                //TODO set true coordinates
//                LatLng coords = ((FieldsActivity)getActivity()).mCoord;
                LatLng coords = new LatLng(UtilesPreferences.CACERES_LATITUDE, UtilesPreferences.CACERES_LONGITUDE);
                if (mMap != null && coords != null) {
                    // Add a marker, and move the camera.
                    float hue = Utiles.getFloatFromResources(getResources(), R.dimen.hue_of_colorSportteam_logo);
                    mMap.addMarker(new MarkerOptions().position(coords)
                            .icon(BitmapDescriptorFactory.defaultMarker(hue)));
                    LatLng southwest = new LatLng(coords.latitude - 0.00135, coords.longitude - 0.00135);
                    LatLng northeast = new LatLng(coords.latitude + 0.00135, coords.longitude + 0.00135);
                    LatLngBounds llb = new LatLngBounds(southwest, northeast);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(llb, 0));
                }
            }
        });

        if (getArguments() != null && getArguments().containsKey(BUNDLE_ALARM_ID))
            mAlarmId = getArguments().getString(BUNDLE_ALARM_ID);

        eventsAdapter = new EventsAdapter(null, this, Glide.with(this));
        eventsCoincidenceList.setAdapter(eventsAdapter);
        eventsCoincidenceList.setHasFixedSize(true);
        eventsCoincidenceList.setLayoutManager(new LinearLayoutManager(getActivityContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Detalles de alarma", this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.openAlarm(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        eventsAdapter.replaceData(null);
    }

    @Override
    public void showAlarmSport(String sport) {
        if (sport != null) {
            ((BaseActivity) getActivity()).showContent();
            int sportDrawableResource = getResources()
                    .getIdentifier(sport , "drawable", MyApplication.getAppContext().getPackageName());
            Glide.with(this)
                    .load(sportDrawableResource)
                    .into(imageViewAlarmSport);
            mSportId = sport;
        }

    }

    @Override
    public void showAlarmPlace(final String fieldId, String fieldName, String city) {

        if (city != null && !TextUtils.isEmpty(city)) {
            ((BaseActivity) getActivity()).showContent();
            if (fieldName == null) fieldName = "";
            if (!TextUtils.isEmpty(fieldName)) fieldName = fieldName + ", ";
            this.textViewAlarmPlace.setText(fieldName + city);
        }

        if (fieldId != null && !TextUtils.isEmpty(fieldId)) {
            textViewAlarmPlace.setClickable(true);
            textViewAlarmPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Fragment newFragment = DetailFieldFragment.newInstance(fieldId, true);
                mFragmentManagementListener.initFragment(newFragment, true);
                }
            });
        } else textViewAlarmPlaceIcon.setVisibility(View.INVISIBLE);

    }

    @Override
    public void showAlarmDate(Long dateFrom, Long dateTo) {
        ((BaseActivity) getActivity()).showContent();

        this.textViewAlarmDateFrom.setText(UtilesTime.millisToDateStringShort(dateFrom));

        if (dateTo != null && dateTo > -1)
            this.textViewAlarmDateTo.setText(UtilesTime.millisToDateStringShort(dateTo));
        else
            this.textViewAlarmDateTo.setText(R.string.forever);
    }

    @Override
    public void showAlarmTotalPlayers(Long totalPlayersFrom, Long totalPlayersTo) {
        ((BaseActivity) getActivity()).showContent();

        if (totalPlayersFrom != null && totalPlayersFrom > -1)
            this.textViewAlarmTotalFrom.setText(String.format(Locale.getDefault(), "%2d", totalPlayersFrom));
        else
            this.textViewAlarmTotalFrom.setText(R.string.unspecified);

        if (totalPlayersTo != null && totalPlayersTo > -1)
            this.textViewAlarmTotalTo.setText(String.format(Locale.getDefault(), "%2d", totalPlayersTo));
        else
            this.textViewAlarmTotalTo.setText(R.string.unspecified);
    }

    @Override
    public void showAlarmEmptyPlayers(Long emptyPlayersFrom, Long emptyPlayersTo) {
        ((BaseActivity) getActivity()).showContent();

        if (emptyPlayersFrom != null && emptyPlayersFrom > -1)
            this.textViewAlarmEmptyFrom.setText(String.format(Locale.getDefault(), "%2d", emptyPlayersFrom));
        else
            this.textViewAlarmEmptyFrom.setText(R.string.unspecified);

        if (emptyPlayersTo != null && emptyPlayersTo > -1)
            this.textViewAlarmEmptyTo.setText(String.format(Locale.getDefault(), "%2d", emptyPlayersTo));
        else
            this.textViewAlarmEmptyTo.setText(R.string.unspecified);
    }

    @Override
    public void showEvents(Cursor data) {
        eventsAdapter.replaceData(data);
        if (data != null && data.getCount() > 0) {
            eventsCoincidenceList.setVisibility(View.VISIBLE);
            eventsCoincidencePlaceholder.setVisibility(View.INVISIBLE);
        } else {
            String myUserId = Utiles.getCurrentUserId();
            if (TextUtils.isEmpty(myUserId))
                FirebaseActions.deleteNotification(myUserId, mAlarmId + FirebaseDBContract.User.ALARMS);
            eventsCoincidenceList.setVisibility(View.INVISIBLE);
            eventsCoincidencePlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void clearUI() {
        this.imageViewAlarmSport.setVisibility(View.INVISIBLE);
        this.textViewAlarmPlace.setText("");
        this.textViewAlarmPlace.setClickable(false);
        this.textViewAlarmPlace.setOnClickListener(null);
        this.textViewAlarmDateFrom.setText("");
        this.textViewAlarmDateTo.setText("");
        this.textViewAlarmTotalFrom.setText("");
        this.textViewAlarmTotalTo.setText("");
        this.textViewAlarmEmptyFrom.setText("");
        this.textViewAlarmEmptyTo.setText("");
    }

    @Override
    public void onEventClick(String eventId) {
        Fragment newFragment = DetailEventFragment.newInstance(eventId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
