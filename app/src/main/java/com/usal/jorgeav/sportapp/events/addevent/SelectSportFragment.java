package com.usal.jorgeav.sportapp.events.addevent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.SelectSportsAdapter;
import com.usal.jorgeav.sportapp.data.Sport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 20/07/2017.
 */

public class SelectSportFragment extends BaseFragment implements SelectSportsAdapter.OnSelectSportClickListener {
    private final static String TAG = SelectSportFragment.class.getSimpleName();

    @BindView(R.id.recycler_list)
    RecyclerView sportsRecyclerViewList;
    private SelectSportsAdapter mSportAdapter;
    int mSportSelectedPosition;

    public SelectSportFragment() {
        // Required empty public constructor
    }

    public static SelectSportFragment newInstance() {
        SelectSportFragment fragment = new SelectSportFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, root);

        mSportAdapter = new SelectSportsAdapter(null, this);
        sportsRecyclerViewList.setAdapter(mSportAdapter);
        sportsRecyclerViewList.setHasFixedSize(true);
        sportsRecyclerViewList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    private List<Sport> loadSports() {
        ArrayList<Sport> result = new ArrayList<>();

        String[] sportsNameArray = getResources().getStringArray(R.array.sport_id);
        for (String aSportsNameArray : sportsNameArray) {
            result.add(new Sport(aSportsNameArray, 0f, 0));
        }

        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
        hideSoftKeyboard();
        mFragmentManagementListener.setCurrentDisplayedFragment("Selecciona deporte", this);
        if (mActionBarIconManagementListener != null) mActionBarIconManagementListener.setToolbarAsUp();
        mSportAdapter.replaceData(loadSports());
        showContent();
    }

    @Override
    public void onPause() {
        super.onPause();
        mFragmentManagementListener.setCurrentDisplayedFragment(null, null);
        mSportAdapter.replaceData(null);
    }

    @Override
    public void onSportClick(Sport sport) {
        Fragment fragment = NewEventFragment.newInstance(null, sport.getmName());
        mFragmentManagementListener.initFragment(fragment, true);
    }
}
