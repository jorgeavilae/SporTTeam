package com.usal.jorgeav.sportapp.adduser.sportpractice;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usal.jorgeav.sportapp.ActivityContracts;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.AddSportsAdapter;
import com.usal.jorgeav.sportapp.data.Sport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SportsListFragment extends Fragment {
    private final static String TAG = SportsListFragment.class.getSimpleName();
    public static final String BUNDLE_INSTANCE_LISTENER = "BUNDLE_INSTANCE_LISTENER";
    public static final String BUNDLE_INSTANCE_SPORT_LIST = "BUNDLE_INSTANCE_SPORT_LIST";

    private ActivityContracts.ActionBarIconManagement mActionBarIconManagementListener;
    private ActivityContracts.FragmentManagement mFragmentManagementListener;
    private OnSportsSelected mOnSportsSelectedListener;
    private AddSportsAdapter mSportAdapter;

    @BindView(R.id.recycler_list)
    RecyclerView sportsRecyclerViewList;

    public SportsListFragment() {
        // Required empty public constructor
    }

    public void setOnSportSelectedLister(OnSportsSelected listener) {
        this.mOnSportsSelectedListener = listener;
    }

    public static SportsListFragment newInstance(ArrayList<Sport> sportsList, OnSportsSelected listener) {
        SportsListFragment fragment = new SportsListFragment();
        fragment.setOnSportSelectedLister(listener);
        if (sportsList != null) {
            Bundle args = new Bundle();
            args.putParcelableArrayList(BUNDLE_INSTANCE_SPORT_LIST, sportsList);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, root);

        mSportAdapter = new AddSportsAdapter(loadSports());
        sportsRecyclerViewList.setAdapter(mSportAdapter);
        sportsRecyclerViewList.setHasFixedSize(true);
        sportsRecyclerViewList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mFragmentManagementListener.showContent();

        return root;
    }

    private List<Sport> loadSports() {
        ArrayList<Sport> result = new ArrayList<>();

        String[] sportsNameArray = getResources().getStringArray(R.array.sport_id);
        for (String aSportsNameArray : sportsNameArray) {
            result.add(new Sport(aSportsNameArray, 0f, 0));
        }

        if (getArguments() != null && getArguments().containsKey(BUNDLE_INSTANCE_SPORT_LIST)) {
            ArrayList<Sport> sportsListFromActivity = getArguments().getParcelableArrayList(BUNDLE_INSTANCE_SPORT_LIST);
            if (sportsListFromActivity != null) {
                for (Sport sportFromActivity : sportsListFromActivity)
                    for (Sport sportFromResources : result)
                        if (isTheSameSport(sportFromActivity, sportFromResources)) {
                            sportFromResources.setmLevel(sportFromActivity.getmLevel());
                            break;
                        }
            }
        }

        return result;
    }

    private boolean isTheSameSport(Sport a, Sport b) {
        return a.getmName().equals(b.getmName());
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
        mOnSportsSelectedListener.retrieveSportsSelected(mSportAdapter.getDataAsArrayList());
    }
    
    public interface OnSportsSelected {
        void retrieveSportsSelected(List<Sport> sportsSelected);
    }
}
