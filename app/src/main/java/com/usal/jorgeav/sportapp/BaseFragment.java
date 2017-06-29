package com.usal.jorgeav.sportapp;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.usal.jorgeav.sportapp.mainactivities.ActivityContracts;

/**
 * Created by Jorge Avila on 29/06/2017.
 */

public abstract class BaseFragment extends Fragment {

    protected ActivityContracts.FragmentManagement mFragmentManagementListener;
    protected ActivityContracts.ActionBarIconManagement mActionBarIconManagementListener;

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

    public Context getActivityContext() {
        return getActivity();
    }
}
