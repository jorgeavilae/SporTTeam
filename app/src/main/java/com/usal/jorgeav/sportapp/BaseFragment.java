package com.usal.jorgeav.sportapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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

    public BaseFragment getThis() { return this;}

    public void resetBackStack() {
        getActivity().getSupportFragmentManager().popBackStack(
                getActivity().getSupportFragmentManager().getBackStackEntryAt(0).getId(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    //Todo invoke when necesary
    /* https://stackoverflow.com/a/1109108/4235666 */
    public void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
