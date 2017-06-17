package com.usal.jorgeav.sportapp.alarms;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.usal.jorgeav.sportapp.ActivityContracts;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.AlarmAdapter;
import com.usal.jorgeav.sportapp.alarms.addalarm.NewAlarmFragment;
import com.usal.jorgeav.sportapp.alarms.alarmdetail.DetailAlarmFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmsFragment extends Fragment implements AlarmsContract.View, AlarmAdapter.OnAlarmitemClickListener {
    private static final String TAG = AlarmsFragment.class.getSimpleName();

    AlarmsContract.Presenter mAlarmsPresenter;
    private ActivityContracts.FragmentManagement mFragmentManagementListener;
    private ActivityContracts.ActionBarIconManagement mActionBarIconManagementListener;

    @BindView(R.id.alarm_create_button)
    Button alarmCreateButton;
    AlarmAdapter mAlarmsRecyclerAdapter;
    @BindView(R.id.alarms_list)
    RecyclerView alarmsRecyclerList;

    public AlarmsFragment() {
        // Required empty public constructor
    }

    public static AlarmsFragment newInstance() {
        return new AlarmsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAlarmsPresenter = new AlarmsPresenter(this);
        mAlarmsRecyclerAdapter = new AlarmAdapter(null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alarms, container, false);
        ButterKnife.bind(this, root);

        alarmsRecyclerList.setAdapter(mAlarmsRecyclerAdapter);
        alarmsRecyclerList.setHasFixedSize(true);
        alarmsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        alarmCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = NewAlarmFragment.newInstance();
                mFragmentManagementListener.initFragment(fragment, true);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.alarms), this);
        mActionBarIconManagementListener.setToolbarAsNav();
        mAlarmsPresenter.loadAlarms(getLoaderManager(), getArguments());
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
    public void showAlarms(Cursor cursor) {
        mAlarmsRecyclerAdapter.replaceData(cursor);
        if (cursor != null) mFragmentManagementListener.showContent();
    }

    @Override
    public Context getActivityContext() {
        return getActivity();
    }

    @Override
    public Fragment getThis() {
        return this;
    }

    @Override
    public void onAlarmClick(String alarmId) {
        Fragment newFragment = DetailAlarmFragment.newInstance(alarmId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
