package com.usal.jorgeav.sportapp.searchevent.advancedsearch;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.SportSpinnerAdapter;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchEventsFragment extends BaseFragment implements SearchEventsContract.View {

    @SuppressWarnings("unused")
    private static final String TAG = SearchEventsFragment.class.getSimpleName();

    SearchEventsContract.Presenter mSearchEventsPresenter;

    public static final String BUNDLE_SPORT = "BUNDLE_SPORT";
    String mSportIdSelected = "";

    @BindView(R.id.search_events_button)
    Button searchEventsButton;
    @BindView(R.id.search_events_icon)
    ImageView searchEventsIcon;
    @BindView(R.id.search_events_sport)
    TextView searchEventsSportName;
    @BindView(R.id.search_events_date_from)
    EditText searchEventsDateFrom;
    @BindView(R.id.search_events_date_to)
    EditText searchEventsDateTo;
    @BindView(R.id.search_events_total_from)
    EditText searchEventsTotalFrom;
    @BindView(R.id.search_events_total_to)
    EditText searchEventsTotalTo;
    @BindView(R.id.search_events_empty_from)
    EditText searchEventsEmptyFrom;
    @BindView(R.id.search_events_empty_to)
    EditText searchEventsEmptyTo;

    Calendar myCalendar;
    DatePickerDialog datePickerDialogFrom;
    DatePickerDialog datePickerDialogTo;

    public SearchEventsFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new SearchEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mSearchEventsPresenter = new SearchEventsPresenter(this);
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
        if (item.getItemId() == R.id.action_ok) {
            actionOkPressed();
            return true;
        }
        return false;
    }

    private void actionOkPressed() {
        Long dateFrom = -1L;
        Long dateTo = -1L;
        if (!TextUtils.isEmpty(searchEventsDateFrom.getText().toString()))
            dateFrom = UtilesTime.stringDateToMillis(searchEventsDateFrom.getText().toString());
        if (!TextUtils.isEmpty(searchEventsDateTo.getText().toString()))
            dateTo = UtilesTime.stringDateToMillis(searchEventsDateTo.getText().toString());

        int totalFrom = -1;
        int totalTo = -1;
        if (!TextUtils.isEmpty(searchEventsTotalFrom.getText().toString()))
            totalFrom = Integer.parseInt(searchEventsTotalFrom.getText().toString());
        if (!TextUtils.isEmpty(searchEventsTotalTo.getText().toString()))
            totalTo = Integer.parseInt(searchEventsTotalTo.getText().toString());

        int emptyFrom = -1;
        int emptyTo = -1;
        if (!TextUtils.isEmpty(searchEventsEmptyFrom.getText().toString()))
            emptyFrom = Integer.parseInt(searchEventsEmptyFrom.getText().toString());
        if (!TextUtils.isEmpty(searchEventsEmptyTo.getText().toString()))
            emptyTo = Integer.parseInt(searchEventsEmptyTo.getText().toString());

        //validate data
        if (mSearchEventsPresenter.validateData(dateFrom, dateTo, totalFrom, totalTo, emptyFrom, emptyTo))
            if (getActivity() instanceof OnSearchEventFilter) {
                ((OnSearchEventFilter) getActivity()).onFilterSet(mSportIdSelected, dateFrom, dateTo, totalFrom, totalTo, emptyFrom, emptyTo);
            } else {
                Log.e(TAG, "onOptionsItemSelected: Activity does not implement OnSearchEventFilter");
            }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search_events, container, false);
        ButterKnife.bind(this, root);

        searchEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPickSportDialog();
            }
        });

        myCalendar = Calendar.getInstance();
        searchEventsDateFrom.setOnClickListener(new View.OnClickListener() {
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
                                searchEventsDateFrom.setText(UtilesTime.millisToDateString(myCalendar.getTimeInMillis()));
                                searchEventsDateTo.setText("");
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
                        searchEventsDateFrom.setText("");
                        searchEventsDateTo.setText("");
                    }
                });
                datePickerDialogFrom.show();
            }
        });

        searchEventsDateTo.setOnClickListener(new View.OnClickListener() {
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
                                searchEventsDateTo.setText(UtilesTime.millisToDateString(c.getTimeInMillis()));
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
                        if (!TextUtils.isEmpty(searchEventsDateTo.getText())) // myCalendar has been set
                            myCalendar.setTimeInMillis(datePickerDialogTo.getDatePicker().getMinDate() - 1000*60*60*24);
                        searchEventsDateTo.setText("");
                    }
                });
                datePickerDialogTo.show();
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SPORT))
            setSportSearched(savedInstanceState.getString(BUNDLE_SPORT));

        return root;
    }

    private void createPickSportDialog() {
        ArrayList<String> sportsResources = new ArrayList<>(
                Arrays.asList(getResources().getStringArray(R.array.sport_id_values)));
        final SportSpinnerAdapter listAdapter = new SportSpinnerAdapter(getActivityContext(),
                R.layout.sport_spinner_item, sportsResources);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_sport)
                .setAdapter(listAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sportId = (String) listAdapter.getItem(i);
                        setSportSearched(sportId);
                    }
                })
                .setCancelable(true)
                .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unsetSportSearched();
                    }
                });
        builder.create().show();
    }

    private void setSportSearched(String sportId) {
        mSportIdSelected = sportId;

        int sportStringResource = getResources()
                .getIdentifier(sportId, "string", getActivityContext().getPackageName());
        searchEventsSportName.setText(getString(sportStringResource));

        int sportDrawableResource = Utiles.getSportIconFromResource(sportId);
        searchEventsIcon.setVisibility(View.VISIBLE);
        Glide.with(this).load(sportDrawableResource).into(searchEventsIcon);
    }

    private void unsetSportSearched() {
        mSportIdSelected = "";

        searchEventsSportName.setText(null);
        searchEventsIcon.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.search_events), this);
        mActionBarIconManagementListener.setToolbarAsUp();
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mSportIdSelected))
            outState.putString(BUNDLE_SPORT, mSportIdSelected);
    }

    public interface OnSearchEventFilter {
        void onFilterSet(String sportId, Long dateFrom, Long dateTo, int totalFrom, int totalTo,
                         int emptyFrom, int emptyTo);
    }
}