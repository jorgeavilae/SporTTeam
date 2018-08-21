package com.usal.jorgeav.sportapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.usal.jorgeav.sportapp.mainactivities.ActivityContracts;

public abstract class BaseFragment extends Fragment {
    @SuppressWarnings("unused")
    private static final String TAG = BaseFragment.class.getSimpleName();

    protected ActivityContracts.FragmentManagement mFragmentManagementListener;
    protected ActivityContracts.NavigationDrawerManagement mNavigationDrawerManagementListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActivityContracts.FragmentManagement)
            mFragmentManagementListener = (ActivityContracts.FragmentManagement) context;
        if (context instanceof ActivityContracts.NavigationDrawerManagement)
            mNavigationDrawerManagementListener = (ActivityContracts.NavigationDrawerManagement) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideSoftKeyboard();
        mFragmentManagementListener = null;
        mNavigationDrawerManagementListener = null;
    }

    public Context getActivityContext() {
        return getActivity();
    }

    public BaseFragment getThis() { return this;}

    public void showContent() {
        if (mFragmentManagementListener != null)
            mFragmentManagementListener.showContent();
    }

    public void hideContent() {
        if (mFragmentManagementListener != null)
            mFragmentManagementListener.hideContent();
    }

    public void resetBackStack() {
        getActivity().getSupportFragmentManager().popBackStack(
                getActivity().getSupportFragmentManager().getBackStackEntryAt(0).getId(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /* https://stackoverflow.com/a/1109108/4235666 */
    public void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
