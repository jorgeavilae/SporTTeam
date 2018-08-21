package com.usal.jorgeav.sportapp.alarms.alarmdetail;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.alarms.addalarm.NewAlarmFragment;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.fields.fielddetail.DetailFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.BaseActivity;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailAlarmFragment extends BaseFragment implements DetailAlarmContract.View, EventsAdapter.OnEventItemClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = DetailAlarmFragment.class.getSimpleName();
    public static final String BUNDLE_ALARM_ID = "BUNDLE_ALARM_ID";

    private static String mAlarmId = "";
    private static String mSportId = "";
    private DetailAlarmContract.Presenter mPresenter;

    // Store Field's coordinates for rotations
    public static final String INSTANCE_COORDS = "INSTANCE_COORDS";
    LatLng mCoords;

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

        mCoords = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_COORDS))
            mCoords = savedInstanceState.getParcelable(INSTANCE_COORDS);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_ALARM_ID))
            mAlarmId = getArguments().getString(BUNDLE_ALARM_ID);

        //Need to be MapView, not SupportMapFragment https://stackoverflow.com/a/19354359/4235666
        if (alarmMap != null) alarmMap.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (alarmMap != null) alarmMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                Utiles.setCoordinatesInMap(getActivityContext(), mMap, mCoords);
            }
        });

        eventsAdapter = new EventsAdapter(null, this, Glide.with(this));
        eventsCoincidenceList.setAdapter(eventsAdapter);
        eventsCoincidenceList.setHasFixedSize(true);
        eventsCoincidenceList.setLayoutManager(new LinearLayoutManager(getActivityContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.alarm_detail_title), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (alarmMap != null) alarmMap.onStart();
        mPresenter.openAlarm(getLoaderManager(), getArguments());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (alarmMap != null) alarmMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (alarmMap != null) alarmMap.onPause();
        eventsAdapter.replaceData(null);
    }

    @Override
    public void showAlarmSport(String sport) {
        if (sport != null) {
            Glide.with(this)
                    .load(Utiles.getSportIconFromResource(sport))
                    .into(imageViewAlarmSport);
            mSportId = sport;
        }

    }

    @Override
    public void showAlarmPlace(Field field, String city) {
        if (field != null) {
            final String fieldId = field.getId();
            this.textViewAlarmPlace.setText(field.getName() + ", " + field.getCity());
            this.textViewAlarmPlace.setClickable(true);
            this.textViewAlarmPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment newFragment = DetailFieldFragment.newInstance(fieldId, true);
                    mFragmentManagementListener.initFragment(newFragment, true);
                }
            });
            mCoords = new LatLng(field.getCoord_latitude(), field.getCoord_longitude());
            Utiles.setCoordinatesInMap(getActivityContext(), mMap, mCoords);
        } else if (city != null && !TextUtils.isEmpty(city)) {
            this.textViewAlarmPlace.setText(city);
            this.textViewAlarmPlaceIcon.setVisibility(View.INVISIBLE);
            mCoords = null;
        }
    }

    @Override
    public void showAlarmDate(Long dateFrom, Long dateTo) {
        ((BaseActivity) getActivity()).showContent();

        this.textViewAlarmDateFrom.setText(UtilesTime.millisToDateStringShort(dateFrom));

        if (dateTo != null && dateTo > -1) {
            this.textViewAlarmDateTo.setText(UtilesTime.millisToDateStringShort(dateTo));
            if (dateTo < System.currentTimeMillis()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.old_alarm)
                        .setPositiveButton(android.R.string.ok, null);
                builder.create().show();
            }
        } else
            this.textViewAlarmDateTo.setText(R.string.forever);
    }

    @Override
    public void showAlarmTotalPlayers(Long totalPlayersFrom, Long totalPlayersTo) {
        ((BaseActivity) getActivity()).showContent();

        if (totalPlayersFrom != null && totalPlayersFrom > -1)
            this.textViewAlarmTotalFrom.setText(String.format(Locale.getDefault(), "%d", totalPlayersFrom));
        else
            this.textViewAlarmTotalFrom.setText(R.string.unspecified);

        if (totalPlayersTo != null && totalPlayersTo > -1)
            this.textViewAlarmTotalTo.setText(String.format(Locale.getDefault(), "%d", totalPlayersTo));
        else
            this.textViewAlarmTotalTo.setText(R.string.unspecified);
    }

    @Override
    public void showAlarmEmptyPlayers(Long emptyPlayersFrom, Long emptyPlayersTo) {
        ((BaseActivity) getActivity()).showContent();

        if (emptyPlayersFrom != null && emptyPlayersFrom > -1)
            this.textViewAlarmEmptyFrom.setText(String.format(Locale.getDefault(), "%d", emptyPlayersFrom));
        else
            this.textViewAlarmEmptyFrom.setText(R.string.unspecified);

        if (emptyPlayersTo != null && emptyPlayersTo > -1)
            this.textViewAlarmEmptyTo.setText(String.format(Locale.getDefault(), "%d", emptyPlayersTo));
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
                NotificationsFirebaseActions.deleteNotification(myUserId, mAlarmId + FirebaseDBContract.User.ALARMS);
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
        this.mCoords = null;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCoords != null)
            outState.putParcelable(INSTANCE_COORDS, mCoords);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alarmMap != null) alarmMap.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (alarmMap != null) alarmMap.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (alarmMap != null) alarmMap.onLowMemory();
    }
}
