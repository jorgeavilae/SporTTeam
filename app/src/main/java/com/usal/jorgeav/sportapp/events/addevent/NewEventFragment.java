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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Places;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.PlaceAutocompleteAdapter;
import com.usal.jorgeav.sportapp.events.addevent.selectfield.SelectFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.EventsActivity;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

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
    @BindView(R.id.new_event_name)
    EditText newEventName;
    @BindView(R.id.new_event_city)
    EditText newEventCity;
    @BindView(R.id.new_event_autocomplete_city)
    AutoCompleteTextView newEventAutocompleteCity;
    @BindView(R.id.new_event_date)
    EditText newEventDate;
    @BindView(R.id.new_event_time)
    EditText newEventTime;
    @BindView(R.id.new_event_total)
    EditText newEventTotal;
    @BindView(R.id.new_event_empty)
    EditText newEventEmpty;
    HashMap<String, Boolean> mParticipants;

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

    public static NewEventFragment newInstance(@Nullable String eventId) {
        NewEventFragment nef = new NewEventFragment();
        if (eventId != null) {
            Bundle b = new Bundle();
            b.putString(BUNDLE_EVENT_ID, eventId);
            nef.setArguments(b);
        }
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
                    ((EventsActivity)getActivity()).newEventFieldSelected,
                    newEventName.getText().toString(),
                    //todo cambiar ciudad ((EventsActivity)getActivity()).newEventCitySelected,
                    "ciudad",
                    newEventDate.getText().toString(),
                    newEventTime.getText().toString(),
                    newEventTotal.getText().toString(),
                    newEventEmpty.getText().toString(),
                    mParticipants);
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_event, container, false);
        ButterKnife.bind(this, root);

        setAutocompleteTextView();

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

        return root;
    }

    private void setAutocompleteTextView() {
        // Set up the adapter that will retrieve suggestions from
        // the Places Geo Data API that cover Spain
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                /*https://developers.google.com/android/reference/com/google/android/gms/location/places/AutocompleteFilter.Builder.html#setCountry(java.lang.String)*/
                .setCountry("ES"/*https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#ES*/)
                .build();

        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, null, typeFilter);
        newEventAutocompleteCity.setAdapter(mAdapter);

        newEventAutocompleteCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                /*
                 Retrieve the place ID of the selected item from the Adapter.
                 The adapter stores each Place suggestion in a AutocompletePrediction from which we
                 read the place ID and title.
                  */
                AutocompletePrediction item = mAdapter.getItem(position);
                if (item != null) {
                    CharSequence primaryText = item.getPrimaryText(null);
                    Log.i(TAG, "Autocomplete item selected: " + primaryText);
                    ((EventsActivity)getActivity()).setCity(primaryText.toString());
                }
            }
        });
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
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentManagementListener.showContent();
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
    public void showEventPlace(String place) {
        if (place != null && !TextUtils.isEmpty(place) && getActivity() instanceof SelectFieldFragment.OnFieldSelected)
            ((SelectFieldFragment.OnFieldSelected)getActivity()).retrieveFieldSelected(place);
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
    public void showEventCity(String city) {
        if (city != null && !TextUtils.isEmpty(city)) {
            newEventCity.setText(city);
            ((EventsActivity) getActivity()).setCity(city);
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
    public void clearUI() {
        newEventSport.setSelection(0);
        ((SelectFieldFragment.OnFieldSelected)getActivity()).retrieveFieldSelected("");
        newEventName.setText("");
        newEventDate.setText("");
        newEventTime.setText("");
        newEventCity.setText("");
        ((EventsActivity) getActivity()).setCity("");
        newEventTotal.setText("");
        newEventEmpty.setText("");
    }
}
