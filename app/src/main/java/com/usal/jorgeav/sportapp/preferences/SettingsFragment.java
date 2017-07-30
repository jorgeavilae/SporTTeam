package com.usal.jorgeav.sportapp.preferences;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    private final static String TAG = SettingsFragment.class.getSimpleName();

    public static final String KEY_PREF_SYNC_CONN = "pref_syncConnectionType";
    public static final String KEY_PREF_CITY = "pref_city";
    public static final String KEY_PREF_EMAIL = "pref_email";

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
        } else if (key.equals(KEY_PREF_EMAIL)) {
            String email = sharedPreferences.getString(key, "");
            if (!TextUtils.isEmpty(email) && isEmailValid(email)) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    //Update email in FirebaseAuth
                    user.updateEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User email address updated.");
                                        //TODO
                                        //Update email in FirebaseDatabase
                                        FirebaseActions.updateUserEmailAndReload(user.getUid(), email);

                                        //Update email in ContentProvider Email_logged
                                        getActivity().getContentResolver().update(SportteamContract.EmailLoggedEntry.CONTENT_EMAIL_LOGGED_URI, cv, )

                                        //Update share
                                        Preference emailPref = findPreference(key);
                                        // Set summary to be the user-description for the selected value
                                        cityPref.setSummary(sharedPreferences.getString(key, ""));
                                    }
                                }
                            });

                }
            }
        }
    }

    private boolean isEmailValid(@NonNull String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
