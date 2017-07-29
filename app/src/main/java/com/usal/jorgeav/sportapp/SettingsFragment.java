package com.usal.jorgeav.sportapp;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

public class SettingsFragment extends PreferenceFragment {

    private Context mActivityContext;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    /* getActivity only returns != null inside attach/detach lifecycle */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityContext = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivityContext = null;
    }
}
