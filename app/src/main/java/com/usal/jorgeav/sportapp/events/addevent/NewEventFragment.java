package com.usal.jorgeav.sportapp.events.addevent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.usal.jorgeav.sportapp.ActivityContracts;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.events.addevent.selectfield.SelectFieldFragment;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class NewEventFragment extends Fragment implements NewEventContract.View, SelectFieldFragment.OnFieldSelected  {
    public static final String TAG = NewEventFragment.class.getSimpleName();

    NewEventContract.Presenter mNewEventPresenter;
    private ActivityContracts.FragmentManagement mFragmentManagementListener;
    private ActivityContracts.ActionBarIconManagement mActionBarIconManagementListener;

    @BindView(R.id.new_event_sport)
    Spinner newEventSport;
    @BindView(R.id.new_event_field)
    Button newEventFieldButton;
    @BindView(R.id.new_event_name)
    EditText newEventName;
    @BindView(R.id.new_event_city)
    EditText newEventCity;
    @BindView(R.id.new_event_date)
    EditText newEventDate;
    @BindView(R.id.new_event_time)
    EditText newEventTime;
    @BindView(R.id.new_event_total)
    EditText newEventTotal;
    @BindView(R.id.new_event_empty)
    EditText newEventEmpty;
    @BindView(R.id.new_event_add_event)
    Button newEventAddButton;

    String fieldSelectedId;
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

    public static NewEventFragment newInstance() {
        return new NewEventFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNewEventPresenter = new NewEventPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_event, container, false);
        ButterKnife.bind(this, root);

        myCalendar = Calendar.getInstance();

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivityContext(), R.array.sport_id, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newEventSport.setAdapter(adapter);

        final SelectFieldFragment.OnFieldSelected listener = this;
        newEventFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = SelectFieldFragment.newInstance(getSportSelected(), listener);
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

        newEventAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewEventPresenter.addEvent(
                        newEventSport.getSelectedItem().toString(),
                        fieldSelectedId,
                        newEventName.getText().toString(),
                        newEventCity.getText().toString(),
                        newEventDate.getText().toString(),
                        newEventTime.getText().toString(),
                        newEventTotal.getText().toString(),
                        newEventEmpty.getText().toString());
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("New Event", this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActivityContracts.FragmentManagement)
            mFragmentManagementListener = (ActivityContracts.FragmentManagement) context;
        if (context instanceof ActivityContracts.ActionBarIconManagement)
            mActionBarIconManagementListener = (ActivityContracts.ActionBarIconManagement) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentManagementListener = null;
        mActionBarIconManagementListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (datePickerDialog != null && datePickerDialog.isShowing()) datePickerDialog.dismiss();
        if (timePickerDialog != null && timePickerDialog.isShowing()) timePickerDialog.dismiss();
    }

    @Override
    public Context getActivityContext() {
        return getActivity();
    }

    @Override
    public Fragment getThis() {
        return this;
    }

    private String getSportSelected() {
        return newEventSport.getSelectedItem().toString();
    }

    @Override
    public void retrieveFieldSelected(String fieldId) {
        fieldSelectedId = fieldId;
    }
}
