package com.usal.jorgeav.sportapp.fields.addfield;

import android.app.TimePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.PlaceAutocompleteAdapter;
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

public class NewFieldFragment extends BaseFragment implements NewFieldContract.View  {
    public static final String TAG = NewFieldFragment.class.getSimpleName();
    public static final String BUNDLE_FIELD_ID = "BUNDLE_FIELD_ID";

    NewFieldContract.Presenter mNewFieldPresenter;
    private static boolean sInitialize;

    // Static prevent double initialization with same ID
    private static GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;

    ArrayAdapter<CharSequence> sportsAdapter;
    @BindView(R.id.new_field_sport)
    Spinner newFieldSport;
    @BindView(R.id.new_field_address)
    AutoCompleteTextView newFieldAutocompleteAddress;
    @BindView(R.id.new_field_name)
    EditText newFieldName;
    @BindView(R.id.new_field_open_time)
    EditText newFieldOpenTime;
    @BindView(R.id.new_field_close_time)
    EditText newFieldCloseTime;
    @BindView(R.id.new_field_rate)
    RatingBar newFieldRate;

    Calendar myCalendar;
    TimePickerDialog openTimePickerDialog;
    TimePickerDialog.OnTimeSetListener openTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hour);
            myCalendar.set(Calendar.MINUTE, minute);
            newFieldOpenTime.setText(UtilesTime.calendarToTime(myCalendar.getTime()));
        }
    };
    TimePickerDialog closeTimePickerDialog;
    TimePickerDialog.OnTimeSetListener closeTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hour);
            myCalendar.set(Calendar.MINUTE, minute);
            newFieldCloseTime.setText(UtilesTime.calendarToTime(myCalendar.getTime()));
        }
    };

    public NewFieldFragment() {
        // Required empty public constructor
    }

    public static NewFieldFragment newInstance(@Nullable String fieldId) {
        NewFieldFragment nff = new NewFieldFragment();
        if (fieldId != null) {
            Bundle b = new Bundle();
            b.putString(BUNDLE_FIELD_ID, fieldId);
            nff.setArguments(b);
        }
        sInitialize = false;
        return nff;
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

        mNewFieldPresenter = new NewFieldPresenter(this);
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

            String fieldId = "";
            if (getArguments() != null && getArguments().containsKey(BUNDLE_FIELD_ID))
                fieldId = getArguments().getString(BUNDLE_FIELD_ID);

//            mNewFieldPresenter.addField(
//                    fieldId,
//                    newFieldSport.getSelectedItem().toString(),
//                    ((EventsActivity)getActivity()).newEventFieldSelected,
//                    newFieldName.getText().toString(),
//                    //todo cambiar ciudad ((EventsActivity)getActivity()).newEventCitySelected,
//                    "ciudad",
//                    .getText().toString(),
//                    newEventTime.getText().toString(),
//                    newEventTotal.getText().toString(),
//                    newEventEmpty.getText().toString(),
//                    mParticipants);
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_field, container, false);
        ButterKnife.bind(this, root);

        setAutocompleteTextView();

        myCalendar = Calendar.getInstance();

        sportsAdapter = ArrayAdapter.createFromResource(getActivityContext(), R.array.sport_id, android.R.layout.simple_spinner_item);
        sportsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newFieldSport.setAdapter(sportsAdapter);

        newFieldOpenTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePickerDialog = new TimePickerDialog(getActivityContext(), openTimeSetListener,
                        myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
                openTimePickerDialog.show();
            }
        });

        newFieldCloseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeTimePickerDialog = new TimePickerDialog(getActivityContext(), closeTimeSetListener,
                        myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
                closeTimePickerDialog.show();
            }
        });

        return root;
    }

    private void setAutocompleteTextView() {
//        // Set up the adapter that will retrieve suggestions from
//        // the Places Geo Data API that cover Spain
//        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
//                /*https://developers.google.com/android/reference/com/google/android/gms/location/places/AutocompleteFilter.Builder.html#setCountry(java.lang.String)*/
//                .setCountry("ES"/*https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#ES*/)
//                .build();
//
//        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, null, typeFilter);
//        newEventAutocompleteCity.setAdapter(mAdapter);
//
//        newEventAutocompleteCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                /*
//                 Retrieve the place ID of the selected item from the Adapter.
//                 The adapter stores each Place suggestion in a AutocompletePrediction from which we
//                 read the place ID and title.
//                  */
//                AutocompletePrediction item = mAdapter.getItem(position);
//                if (item != null) {
//                    CharSequence primaryText = item.getPrimaryText(null);
//                    Log.i(TAG, "Autocomplete item selected: " + primaryText);
//                    ((EventsActivity)getActivity()).setCity(primaryText.toString());
//                }
//            }
//        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Nuevo Campo", this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (sInitialize) return;
        mNewFieldPresenter.openField(getLoaderManager(), getArguments());
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
        if (openTimePickerDialog != null && openTimePickerDialog.isShowing()) openTimePickerDialog.dismiss();
        if (closeTimePickerDialog != null && closeTimePickerDialog.isShowing()) closeTimePickerDialog.dismiss();
    }

    private String getSportSelected() {
        return newFieldSport.getSelectedItem().toString();
    }

    @Override
    public void showEventSport(String sport) {
        if (sport != null && !TextUtils.isEmpty(sport))
            newEventSport.setSelection(sportsAdapter.getPosition(sport));
    }

    @Override
    public void showEventPlace(String place) {

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
        newEventName.setText("");
        newEventDate.setText("");
        newEventTime.setText("");
        newEventCity.setText("");
        ((EventsActivity) getActivity()).setCity("");
        newEventTotal.setText("");
        newEventEmpty.setText("");
    }
}
