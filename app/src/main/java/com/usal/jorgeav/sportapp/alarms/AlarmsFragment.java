package com.usal.jorgeav.sportapp.alarms;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.AlarmAdapter;
import com.usal.jorgeav.sportapp.alarms.alarmdetail.DetailAlarmFragment;
import com.usal.jorgeav.sportapp.events.addevent.SelectSportFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmsFragment extends BaseFragment implements AlarmsContract.View, AlarmAdapter.OnAlarmItemClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = AlarmsFragment.class.getSimpleName();

    AlarmsContract.Presenter mAlarmsPresenter;

    AlarmAdapter mAlarmsRecyclerAdapter;
    @BindView(R.id.alarms_list)
    RecyclerView alarmsRecyclerList;
    @BindView(R.id.alarms_placeholder)
    ConstraintLayout alarmsPlaceholder;

    public AlarmsFragment() {
        // Required empty public constructor
    }

    public static AlarmsFragment newInstance() {
        return new AlarmsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mAlarmsPresenter = new AlarmsPresenter(this);
        mAlarmsRecyclerAdapter = new AlarmAdapter(null, this, Glide.with(this));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_add) {
            Fragment fragment = SelectSportFragment.newInstance();
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alarms, container, false);
        ButterKnife.bind(this, root);

        alarmsRecyclerList.setAdapter(mAlarmsRecyclerAdapter);
        alarmsRecyclerList.setHasFixedSize(true);
        alarmsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.alarms), this);
        mNavigationDrawerManagementListener.setToolbarAsNav();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAlarmsPresenter.loadAlarms(getLoaderManager(), getArguments());
    }

    @Override
    public void onPause() {
        super.onPause();
        mAlarmsRecyclerAdapter.replaceData(null);
    }

    @Override
    public void showAlarms(Cursor cursor) {
        mAlarmsRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            alarmsRecyclerList.setVisibility(View.VISIBLE);
            alarmsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            alarmsRecyclerList.setVisibility(View.INVISIBLE);
            alarmsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    @Override
    public void onAlarmClick(String alarmId) {
        Fragment newFragment = DetailAlarmFragment.newInstance(alarmId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
