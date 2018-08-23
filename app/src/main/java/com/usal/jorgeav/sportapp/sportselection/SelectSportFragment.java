package com.usal.jorgeav.sportapp.sportselection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.SelectSportsAdapter;
import com.usal.jorgeav.sportapp.data.Sport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectSportFragment extends BaseFragment implements SelectSportsAdapter.OnSelectSportClickListener {
    private final static String TAG = SelectSportFragment.class.getSimpleName();

    @BindView(R.id.recycler_list)
    RecyclerView sportsRecyclerViewList;
    private SelectSportsAdapter mSportAdapter;

    public SelectSportFragment() {
        // Required empty public constructor
    }

    public static SelectSportFragment newInstance() {
        return new SelectSportFragment();
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

        if (!(getActivity() instanceof OnSportSelectedListener)) {
            Log.e(TAG, "onCreateView: getActivity() should implement OnSportSelectedListener");
            getActivity().onBackPressed();
        }
        mSportAdapter = new SelectSportsAdapter(null,
                this,
                Glide.with(this));
        sportsRecyclerViewList.setAdapter(mSportAdapter);
        sportsRecyclerViewList.setHasFixedSize(true);
        sportsRecyclerViewList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    private List<Sport> loadSports() {
        ArrayList<Sport> result = new ArrayList<>();

        String[] sportsNameArray = getResources().getStringArray(R.array.sport_id_values);
        for (String aSportsNameArray : sportsNameArray) {
            result.add(new Sport(aSportsNameArray, (double) 0f));
        }

        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
        hideSoftKeyboard();
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.pick_sport), this);
        if (mNavigationDrawerManagementListener != null) mNavigationDrawerManagementListener.setToolbarAsUp();
        mSportAdapter.replaceData(loadSports());
        showContent();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSportAdapter.replaceData(null);
    }

    @Override
    public void onSportClick(String sportId) {
        if (getActivity() instanceof OnSportSelectedListener)
            ((OnSportSelectedListener) getActivity()).onSportSelected(sportId);
    }

    public interface OnSportSelectedListener {
        void onSportSelected(String sportId);
    }
}
