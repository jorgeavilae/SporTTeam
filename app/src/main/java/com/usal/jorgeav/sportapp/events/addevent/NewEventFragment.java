package com.usal.jorgeav.sportapp.events.addevent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.PlaceAutocompleteAdapter;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
import com.usal.jorgeav.sportapp.events.addevent.selectfield.SelectFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.EventsActivity;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class NewEventFragment extends BaseFragment implements NewEventContract.View  {
    public static final String TAG = NewEventFragment.class.getSimpleName();
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";
    public static final String BUNDLE_SPORT_SELECTED_ID = "BUNDLE_SPORT_SELECTED_ID";

    NewEventContract.Presenter mNewEventPresenter;
    private static boolean sInitialize;

    // Static prevent double initialization with same ID
    private static GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;

    ArrayAdapter<CharSequence> sportsAdapter;
    @BindView(R.id.new_event_sport)
    Spinner newEventSport;
    @BindView(R.id.new_event_field)
    Button newEventFieldButton;
    @BindView(R.id.new_event_address)
    TextView newEventAddress;
    @BindView(R.id.new_event_name)
    EditText newEventName;
//    @BindView(R.id.new_event_autocomplete_city)
//    AutoCompleteTextView newEventAutocompleteCity;
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
            newEventDate.setText(UtilesTime.calendarToDate(myCalendar.getTime()));
        }
    };
    TimePickerDialog timePickerDialog;
    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hour);
            myCalendar.set(Calendar.MINUTE, minute);
            newEventTime.setText(UtilesTime.calendarToTime(myCalendar.getTime()));
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
            Log.d(TAG, "onOptionsItemSelected: Ok");

            String eventId = "";
            if (getArguments() != null && getArguments().containsKey(BUNDLE_EVENT_ID))
                eventId = getArguments().getString(BUNDLE_EVENT_ID);

            mNewEventPresenter.addEvent(
                    eventId,
                    newEventSport.getSelectedItem().toString(),
                    ((EventsActivity)getActivity()).mPlaceSelected.getAddress(),//TOdo sustituir por fieldID
                    ((EventsActivity)getActivity()).mPlaceSelected.getCoordinates(),
                    newEventName.getText().toString(),
                    ((EventsActivity)getActivity()).mPlaceSelected.getShortNameLocality(),
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

        myCalendar = Calendar.getInstance();

        sportsAdapter = ArrayAdapter.createFromResource(getActivityContext(), R.array.sport_id, android.R.layout.simple_spinner_item);
        sportsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newEventSport.setAdapter(sportsAdapter);

        newEventFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = SelectFieldFragment.newInstance(getSportSelected());
                mFragmentManagementListener.initFragment(fragment, true);
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

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Nuevo evento", this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (sInitialize) return;
        mNewEventPresenter.openEvent(getLoaderManager(), getArguments());
        sInitialize = true;

        if (getArguments() != null && getArguments().containsKey(BUNDLE_SPORT_SELECTED_ID)) {
            setSportLayout(getArguments().getString(BUNDLE_SPORT_SELECTED_ID));
        } else {
            showContent();
        }
    }

    private void setSportLayout(String sportId) {
        Log.d(TAG, "setSportLayout: "+sportId);

        //Set sport in Spinner
        showEventSport(sportId);

        // Check if the sport doesn't need a field
        String[] arraySports = getActivityContext().getResources().getStringArray(R.array.sport_id);
        if (sportId.equals(arraySports[0]) || sportId.equals(arraySports[1])) { // Running & Biking
            ((EventsActivity)getActivity()).startMapActivityForResult(null);
        } else {
            // Sport needs a Field so load from ContentProvider and start MapActivity in retrieveFields()
            mNewEventPresenter.loadFields(getLoaderManager(), getArguments());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (datePickerDialog != null && datePickerDialog.isShowing()) datePickerDialog.dismiss();
        if (timePickerDialog != null && timePickerDialog.isShowing()) timePickerDialog.dismiss();
    }

    private String getSportSelected() {
        return newEventSport.getSelectedItem().toString();
    }

    @Override
    public void showEventSport(String sport) {
        if (sport != null && !TextUtils.isEmpty(sport))
            newEventSport.setSelection(sportsAdapter.getPosition(sport));
    }

    @Override
    public void showEventField(String fieldId, String city, LatLng coordinates) {
        if (city != null && !TextUtils.isEmpty(city)) {
            newEventAddress.setText(city);
        }
    }

    @Override
    public void showEventName(String name) {
        if (name != null && !TextUtils.isEmpty(name))
        newEventName.setText(name);
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
        showContent();
        if (fieldList != null) {
            ((EventsActivity) getActivity()).startMapActivityForResult(fieldList);
        } else {
            Toast.makeText(getActivityContext(), "There isn't fields for this sport", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void clearUI() {
        newEventSport.setSelection(0);
        ((EventsActivity)getActivity()).mPlaceSelected = null;
        newEventName.setText("");
        newEventDate.setText("");
        newEventTime.setText("");
        newEventAddress.setText("");
        newEventTotal.setText("");
        newEventEmpty.setText("");
    }
}
