package com.usal.jorgeav.sportapp.adduser.sportpractice;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usal.jorgeav.sportapp.ActivityContracts;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Sport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jorge Avila on 25/05/2017.
 */

public class SportsListFragment extends Fragment {
    public static final String BUNDLE_INSTANCE_LISTENER = "BUNDLE_INSTANCE_LISTENER";

    private ActivityContracts.ActionBarIconManagement mActionBarIconManagementListener;
    private ActivityContracts.FragmentManagement mFragmentManagementListener;
    private OnSportsSelected mOnSportsSelectedListener;

    @BindView(R.id.recycler_list)
    RecyclerView sportsList;

    public SportsListFragment() {
        // Required empty public constructor
    }

    public void setOnSportSelectedLister(OnSportsSelected listener) {
        this.mOnSportsSelectedListener = listener;
    }

    public static SportsListFragment newInstance(OnSportsSelected listener) {
        SportsListFragment fragment = new SportsListFragment();
        fragment.setOnSportSelectedLister(listener);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, root);
        //TODO load my sports with puntutation and others();
        mFragmentManagementListener.showContent();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(SportsListFragment.class.getSimpleName(), this);
        if (mActionBarIconManagementListener != null) mActionBarIconManagementListener.setToolbarAsUp();
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
        mOnSportsSelectedListener.retrieveSportsSelected(null);
    }
    
    public interface OnSportsSelected {
        void retrieveSportsSelected(List<Sport> sportsSelected);
    }
}
