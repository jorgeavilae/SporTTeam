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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

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
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.mainactivities.FieldsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewFieldFragment extends BaseFragment implements NewFieldContract.View {
    public static final String TAG = NewFieldFragment.class.getSimpleName();

    public static final String BUNDLE_FIELD_ID = "BUNDLE_FIELD_ID";

    NewFieldContract.Presenter mNewFieldPresenter;
    private static boolean sInitialize;

    // Static prevent double initialization with same ID
    private static GoogleApiClient mGoogleApiClient;
    private String mCreator = "";
    private ArrayList<Field> mFieldList;

    @BindView(R.id.new_field_map)
    MapView newFieldMap;
    private GoogleMap mMap;
    @BindView(R.id.new_field_address)
    TextView newFieldAddress;
    @BindView(R.id.new_field_map_button)
    Button newFieldMapButton;
    @BindView(R.id.new_field_name)
    EditText newFieldName;
    @BindView(R.id.new_field_open_time)
    EditText newFieldOpenTime;
    @BindView(R.id.new_field_close_time)
    EditText newFieldCloseTime;
    @BindView(R.id.new_field_all_day_time)
    CheckBox newFieldAllDayTime;
    List<SportCourt> mSports;

    Calendar myCalendar;
    TimePickerDialog openTimePickerDialog;
    TimePickerDialog.OnTimeSetListener openTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hour);
            myCalendar.set(Calendar.MINUTE, minute);
            newFieldOpenTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        }
    };
    TimePickerDialog closeTimePickerDialog;
    TimePickerDialog.OnTimeSetListener closeTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hour);
            myCalendar.set(Calendar.MINUTE, minute);
            newFieldCloseTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        }
    };

    public NewFieldFragment() {
        // Required empty public constructor
    }

    public static NewFieldFragment newInstance(@Nullable String fieldId) {
        NewFieldFragment nff = new NewFieldFragment();
        Bundle b = new Bundle();
        if (fieldId != null)
            b.putString(BUNDLE_FIELD_ID, fieldId);
        nff.setArguments(b);
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
            String fieldId = "";
            if (getArguments() != null && getArguments().containsKey(BUNDLE_FIELD_ID))
                fieldId = getArguments().getString(BUNDLE_FIELD_ID);

            if (mCreator == null || TextUtils.isEmpty(mCreator))
                mCreator = Utiles.getCurrentUserId();
            if (newFieldAllDayTime.isChecked()) {
                newFieldOpenTime.setText(UtilesTime.millisToTimeString(0));
                newFieldCloseTime.setText(UtilesTime.millisToTimeString(0));
            }
            mNewFieldPresenter.addField(
                    fieldId,
                    newFieldName.getText().toString(),
                    ((FieldsActivity) getActivity()).mAddress,
                    ((FieldsActivity) getActivity()).mCoord,
                    ((FieldsActivity) getActivity()).mCity,
                    newFieldOpenTime.getText().toString(),
                    newFieldCloseTime.getText().toString(),
                    mCreator, mSports);
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_field, container, false);
        ButterKnife.bind(this, root);

        //Need to be MapView, not SupportMapFragment https://stackoverflow.com/a/19354359/4235666
        newFieldMap.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        newFieldMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Coordinates selected previously
                Utiles.setCoordinatesInMap(getActivityContext(), mMap, ((FieldsActivity) getActivity()).mCoord);
            }
        });

        newFieldMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFieldList != null) // Wait till all Fields from city are loaded
                    ((FieldsActivity) getActivity()).startMapActivityForResult(mFieldList, false);
            }
        });

        myCalendar = Calendar.getInstance();

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

        newFieldAllDayTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    newFieldOpenTime.setEnabled(false);
                    newFieldOpenTime.setText(" ");
                    newFieldCloseTime.setEnabled(false);
                    newFieldCloseTime.setText(" ");
                } else {
                    newFieldOpenTime.setEnabled(true);
                    newFieldOpenTime.setText("");
                    newFieldCloseTime.setEnabled(true);
                    newFieldCloseTime.setText("");
                }
            }
        });

        // Show map if with place selected previously
        showFieldPlace(((FieldsActivity)getActivity()).mAddress,
                ((FieldsActivity)getActivity()).mCity,
                ((FieldsActivity)getActivity()).mCoord);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.new_field_title), this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        newFieldMap.onStart();
        //Do always to have Fields to populate Map
        mNewFieldPresenter.loadNearbyFields(getLoaderManager(), getArguments());

        //Do when edit a Field
        if (getArguments() != null && getArguments().containsKey(BUNDLE_FIELD_ID)) {
            if (sInitialize) {
                //To prevent double initialization on edits
                mNewFieldPresenter.destroyOpenFieldLoader(getLoaderManager());
                return;
            }
            mNewFieldPresenter.openField(getLoaderManager(), getArguments());
            sInitialize = true;
        } else { //Do when create a new Field
            setSportCourts(((FieldsActivity) getActivity()).mSports);
        }
    }

    @Override
    public void retrieveFields(ArrayList<Field> fieldList) {
        mFieldList = fieldList;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentManagementListener.showContent();
        newFieldMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        newFieldMap.onPause();
        if (openTimePickerDialog != null && openTimePickerDialog.isShowing())
            openTimePickerDialog.dismiss();
        if (closeTimePickerDialog != null && closeTimePickerDialog.isShowing())
            closeTimePickerDialog.dismiss();
    }

    @Override
    public void showFieldName(String name) {
        if (name != null && !TextUtils.isEmpty(name))
            newFieldName.setText(name);
    }

    @Override
    public void showFieldPlace(String address, String city, LatLng coords) {
        if (address != null && !TextUtils.isEmpty(address)
                && city != null && !TextUtils.isEmpty(city)
                && coords != null) {
            newFieldAddress.setText(address);

            Utiles.setCoordinatesInMap(getActivityContext(), mMap, coords);
        }
    }

    @Override
    public void showFieldTimes(long openTime, long closeTimes) {
        if (openTime <= closeTimes) {
            newFieldOpenTime.setText(UtilesTime.millisToTimeString(openTime));
            newFieldCloseTime.setText(UtilesTime.millisToTimeString(closeTimes));
        }
    }

    @Override
    public void showFieldCreator(String creator) {
        if (creator != null && !TextUtils.isEmpty(creator))
            mCreator = creator;
    }

    @Override
    public void setSportCourts(List<SportCourt> sports) {
        mSports = sports;
    }

    @Override
    public void clearUI() {
        newFieldName.setText("");
        newFieldAddress.setText("");
        newFieldOpenTime.setText("");
        newFieldCloseTime.setText("");
        mSports = null;
        mCreator = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        newFieldMap.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        newFieldMap.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        newFieldMap.onLowMemory();
    }
}
