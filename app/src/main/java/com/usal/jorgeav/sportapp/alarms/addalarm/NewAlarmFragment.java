package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.events.addevent.selectfield.SelectFieldFragment;
import com.usal.jorgeav.sportapp.mainactivities.AlarmsActivity;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

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

    NewAlarmContract.Presenter mNewAlarmPresenter;
    private static boolean sInitialize;

    ArrayAdapter<CharSequence> sportsAdapter;
    @BindView(R.id.new_alarm_sport)
    Spinner newAlarmSport;
    @BindView(R.id.new_alarm_field)
    Button newAlarmFieldButton;
    @BindView(R.id.new_alarm_city)
    EditText newAlarmCity;
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

    public static NewAlarmFragment newInstance(@Nullable String alarmId) {
        NewAlarmFragment naf = new NewAlarmFragment();
        if (alarmId != null) {
            Bundle b = new Bundle();
            b.putString(BUNDLE_ALARM_ID, alarmId);
            naf.setArguments(b);
        }
        sInitialize = false;
        return naf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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
                    ((AlarmsActivity)getActivity()).newAlarmFieldSelected,
                    newAlarmCity.getText().toString(),
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
                Fragment fragment = SelectFieldFragment.newInstance(getSportSelected());
                mFragmentManagementListener.initFragment(fragment, true);
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
                                newAlarmDateFrom.setText(UtilesTime.calendarToDate(myCalendar.getTime()));
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
                                newAlarmDateTo.setText(UtilesTime.calendarToDate(c.getTime()));
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

        return root;
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
        if (sInitialize) return;
        mNewAlarmPresenter.openAlarm(getLoaderManager(), getArguments());
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
        if (datePickerDialogFrom != null && datePickerDialogFrom.isShowing()) datePickerDialogFrom.dismiss();
        if (datePickerDialogTo != null && datePickerDialogTo.isShowing()) datePickerDialogTo.dismiss();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (!TextUtils.isEmpty(newAlarmDateFrom.getText().toString()))
            newAlarmDateTo.setEnabled(true);
    }

    private String getSportSelected() {
        return newAlarmSport.getSelectedItem().toString();
    }

    @Override
    public void showAlarmSport(String sport) {
        if (sport != null && !TextUtils.isEmpty(sport))
            newAlarmSport.setSelection(sportsAdapter.getPosition(sport));
    }

    @Override
    public void showAlarmField(String fieldId) {
        if (fieldId != null && !TextUtils.isEmpty(fieldId) && getActivity() instanceof SelectFieldFragment.OnFieldSelected)
            //Coordinates aren't needed in alarms
            ((SelectFieldFragment.OnFieldSelected)getActivity()).retrieveFieldSelected(fieldId, null);
    }

    @Override
    public void showAlarmCity(String city) {
        if (city != null && !TextUtils.isEmpty(city))
            newAlarmCity.setText(city);
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
        ((SelectFieldFragment.OnFieldSelected)getActivity()).retrieveFieldSelected("", null);
        newAlarmCity.setText("");
        newAlarmDateFrom.setText("");
        newAlarmDateTo.setEnabled(false);
        newAlarmDateTo.setText("");
        newAlarmTotalFrom.setText("");
        newAlarmTotalTo.setText("");
        newAlarmEmptyFrom.setText("");
        newAlarmEmptyTo.setText("");
    }
}
