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
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.mainactivities.FieldsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private String mCreator = "";

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
    List<SportCourt> mSports;

    Calendar myCalendar;
    TimePickerDialog openTimePickerDialog;
    TimePickerDialog.OnTimeSetListener openTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hour);
            myCalendar.set(Calendar.MINUTE, minute);
            long time = TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute);
            newFieldOpenTime.setText(UtilesTime.millisToTimeString(time));
        }
    };
    TimePickerDialog closeTimePickerDialog;
    TimePickerDialog.OnTimeSetListener closeTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hour);
            myCalendar.set(Calendar.MINUTE, minute);
            long time = TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute);
            newFieldCloseTime.setText(UtilesTime.millisToTimeString(time));
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
            Log.d(TAG, "onOptionsItemSelected: Ok");

            String fieldId = "";
            if (getArguments() != null && getArguments().containsKey(BUNDLE_FIELD_ID))
                fieldId = getArguments().getString(BUNDLE_FIELD_ID);

            if (mCreator == null || TextUtils.isEmpty(mCreator)) mCreator = Utiles.getCurrentUserId();
            mNewFieldPresenter.addField(
                    fieldId,
                    newFieldName.getText().toString(),
                    ((FieldsActivity)getActivity()).mAddress,
                    ((FieldsActivity)getActivity()).mCoord,
                    ((FieldsActivity)getActivity()).mCity,
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

        showFieldPlace(((FieldsActivity)getActivity()).mAddress,
                ((FieldsActivity)getActivity()).mCity,
                ((FieldsActivity)getActivity()).mCoord);

        return root;
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
        mNewFieldPresenter.loadNearbyFields(getLoaderManager(), getArguments());
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

    @Override
    public void retrieveFields(ArrayList<Field> dataList) {

    }

    @Override
    public void showFieldName(String name) {
        if (name != null && !TextUtils.isEmpty(name))
            newFieldName.setText(name);
    }

    @Override
    public void showFieldPlace(String address, String city, LatLng coords) {
        newFieldAddress.setText(address);
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
    public void onDetach() {
        super.onDetach();
        ((FieldsActivity)getActivity()).mFieldId = null;
        ((FieldsActivity)getActivity()).mAddress = null;
        ((FieldsActivity)getActivity()).mCity = null;
        ((FieldsActivity)getActivity()).mCoord = null;
    }
}
