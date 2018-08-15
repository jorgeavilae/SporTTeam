package com.usal.jorgeav.sportapp.events.addevent;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
import com.usal.jorgeav.sportapp.mainactivities.EventsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewEventFragment extends BaseFragment implements NewEventContract.View {
    public static final String TAG = NewEventFragment.class.getSimpleName();

    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";
    public static final String BUNDLE_SPORT_SELECTED_ID = "BUNDLE_SPORT_SELECTED_ID";

    NewEventContract.Presenter mNewEventPresenter;
    private static boolean sInitialize;

    // Static prevent double initialization with same ID
    private static GoogleApiClient mGoogleApiClient;
    public static final String INSTANCE_FIELD_LIST_ID = "INSTANCE_FIELD_LIST_ID";
    ArrayList<Field> mFieldList;
    public static final String INSTANCE_FRIENDS_LIST_ID = "INSTANCE_FRIENDS_LIST_ID";
    ArrayList<String> mFriendsList;

    @BindView(R.id.new_event_map)
    MapView newEventMap;
    private GoogleMap mMap;
    @BindView(R.id.new_event_sport)
    ImageView newEventSport;
    String mSportId = "";
    @BindView(R.id.new_event_address)
    TextView newEventAddress;
    @BindView(R.id.new_event_field_button)
    Button newEventFieldButton;
    @BindView(R.id.new_event_name)
    EditText newEventName;
    @BindView(R.id.new_event_date)
    EditText newEventDate;
    @BindView(R.id.new_event_time)
    EditText newEventTime;
    @BindView(R.id.new_event_total)
    EditText newEventTotal;
    @BindView(R.id.new_event_empty)
    EditText newEventEmpty;
    @BindView(R.id.new_event_infinite_players)
    CheckBox newEventInfinitePlayers;
    HashMap<String, Boolean> mParticipants;
    HashMap<String, SimulatedUser> mSimulatedParticipants;

    Calendar myCalendar;
    DatePickerDialog datePickerDialog;
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            newEventDate.setText(UtilesTime.millisToDateString(myCalendar.getTimeInMillis()));
        }
    };
    TimePickerDialog timePickerDialog;
    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hour);
            myCalendar.set(Calendar.MINUTE, minute);
            newEventTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        }
    };

    public NewEventFragment() {
        // Required empty public constructor
    }

    public static NewEventFragment newInstance(@Nullable String eventId, @Nullable String sportId) {
        NewEventFragment nef = new NewEventFragment();
        Bundle b = new Bundle();
        if (eventId != null)
            b.putString(BUNDLE_EVENT_ID, eventId);
        if (sportId != null)
            b.putString(BUNDLE_SPORT_SELECTED_ID, sportId);
        nef.setArguments(b);
        sInitialize = false;
        return nef;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (mGoogleApiClient == null)
            mGoogleApiClient = new GoogleApiClient
                    .Builder(getActivity())
                    .addApi(Places.GEO_DATA_API)
                    .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.e(TAG, "onConnectionFailed: Google Api Client is not connected");
                        }
                    })
                    .build();
        else mGoogleApiClient.connect();

        mNewEventPresenter = new NewEventPresenter(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_ok, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        hideSoftKeyboard();
        if (item.getItemId() == R.id.action_ok) {
            String eventId = "";
            if (getArguments() != null && getArguments().containsKey(BUNDLE_EVENT_ID))
                eventId = getArguments().getString(BUNDLE_EVENT_ID);

            mNewEventPresenter.addEvent(
                    eventId,
                    mSportId,
                    ((EventsActivity) getActivity()).mFieldId,
                    ((EventsActivity) getActivity()).mAddress,
                    ((EventsActivity) getActivity()).mCoord,
                    newEventName.getText().toString(),
                    ((EventsActivity) getActivity()).mCity,
                    newEventDate.getText().toString(),
                    newEventTime.getText().toString(),
                    newEventTotal.getText().toString(),
                    newEventEmpty.getText().toString(),
                    mParticipants, mSimulatedParticipants, mFriendsList);
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_event, container, false);
        ButterKnife.bind(this, root);

        //Need to be MapView, not SupportMapFragment https://stackoverflow.com/a/19354359/4235666
        newEventMap.onCreate(savedInstanceState);
        try { MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) { e.printStackTrace(); }
        newEventMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Coordinates selected previously
                Utiles.setCoordinatesInMap(getActivityContext(), mMap, ((EventsActivity)getActivity()).mCoord);
            }
        });

        if (getArguments() != null && getArguments().containsKey(BUNDLE_SPORT_SELECTED_ID))
            setSportLayout(getArguments().getString(BUNDLE_SPORT_SELECTED_ID));

        myCalendar = Calendar.getInstance();

        newEventFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean onlyFields = true;
                if (!Utiles.sportNeedsField(mSportId)) onlyFields = false;
                ((EventsActivity) getActivity()).startMapActivityForResult(mFieldList, onlyFields);
            }
        });

        newEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(getActivityContext(), dateSetListener,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
            }
        });

        newEventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog = new TimePickerDialog(getActivityContext(), timeSetListener, myCalendar
                        .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        newEventInfinitePlayers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    newEventEmpty.setEnabled(false);
                    newEventEmpty.setText(R.string.infinite);
                } else {
                    newEventEmpty.setEnabled(true);
                    newEventEmpty.setText("");
                }
            }
        });

        hideContent();

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FIELD_LIST_ID))
            mFieldList = savedInstanceState.getParcelableArrayList(INSTANCE_FIELD_LIST_ID);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FRIENDS_LIST_ID))
            mFriendsList = savedInstanceState.getStringArrayList(INSTANCE_FRIENDS_LIST_ID);

        //Show newField dialog on rotation if needed, after retrieveFields are called
        if (mFieldList != null && mFieldList.size() == 0 && Utiles.sportNeedsField(mSportId))
            startNewFieldDialog();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.action_create_event), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        newEventMap.onStart();
        if (!sInitialize) {
            mNewEventPresenter.openEvent(getLoaderManager(), getArguments());

            // Only need to show MapActivity on init once, not on rotation
            // Load Fields from ContentProvider and start MapActivity in retrieveFields()
            mNewEventPresenter.loadFields(getLoaderManager(), getArguments());

            // Just load friends once
            mNewEventPresenter.loadFriends(getLoaderManager(), getArguments());

            sInitialize = true;
        } else {
            showContent();
        }
    }

    private void setSportLayout(String sportId) {
        // Show sport
        showEventSport(sportId);

        if (Utiles.sportNeedsField(sportId))
            newEventInfinitePlayers.setVisibility(View.INVISIBLE);
        else
            newEventInfinitePlayers.setVisibility(View.VISIBLE);
    }



    @Override
    public void onPause() {
        super.onPause();
        newEventMap.onPause();
        if (datePickerDialog != null && datePickerDialog.isShowing()) datePickerDialog.dismiss();
        if (timePickerDialog != null && timePickerDialog.isShowing()) timePickerDialog.dismiss();
    }

    @Override
    public void showEventSport(String sport) {
        mSportId = sport;
        if (sport != null && !TextUtils.isEmpty(sport))
            Glide.with(this).load(Utiles.getSportIconFromResource(sport)).into(newEventSport);
    }

    @Override
    public void showEventField(String fieldId, String address, String city, LatLng coordinates) {
        if (address != null && !TextUtils.isEmpty(address))
            newEventAddress.setText(address);

        Utiles.setCoordinatesInMap(getActivityContext(), mMap, coordinates);

        ((EventsActivity) getActivity()).mFieldId = fieldId;
        ((EventsActivity) getActivity()).mAddress = address;
        ((EventsActivity) getActivity()).mCity = city;
        ((EventsActivity) getActivity()).mCoord = coordinates;
    }

    @Override
    public void showEventName(String name) {
        if (name != null && !TextUtils.isEmpty(name)) {
            newEventName.setText(name);
            mActionBarIconManagementListener.setActionBarTitle(name);
        }
    }

    @Override
    public void showEventDate(long date) {
        if (date > -1) {
            newEventDate.setText(UtilesTime.millisToDateString(date));
            newEventTime.setText(UtilesTime.millisToTimeString(date));
        }
    }

    @Override
    public void showEventTotalPlayers(int totalPlayers) {
        if (totalPlayers > -1)
            newEventTotal.setText(String.format(Locale.getDefault(), "%d", totalPlayers));
    }

    @Override
    public void showEventEmptyPlayers(int emptyPlayers) {
        if (emptyPlayers > -1)
            newEventEmpty.setText(String.format(Locale.getDefault(), "%d", emptyPlayers));
    }

    // To not lose participants list on Event edits
    @Override
    public void setParticipants(HashMap<String, Boolean> map) {
        mParticipants = map;
    }
    @Override
    public void setSimulatedParticipants(HashMap<String, SimulatedUser> map) {
        mSimulatedParticipants = map;
    }

    @Override
    public void retrieveFields(ArrayList<Field> fieldList) {
        mFieldList = fieldList;
        showContent();
        if (mFieldList != null) {
            if (!getArguments().containsKey(BUNDLE_EVENT_ID)) {// If not an edit
                if (Utiles.sportNeedsField(mSportId)) { // If new Event need a Field
                    if (mFieldList.size() == 0)
                        // Sport needs a Field so create a new one or cancel Event creation
                        startNewFieldDialog();
                    else
                        ((EventsActivity) getActivity()).startMapActivityForResult(mFieldList, true);
                } else
                    ((EventsActivity) getActivity()).startMapActivityForResult(mFieldList, false);
            }
        }

        //Since mFieldList are going to be retained in savedInstance there isn't need to be loaded again
        mNewEventPresenter.stopLoadFields(getLoaderManager());
    }

    @Override
    public void retrieveFriendsID(ArrayList<String> friendsIdList) {
        mFriendsList = friendsIdList;

        //Since mFieldList are going to be retained in savedInstance there isn't need to be loaded again
        mNewEventPresenter.stopLoadFriends(getLoaderManager());
    }

    private void startNewFieldDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setTitle(R.string.dialog_title_create_new_field)
                .setMessage(R.string.dialog_msg_create_new_field_for_event)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utiles.startFieldsActivityAndNewField(getActivity());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().onBackPressed();
                    }
                })
                .setCancelable(false);
        builder.create().show();
    }

    @Override
    public void clearUI() {
        newEventSport.setVisibility(View.INVISIBLE);
        ((EventsActivity) getActivity()).mFieldId = null;
        ((EventsActivity) getActivity()).mAddress = null;
        ((EventsActivity) getActivity()).mCity = null;
        ((EventsActivity) getActivity()).mCoord = null;
        newEventName.setText("");
        newEventDate.setText("");
        newEventTime.setText("");
        newEventAddress.setText("");
        newEventTotal.setText("");
        newEventEmpty.setText("");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFieldList != null)
            outState.putParcelableArrayList(INSTANCE_FIELD_LIST_ID, mFieldList);
        if (mFriendsList != null)
            outState.putStringArrayList(INSTANCE_FRIENDS_LIST_ID, mFriendsList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // If user go back and pick other sport, we need to clear this variables
        ((EventsActivity) getActivity()).mFieldId = null;
        ((EventsActivity) getActivity()).mAddress = null;
        ((EventsActivity) getActivity()).mCity = null;
        ((EventsActivity) getActivity()).mCoord = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        newEventMap.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        newEventMap.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        newEventMap.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        newEventMap.onLowMemory();
    }
}
