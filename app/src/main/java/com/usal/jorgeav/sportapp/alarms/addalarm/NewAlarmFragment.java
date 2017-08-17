package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.PlaceAutocompleteAdapter;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.mainactivities.AlarmsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewAlarmFragment extends BaseFragment implements NewAlarmContract.View  {
    private static final String TAG = NewAlarmFragment.class.getSimpleName();

    public static final String BUNDLE_ALARM_ID = "BUNDLE_ALARM_ID";
    public static final String BUNDLE_SPORT_SELECTED_ID = "BUNDLE_SPORT_SELECTED_ID";
    public static final String INSTANCE_FIELD_LIST_ID = "INSTANCE_FIELD_LIST_ID";
    ArrayList<Field> mFieldList;

    // Static prevent double initialization with same ID
    private static GoogleApiClient mGoogleApiClient;
    PlaceAutocompleteAdapter mAdapter;

    NewAlarmContract.Presenter mNewAlarmPresenter;
    private static boolean sInitialize;
    String mSportId = "";

    @BindView(R.id.new_alarm_map)
    MapView newAlarmMap;
    private GoogleMap mMap;
    @BindView(R.id.new_alarm_sport)
    ImageView newAlarmSport;
    @BindView(R.id.new_alarm_field)
    TextView newAlarmField;
    @BindView(R.id.new_alarm_field_button)
    Button newAlarmFieldButton;
    @BindView(R.id.new_alarm_city)
    AutoCompleteTextView newAlarmCity;
    @BindView(R.id.new_alarm_date_from)
    EditText newAlarmDateFrom;
    @BindView(R.id.new_alarm_date_to)
    EditText newAlarmDateTo;
    @BindView(R.id.new_alarm_total_from)
    EditText newAlarmTotalFrom;
    @BindView(R.id.new_alarm_total_to)
    EditText newAlarmTotalTo;
    @BindView(R.id.new_alarm_empty_from)
    EditText newAlarmEmptyFrom;
    @BindView(R.id.new_alarm_empty_to)
    EditText newAlarmEmptyTo;
    @BindView(R.id.new_alarm_infinite_players)
    CheckBox newAlarmInfinitePlayers;

    Calendar myCalendar;
    DatePickerDialog datePickerDialogFrom;
    DatePickerDialog datePickerDialogTo;

    public NewAlarmFragment() {
        // Required empty public constructor
    }

    public static NewAlarmFragment newInstance(@Nullable String alarmId, @Nullable String sportId) {
        NewAlarmFragment naf = new NewAlarmFragment();
        Bundle b = new Bundle();
        if (alarmId != null)
            b.putString(BUNDLE_ALARM_ID, alarmId);
        if (sportId != null)
            b.putString(BUNDLE_SPORT_SELECTED_ID, sportId);
        naf.setArguments(b);
        sInitialize = false;
        return naf;
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

        mNewAlarmPresenter = new NewAlarmPresenter(this);
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
            String alarmId = "";
            if (getArguments() != null && getArguments().containsKey(BUNDLE_ALARM_ID))
                alarmId = getArguments().getString(BUNDLE_ALARM_ID);

            mNewAlarmPresenter.addAlarm(
                    alarmId,
                    mSportId,
                    ((AlarmsActivity)getActivity()).mFieldId,
                    ((AlarmsActivity)getActivity()).mCity,
                    newAlarmDateFrom.getText().toString(),
                    newAlarmDateTo.getText().toString(),
                    newAlarmTotalFrom.getText().toString(),
                    newAlarmTotalTo.getText().toString(),
                    newAlarmEmptyFrom.getText().toString(),
                    newAlarmEmptyTo.getText().toString());
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_alarm, container, false);
        ButterKnife.bind(this, root);

        //Need to be MapView, not SupportMapFragment https://stackoverflow.com/a/19354359/4235666
        newAlarmMap.onCreate(savedInstanceState);
        try { MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) { e.printStackTrace(); }
        newAlarmMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Coordinates selected previously
                Utiles.setCoordinatesInMap(getActivityContext(), mMap, ((AlarmsActivity)getActivity()).mCoord);
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FIELD_LIST_ID))
            mFieldList = savedInstanceState.getParcelableArrayList(INSTANCE_FIELD_LIST_ID);
        if (getArguments() != null && getArguments().containsKey(BUNDLE_SPORT_SELECTED_ID))
            setSportLayout(getArguments().getString(BUNDLE_SPORT_SELECTED_ID));

        newAlarmFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFieldList != null)
                    ((AlarmsActivity) getActivity()).startMapActivityForResult(mFieldList);
            }
        });

        myCalendar = Calendar.getInstance();
        newAlarmDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogFrom = new DatePickerDialog(
                        getActivityContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                newAlarmDateFrom.setText(UtilesTime.millisToDateString(myCalendar.getTimeInMillis()));
                                newAlarmDateTo.setText("");
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialogFrom.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                datePickerDialogFrom.setCanceledOnTouchOutside(true);
                datePickerDialogFrom.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myCalendar.setTimeInMillis(System.currentTimeMillis());
                        newAlarmDateFrom.setText("");
                        newAlarmDateTo.setText("");
                    }
                });
                datePickerDialogFrom.show();
            }
        });

        newAlarmDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogTo = new DatePickerDialog(
                        getActivityContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, monthOfYear);
                                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                newAlarmDateTo.setText(UtilesTime.millisToDateString(c.getTimeInMillis()));
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialogTo.getDatePicker().setMinDate(myCalendar.getTimeInMillis() + 1000*60*60*24);
                datePickerDialogTo.setCanceledOnTouchOutside(true);
                datePickerDialogTo.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(newAlarmDateTo.getText())) // myCalendar has been set
                            myCalendar.setTimeInMillis(datePickerDialogTo.getDatePicker().getMinDate() - 1000*60*60*24);
                        newAlarmDateTo.setText("");
                    }
                });
                datePickerDialogTo.show();
            }
        });

        newAlarmInfinitePlayers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    newAlarmEmptyFrom.setEnabled(false);
                    newAlarmEmptyFrom.setText(R.string.infinite);
                    newAlarmEmptyTo.setEnabled(false);
                    newAlarmEmptyTo.setText(R.string.infinite);
                } else {
                    newAlarmEmptyFrom.setEnabled(true);
                    newAlarmEmptyFrom.setText("");
                    newAlarmEmptyTo.setEnabled(true);
                    newAlarmEmptyTo.setText("");
                }
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
        newAlarmCity.setAdapter(mAdapter);

        newAlarmCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ((AlarmsActivity)getActivity()).mFieldId = null;
                ((AlarmsActivity)getActivity()).mCity = null;
            }
        });

        newAlarmCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                                        ((AlarmsActivity)getActivity()).mFieldId = null;
                                        ((AlarmsActivity)getActivity()).mCity = myPlace.getName().toString();
                                        ((AlarmsActivity)getActivity()).mCoord = myPlace.getLatLng();
                                        Log.i(TAG, "Place found: Name - " + myPlace.getName()
                                                + " LatLng - " + myPlace.getLatLng());
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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.new_alarm_title), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        newAlarmMap.onStart();
        showContent();

        if (sInitialize) return;
        mNewAlarmPresenter.openAlarm(getLoaderManager(), getArguments());
        sInitialize = true;
    }

    private void setSportLayout(String sportId) {
        //Set sport selected
        showAlarmSport(sportId);

        // Check if the sport doesn't need a field
        if (!Utiles.sportNeedsField(sportId)) {
            showContent();

            newAlarmCity.setVisibility(View.VISIBLE);
            newAlarmInfinitePlayers.setVisibility(View.VISIBLE);

            newAlarmField.setVisibility(View.INVISIBLE);
            newAlarmFieldButton.setVisibility(View.INVISIBLE);

            //Set AutocompleteTextView for cities
            setAutocompleteTextView();
        } else {
            newAlarmInfinitePlayers.setVisibility(View.INVISIBLE);
            newAlarmCity.setVisibility(View.INVISIBLE);

            newAlarmField.setVisibility(View.VISIBLE);
            newAlarmFieldButton.setVisibility(View.VISIBLE);

            // Sport needs a Field so load from ContentProvider and store it in retrieveFields()
            mNewAlarmPresenter.loadFields(getLoaderManager(), getArguments());
        }
    }

    @Override
    public void retrieveFields(ArrayList<Field> fieldList) {
        mFieldList = fieldList;
        showContent();
        if (mFieldList != null)
            if (mFieldList.size() == 0)
                startNewFieldDialog();
            /* else startMapForPickAField();
             * Not necessary since isn't needed a Field to create a new Alarm
             */

        //Since mFieldList are retain in savedInstance no need to load again
        mNewAlarmPresenter.stopLoadFields(getLoaderManager());
    }

    private void startNewFieldDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setTitle(R.string.dialog_title_create_new_field)
                .setMessage(R.string.dialog_msg_create_new_field_for_alarm)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utiles.startFieldsActivityAndNewField(getActivity());
                    }
                })
                .setNegativeButton(android.R.string.no, null); //No need to go back since an alarm can be created without a field
        builder.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        newAlarmMap.onResume();
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onPause() {
        super.onPause();
        newAlarmMap.onPause();
        if (datePickerDialogFrom != null && datePickerDialogFrom.isShowing()) datePickerDialogFrom.dismiss();
        if (datePickerDialogTo != null && datePickerDialogTo.isShowing()) datePickerDialogTo.dismiss();
    }

    @Override
    public void showAlarmSport(String sport) {
        if (sport != null && !TextUtils.isEmpty(sport)) {
            mSportId = sport;
            int sportResource = Utiles.getSportIconFromResource(getArguments().getString(BUNDLE_SPORT_SELECTED_ID));
            Glide.with(this).load(sportResource).into(newAlarmSport);
        }
    }

    @Override
    public void showAlarmField(String fieldId, String city) {
        if (fieldId != null && !TextUtils.isEmpty(fieldId) && getActivity() instanceof AlarmsActivity) {
            Field f = UtilesContentProvider.getFieldFromContentProvider(fieldId);
            if (f != null) {
                newAlarmField.setText(f.getName() + ", " + f.getCity());

                ((AlarmsActivity) getActivity()).mFieldId = fieldId;
                ((AlarmsActivity) getActivity()).mCity = f.getCity();
                ((AlarmsActivity) getActivity()).mCoord = new LatLng(f.getCoord_latitude(), f.getCoord_longitude());
            }
        } else if (city != null && !TextUtils.isEmpty(city) && getActivity() instanceof AlarmsActivity) {
            newAlarmCity.setText(city);
            ((AlarmsActivity) getActivity()).mCity = city;
            ((AlarmsActivity) getActivity()).mFieldId = null;
            ((AlarmsActivity) getActivity()).mCoord = null;
        }

        Utiles.setCoordinatesInMap(getActivityContext(), mMap, ((AlarmsActivity) getActivity()).mCoord);
    }

    @Override
    public void showAlarmDate(Long dateFrom, Long dateTo) {
        if (dateFrom != null && dateFrom > 0)
            newAlarmDateFrom.setText(UtilesTime.millisToDateString(dateFrom));

        if (dateTo != null && dateTo > 0)
            newAlarmDateTo.setText(UtilesTime.millisToDateString(dateTo));
    }

    @Override
    public void showAlarmTotalPlayers(Long totalPlayersFrom, Long totalPlayersTo) {
        if (totalPlayersFrom != null && totalPlayersFrom > -1)
            newAlarmTotalFrom.setText(String.format(Locale.getDefault(), "%d", totalPlayersFrom));

        if (totalPlayersTo != null && totalPlayersTo > -1)
            newAlarmTotalTo.setText(String.format(Locale.getDefault(), "%d", totalPlayersTo));
    }

    @Override
    public void showAlarmEmptyPlayers(Long emptyPlayersFrom, Long emptyPlayersTo) {
        if (emptyPlayersFrom != null && emptyPlayersFrom > -1)
            newAlarmEmptyFrom.setText(String.format(Locale.getDefault(), "%d", emptyPlayersFrom));

        if (emptyPlayersTo != null && emptyPlayersTo > -1)
            newAlarmEmptyTo.setText(String.format(Locale.getDefault(), "%d", emptyPlayersTo));
    }

    @Override
    public void clearUI() {
        mSportId = "";
        ((AlarmsActivity)getActivity()).mFieldId = null;
        ((AlarmsActivity)getActivity()).mCity = null;
        ((AlarmsActivity)getActivity()).mCoord = null;
        newAlarmField.setText("");
        newAlarmCity.setText("");
        newAlarmDateFrom.setText("");
        newAlarmDateTo.setText("");
        newAlarmTotalFrom.setText("");
        newAlarmTotalTo.setText("");
        newAlarmEmptyFrom.setText("");
        newAlarmEmptyTo.setText("");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // If user go back and pick other sport, we need to clear this variables
        ((AlarmsActivity)getActivity()).mFieldId = null;
        ((AlarmsActivity)getActivity()).mCity = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFieldList != null)
            outState.putParcelableArrayList(INSTANCE_FIELD_LIST_ID, mFieldList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        newAlarmMap.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        newAlarmMap.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        newAlarmMap.onLowMemory();
    }
}
