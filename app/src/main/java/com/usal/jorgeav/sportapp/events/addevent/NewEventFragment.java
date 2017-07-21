package com.usal.jorgeav.sportapp.events.addevent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.PlaceAutocompleteAdapter;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
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
                    ((EventsActivity)getActivity()).newEventFieldSelected,
                    ((EventsActivity)getActivity()).newEventFieldSelectedCoord,
                    newEventName.getText().toString(),
                    ((EventsActivity)getActivity()).newEventCityName,
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

    private void setSportLayout(String sportId) {
        // Check if the sport doesn't need a field
        String[] arraySports = getActivityContext().getResources().getStringArray(R.array.sport_id);
        if (sportId.equals(arraySports[0]) || sportId.equals(arraySports[1])) { // Running & Biking
            //TODO mostrar automcomplete para direcciones
            //TODO ocultar seleccionar pista en el mapa
        } else {
            //TODO mostrar seleccionar pista en el mapa
            //TODO ocultar autocompletar direcciones
        }
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
                    Log.i(TAG, "Autocomplete item selected: " + item.getPlaceId());
                    Places.GeoDataApi.getPlaceById(mGoogleApiClient, item.getPlaceId())
                            .setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(@NonNull PlaceBuffer places) {
                                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                        Place myPlace = places.get(0);
                                        ((EventsActivity)getActivity())
                                                .setCity(myPlace.getName().toString());
                                        Log.i(TAG, "Place found: Name - " + myPlace.getName());
                                    } else {
                                        Log.e(TAG, "Place not found");
                                    }
                                    places.release();
                                }
                            });
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

        if (getArguments() != null && getArguments().containsKey(BUNDLE_SPORT_SELECTED_ID))
            showEventSport(getArguments().getString(BUNDLE_SPORT_SELECTED_ID));
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentManagementListener.showContent();

        /* https://stackoverflow.com/a/13723367/4235666 */
        newEventAutocompleteCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ((EventsActivity)getActivity()).setCity(null);
            }
        });
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
        Log.d(TAG, "showEventField: "+fieldId);
        Log.d(TAG, "showEventField: "+city);
        Log.d(TAG, "showEventField: "+ coordinates);
        if (fieldId != null && !TextUtils.isEmpty(fieldId) && getActivity() instanceof SelectFieldFragment.OnFieldSelected)
            ((SelectFieldFragment.OnFieldSelected)getActivity()).retrieveFieldSelected(fieldId, city, coordinates);

        if (city != null && !TextUtils.isEmpty(city)) {
            newEventAutocompleteCity.setText(city);
            ((EventsActivity) getActivity()).setCity(city);
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
    public void clearUI() {
        newEventSport.setSelection(0);
        ((SelectFieldFragment.OnFieldSelected)getActivity()).retrieveFieldSelected("", "", null);
        newEventName.setText("");
        newEventDate.setText("");
        newEventTime.setText("");
        newEventAutocompleteCity.getText().clear();
        ((EventsActivity) getActivity()).setCity("");
        newEventTotal.setText("");
        newEventEmpty.setText("");
    }
}
