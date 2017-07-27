package com.usal.jorgeav.sportapp.fields;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.SupportMapFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.mainactivities.ActivityContracts;
import com.usal.jorgeav.sportapp.mainactivities.FieldsActivity;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 27/07/2017.
 */

public class FieldsMapFragment extends SupportMapFragment implements FieldsContract.View {
    private static final String TAG = FieldsMapFragment.class.getSimpleName();

    protected ActivityContracts.FragmentManagement mFragmentManagementListener;
    protected ActivityContracts.ActionBarIconManagement mActionBarIconManagementListener;

    FieldsContract.Presenter mFieldsPresenter;
    ArrayList<Field> mFieldsList;
    private static boolean sInitialize;

    public FieldsMapFragment() {
        // Required empty public constructor
    }
    public static FieldsMapFragment newInstance(boolean createNewField) {
        Bundle b = new Bundle();
        //If is necessary to init NewField programmatically
        if (createNewField)
            b.putString(FieldsActivity.INTENT_EXTRA_CREATE_NEW_FIELD, "");
        FieldsMapFragment fragment = new FieldsMapFragment();
        fragment.setArguments(b);
        sInitialize = false;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//                if (mFieldsList != null)
//                    ((FieldsActivity)getActivity()).startMapActivityForResult(mFieldsList, true);

        mFieldsPresenter = new FieldsPresenter(this);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.fields), null);
        mActionBarIconManagementListener.setToolbarAsNav();
    }

    @Override
    public void onStart() {
        super.onStart();

        mFieldsPresenter.loadNearbyFields(getLoaderManager(), getArguments());
    }

    @Override
    public void showFields(Cursor cursor) {
        mFieldsList = UtilesContentProvider.cursorToMultipleField(cursor);
        Log.d(TAG, "showFields: "+mFieldsList);

        //If is necessary to init NewField programmatically
        if (!sInitialize && getArguments() != null && getArguments().containsKey(FieldsActivity.INTENT_EXTRA_CREATE_NEW_FIELD)) {
            //TODO Start new field workflow
            sInitialize = true;
            return;
        }

        //TODO Update Fields in map

        mFragmentManagementListener.showContent();
    }

    @Override
    public Context getActivityContext() {
        return getActivity();
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
        hideSoftKeyboard();
        mFragmentManagementListener = null;
        mActionBarIconManagementListener = null;
    }

    public void showContent() {
        Log.d(TAG, "showContent: ");
        if (mFragmentManagementListener != null)
            mFragmentManagementListener.showContent();
    }

    public void hideContent() {
        Log.d(TAG, "hideContent: ");
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
