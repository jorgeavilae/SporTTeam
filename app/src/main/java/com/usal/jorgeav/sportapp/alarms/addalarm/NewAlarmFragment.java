package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.PlaceAutocompleteAdapter;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.mainactivities.AlarmsActivity;
import com.usal.jorgeav.sportapp.mainactivities.FieldsActivity;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class NewAlarmFragment extends BaseFragment implements NewAlarmContract.View  {
    private static final String TAG = NewAlarmFragment.class.getSimpleName();
    public static final String BUNDLE_ALARM_ID = "BUNDLE_ALARM_ID";
    public static final String BUNDLE_SPORT_SELECTED_ID = "BUNDLE_SPORT_SELECTED_ID";

    // Static prevent double initialization with same ID
    private static GoogleApiClient mGoogleApiClient;
    PlaceAutocompleteAdapter mAdapter;
    public static final String INSTANCE_FIELD_LIST_ID = "INSTANCE_FIELD_LIST_ID";
    ArrayList<Field> mFieldList;
    public static final String INSTANCE_ADDRESS_TEXT_VIEW_ID = "INSTANCE_ADDRESS_TEXT_VIEW_ID";

    NewAlarmContract.Presenter mNewAlarmPresenter;
    private static boolean sInitialize;

    ArrayAdapter<CharSequence> sportsAdapter;
    @BindView(R.id.new_alarm_sport)
    Spinner newAlarmSport;
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
            Log.d(TAG, "onOptionsItemSelected: Ok");

            String alarmId = "";
            if (getArguments() != null && getArguments().containsKey(BUNDLE_ALARM_ID))
                alarmId = getArguments().getString(BUNDLE_ALARM_ID);

            mNewAlarmPresenter.addAlarm(
                    alarmId,
                    newAlarmSport.getSelectedItem().toString(),
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

        myCalendar = Calendar.getInstance();

        sportsAdapter = ArrayAdapter.createFromResource(
                getActivityContext(), R.array.sport_id, android.R.layout.simple_spinner_item);
        sportsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newAlarmSport.setAdapter(sportsAdapter);

        newAlarmFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFieldList != null)
                    ((AlarmsActivity) getActivity()).startMapActivityForResult(mFieldList);
            }
        });

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
                                newAlarmDateTo.setEnabled(true);
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialogFrom.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialogFrom.setCanceledOnTouchOutside(true);
                datePickerDialogFrom.setButton(DialogInterface.BUTTON_NEUTRAL, "Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myCalendar.setTimeInMillis(System.currentTimeMillis());
                        newAlarmDateFrom.setText("");
                        newAlarmDateTo.setText("");
                        newAlarmDateTo.setEnabled(false);
                    }
                });
                datePickerDialogFrom.show();
            }
        });

        newAlarmDateTo.setEnabled(false);
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
                datePickerDialogTo.setButton(DialogInterface.BUTTON_NEUTRAL, "Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(newAlarmDateTo.getText())) // myCalendar has been updated
                            myCalendar.setTimeInMillis(datePickerDialogTo.getDatePicker().getMinDate() - 1000*60*60*24);
                        newAlarmDateTo.setText("");
                    }
                });
                datePickerDialogTo.show();
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FIELD_LIST_ID))
            mFieldList = savedInstanceState.getParcelableArrayList(INSTANCE_FIELD_LIST_ID);

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ADDRESS_TEXT_VIEW_ID))
            newAlarmField.setText(savedInstanceState.getString(INSTANCE_ADDRESS_TEXT_VIEW_ID));

        //Show newField dialog on rotation if needed, after retrieveFields are called
        if (mFieldList != null && mFieldList.size() == 0) startNewFieldTask();

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
        mFragmentManagementListener.setCurrentDisplayedFragment("Nueva alarma", this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null && getArguments().containsKey(BUNDLE_SPORT_SELECTED_ID))
            setSportLayout(getArguments().getString(BUNDLE_SPORT_SELECTED_ID));
        else showContent();

        if (sInitialize) return;
        mNewAlarmPresenter.openAlarm(getLoaderManager(), getArguments());
        sInitialize = true;
    }

    private void setSportLayout(String sportId) {
        //Set sport in Spinner
        showAlarmSport(sportId);

        // Check if the sport doesn't need a field
        String[] arraySports = getActivityContext().getResources().getStringArray(R.array.sport_id);
        if (sportId.equals(arraySports[0]) || sportId.equals(arraySports[1])) { // Running & Biking
            showContent();

            //Set AutocompleteTextView for cities
            newAlarmCity.setVisibility(View.VISIBLE);
            newAlarmField.setVisibility(View.INVISIBLE);
            newAlarmFieldButton.setVisibility(View.INVISIBLE);
            setAutocompleteTextView();
        } else {
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
            if (mFieldList.size() == 0) {
                Toast.makeText(getActivityContext(), "There isn't fields for this sport", Toast.LENGTH_SHORT).show();
                startNewFieldTask();
            } /* else startMapForPickAField();
               * Not necessary since isn't needed a Field to create a new Alarm
               */

        //Since mFieldList are retain in savedInstance no need to load again
        mNewAlarmPresenter.stopLoadFields(getLoaderManager());
    }

    private void startNewFieldTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
        builder.setMessage("Quieres crear un campo nuevo?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startFieldsActivityAndNewField();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().onBackPressed();
                    }
                })
                .setCancelable(false);
        builder.create().show();
    }

    @Override
    public void startFieldsActivityAndNewField() {
        /* https://stackoverflow.com/a/24927301/4235666 */
        Intent startActivityIntent = Intent.makeRestartActivityTask(new ComponentName(getActivityContext(), FieldsActivity.class));

        startActivityIntent.putExtra(FieldsActivity.INTENT_EXTRA_CREATE_NEW_FIELD, "dummy");
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startActivityIntent);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (datePickerDialogFrom != null && datePickerDialogFrom.isShowing()) datePickerDialogFrom.dismiss();
        if (datePickerDialogTo != null && datePickerDialogTo.isShowing()) datePickerDialogTo.dismiss();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (!TextUtils.isEmpty(newAlarmDateFrom.getText().toString()))
            newAlarmDateTo.setEnabled(true);
    }

    @Override
    public void showAlarmSport(String sport) {
        if (sport != null && !TextUtils.isEmpty(sport))
            newAlarmSport.setSelection(sportsAdapter.getPosition(sport));
    }

    @Override
    public void showAlarmField(String fieldId, String city) {
        if (fieldId != null && !TextUtils.isEmpty(fieldId) && getActivity() instanceof AlarmsActivity) {
            //Coordinates aren't needed in alarms
            Field f = UtilesContentProvider.getFieldFromContentProvider(fieldId);
            newAlarmField.setText(f.getName());
            ((AlarmsActivity) getActivity()).mFieldId = fieldId;
        }

        if (city != null && !TextUtils.isEmpty(city) && getActivity() instanceof AlarmsActivity) {
            newAlarmCity.setText(city);
            ((AlarmsActivity) getActivity()).mCity = city;
        }
    }

    @Override
    public void showAlarmDate(Long dateFrom, Long dateTo) {
        if (dateFrom != null && dateFrom > 0) {
            newAlarmDateFrom.setText(UtilesTime.millisToDateString(dateFrom));
            newAlarmDateTo.setEnabled(true);
        }
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
        newAlarmSport.setSelection(0);
        ((AlarmsActivity)getActivity()).mFieldId = null;
        ((AlarmsActivity)getActivity()).mCity = null;
        newAlarmField.setText("");
        newAlarmCity.setText("");
        newAlarmDateFrom.setText("");
        newAlarmDateTo.setEnabled(false);
        newAlarmDateTo.setText("");
        newAlarmTotalFrom.setText("");
        newAlarmTotalTo.setText("");
        newAlarmEmptyFrom.setText("");
        newAlarmEmptyTo.setText("");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((AlarmsActivity)getActivity()).mFieldId = null;
        ((AlarmsActivity)getActivity()).mCity = null;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFieldList != null)
            outState.putParcelableArrayList(INSTANCE_FIELD_LIST_ID, mFieldList);
        if (!TextUtils.isEmpty(newAlarmField.getText().toString()))
            outState.putString(INSTANCE_ADDRESS_TEXT_VIEW_ID, newAlarmField.getText().toString());
    }
}
