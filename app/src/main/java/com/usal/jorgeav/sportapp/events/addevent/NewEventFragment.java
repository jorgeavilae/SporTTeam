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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

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

    @BindView(R.id.new_event_sport)
    TextView newEventSport;
    String mSportId = "";
    @BindView(R.id.new_event_field)
    Button newEventFieldButton;
    @BindView(R.id.new_event_address)
    TextView newEventAddress;
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
            long time = TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute);
            newEventTime.setText(UtilesTime.millisToTimeString(time));
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
                    newEventSport.getText().toString(),
                    ((EventsActivity) getActivity()).mFieldId,
                    ((EventsActivity) getActivity()).mAddress,
                    ((EventsActivity) getActivity()).mCoord,
                    newEventName.getText().toString(),
                    ((EventsActivity) getActivity()).mCity,
                    newEventDate.getText().toString(),
                    newEventTime.getText().toString(),
                    newEventTotal.getText().toString(),
                    newEventEmpty.getText().toString(),
                    mParticipants, mSimulatedParticipants);
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_event, container, false);
        ButterKnife.bind(this, root);

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
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
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

        hideContent();

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FIELD_LIST_ID))
            mFieldList = savedInstanceState.getParcelableArrayList(INSTANCE_FIELD_LIST_ID);

        showEventField(((EventsActivity) getActivity()).mFieldId,
                ((EventsActivity) getActivity()).mAddress,
                ((EventsActivity) getActivity()).mCity,
                ((EventsActivity) getActivity()).mCoord);

        //Show newField dialog on rotation if needed, after retrieveFields are called
        if (mFieldList != null && mFieldList.size() == 0) startNewFieldDialog();

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
        if (!sInitialize) {
            mNewEventPresenter.openEvent(getLoaderManager(), getArguments());

            // Only need to show MapActivity on init once, not on rotation
            // Load Fields from ContentProvider and start MapActivity in retrieveFields()
            mNewEventPresenter.loadFields(getLoaderManager(), getArguments());

            sInitialize = true;
        } else {
            showContent();
        }
    }

    private void setSportLayout(String sportId) {
        // Show sport
        showEventSport(sportId);
    }



    @Override
    public void onPause() {
        super.onPause();
        if (datePickerDialog != null && datePickerDialog.isShowing()) datePickerDialog.dismiss();
        if (timePickerDialog != null && timePickerDialog.isShowing()) timePickerDialog.dismiss();
    }

    @Override
    public void showEventSport(String sport) {
        mSportId = sport;
        if (sport != null && !TextUtils.isEmpty(sport))
            newEventSport.setText(sport);
    }

    @Override
    public void showEventField(String fieldId, String address, String city, LatLng coordinates) {
        //TODO mostrar datos mejor
        if (city != null && !TextUtils.isEmpty(city)) {
            newEventAddress.setText(address);
        }

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
        newEventSport.setText("");
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((EventsActivity) getActivity()).mFieldId = null;
        ((EventsActivity) getActivity()).mAddress = null;
        ((EventsActivity) getActivity()).mCity = null;
        ((EventsActivity) getActivity()).mCoord = null;
    }
}
