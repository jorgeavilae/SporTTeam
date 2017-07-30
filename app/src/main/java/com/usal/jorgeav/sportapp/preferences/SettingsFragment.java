package com.usal.jorgeav.sportapp.preferences;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    private final static String TAG = SettingsFragment.class.getSimpleName();

    public static final String KEY_PREF_SYNC_CONN = "pref_syncConnectionType";
    public static final String KEY_PREF_CITY = "pref_city";

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

    @Override
    public void onStop() {
        super.onStop();
        // unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // register the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_CITY)) {
            String myUid = Utiles.getCurrentUserId();
            if (TextUtils.isEmpty(myUid)) return;

            CityAutocompleteEditTextPreference cityPref = (CityAutocompleteEditTextPreference)findPreference(key);
            // Set summary to be the user-description for the selected value
            cityPref.setSummary(sharedPreferences.getString(key, ""));

            //Update User and reload data
            FirebaseActions.updateUserCityAndReload(myUid, cityPref.citySelectedName, cityPref.citySelectedCoord);
        }
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
