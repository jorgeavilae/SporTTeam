package com.usal.jorgeav.sportapp.preferences;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String TAG = SettingsFragment.class.getSimpleName();

    public static final String KEY_PREF_CITY = "pref_city";
    public static final String KEY_PREF_EMAIL = "pref_email";
    public static final String KEY_PREF_PASSWORD = "pref_password";
    public static final String KEY_PREF_RESET = "pref_reset";
    public static final String KEY_PREF_DELETE = "pref_delete";

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

        //Add click listener for preference actions
        Preference deletePref = findPreference(KEY_PREF_DELETE);
        preparePreferenceDeleteUser(deletePref);
        Preference resetPref = findPreference(KEY_PREF_RESET);
        preparePreferenceResetUser(resetPref);

        //Write summaries
        EditTextPreference cityPref = (EditTextPreference) findPreference(KEY_PREF_CITY);
        cityPref.setSummary(UtilesPreferences.getCurrentUserCity(getActivity()));
        cityPref.setText("");
        EditTextPreference emailPref = (EditTextPreference) findPreference(KEY_PREF_EMAIL);
        emailPref.setSummary(Utiles.getCurrentUserEmail());
        emailPref.setText("");
        EditTextPreference passPref = (EditTextPreference) findPreference(KEY_PREF_PASSWORD);
        passPref.setText("");
    }

    private void preparePreferenceResetUser(Preference deletePref) {
        deletePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.pref_reset_title)
                        .setMessage(R.string.dialog_msg_reset_user)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null)
                                    FirebaseActions.deleteCurrentUser(user.getUid(), SettingsFragment.this, false);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null);
                dialog.create().show();
                return true;
            }
        });
    }

    private void preparePreferenceDeleteUser(Preference deletePref) {
        deletePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.pref_delete_title)
                        .setMessage(R.string.dialog_msg_delete_user)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final ProgressDialog progressDialog = ProgressDialog
                                        .show(getActivity(), "", getString(R.string.loading), true, false);

                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    final String uid = user.getUid();
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if (task.isSuccessful()) {
                                                FirebaseActions.deleteCurrentUser(uid, SettingsFragment.this, true);
                                            } else if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                                                displayReauthenticateDialog(user, new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getActivity(), R.string.toast_login_success, Toast.LENGTH_SHORT).show();
                                                            Log.i(TAG, "User re-authenticated.");
                                                            FirebaseActions.deleteCurrentUser(uid, SettingsFragment.this, true);
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(getActivity(), R.string.toast_error_deleting_user, Toast.LENGTH_SHORT).show();
                                                Exception e = task.getException();
                                                Log.e(TAG, "onComplete: Error deleting user", e);
                                                if (e != null) e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null);
                dialog.create().show();
                return true;
            }
        });
    }

    public void userDataDeleted(final String myUserID, final boolean deleteUser) {
        Log.i(TAG, "userDataDeleted: " + myUserID);
        /* https://stackoverflow.com/a/3875204/4235666
         * https://developer.android.com/reference/android/app/Activity.html#runOnUiThread(java.lang.Runnable)
         */
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (deleteUser) {
                        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                                .child(myUserID).removeValue();
                        getActivity().onBackPressed();
                    } else
                        Toast.makeText(getActivity(), R.string.toast_user_reset_success, Toast.LENGTH_SHORT).show();

                }
            });
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
        switch (key) {
            case KEY_PREF_CITY:
                CityAutocompleteEditTextPreference cityPref = (CityAutocompleteEditTextPreference) findPreference(key);

                //Update User and reload data
                String myUid = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(myUid)) return;
                FirebaseActions.updateUserCityAndReload(myUid, cityPref.citySelectedName, cityPref.citySelectedCoord);

                // Set summary to be the user-description for the selected value
                cityPref.setSummary(cityPref.citySelectedName);
                cityPref.setText("");
                break;
            case KEY_PREF_EMAIL:
                String email = sharedPreferences.getString(key, "");
                if (!TextUtils.isEmpty(email))
                    if (isEmailValid(email)) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            String oldEmail = user.getEmail();
                            if (oldEmail != null && !oldEmail.equals(email)) {
                                updateEmail(user, email, key);
                            } else
                                Toast.makeText(getActivity(), R.string.toast_change_email_equals, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
                    }
                break;
            case KEY_PREF_PASSWORD:
                 String password = sharedPreferences.getString(key, "");
                if (password.length() > 6) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null)
                        updatePassword(user, password);
                } else
                    Toast.makeText(getActivity(), R.string.error_invalid_password, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void updateEmail(final FirebaseUser user, final String email, final String key) {
        final ProgressDialog progressDialog = ProgressDialog
                .show(getActivity(), "", getString(R.string.loading), true, false);

        //Update email in FirebaseAuth
        final String oldEmail = user.getEmail();
        user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), R.string.toast_email_updated, Toast.LENGTH_SHORT).show();

                    //Update Preference summary
                    EditTextPreference emailPref = (EditTextPreference) findPreference(key);
                    emailPref.setSummary(email);
                    emailPref.setText("");

                    //Send email verification
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.dialog_title_email_changed)
                                    .setMessage(R.string.dialog_msg_email_changed)
                                    .setPositiveButton(android.R.string.ok, null);
                            builder.create().show();
                        }
                    });

                    //Update email in FirebaseDatabase
                    FirebaseActions.updateUserEmail(user.getUid(), email);

                    //Update email in ContentProvider Email_logged
                    ContentValues cv = new ContentValues();
                    cv.put(SportteamContract.EmailLoggedEntry.EMAIL, email);
                    getActivity().getContentResolver().update(
                            SportteamContract.EmailLoggedEntry.CONTENT_EMAIL_LOGGED_URI, cv,
                            SportteamContract.EmailLoggedEntry.EMAIL + " = ? ",
                            new String[]{oldEmail});
                } else if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                    // Re-authenticate and retry update email
                    displayReauthenticateDialog(user, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), R.string.toast_login_success, Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "User re-authenticated.");
                                updateEmail(user, email, key);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), R.string.toast_error_updating_email, Toast.LENGTH_SHORT).show();
                    Exception e = task.getException();
                    Log.e(TAG, "onComplete: Error updating email", e);
                    if (e != null) e.printStackTrace();
                }
            }
        });
    }

    private void updatePassword(final FirebaseUser user, final String password) {
        final ProgressDialog progressDialog = ProgressDialog
                .show(getActivity(), "", getString(R.string.loading), true, false);

        //Update password in FirebaseAuth
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), R.string.toast_password_updated, Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                            displayReauthenticateDialog(user, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), R.string.toast_login_success, Toast.LENGTH_SHORT).show();
                                        Log.i(TAG, "User re-authenticated.");
                                        updatePassword(user, password);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), R.string.toast_error_updating_password, Toast.LENGTH_SHORT).show();
                            Exception e = task.getException();
                            Log.e(TAG, "onComplete: Error updating password", e);
                            if (e != null) e.printStackTrace();
                        }

                    }
                });
    }

    private void displayReauthenticateDialog(final FirebaseUser user, final OnCompleteListener<Void> listener) {
        // Set view
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.reauthenticate_dialog, null);
        final EditText emailEditText = (EditText) dialogView.findViewById(R.id.reauthenticate_dialog_email);
        final EditText passEditText = (EditText) dialogView.findViewById(R.id.reauthenticate_dialog_password);
        ImageView visibleButton = (ImageView) dialogView.findViewById(R.id.reauthenticate_dialog_visible_pass);
        visibleButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    passEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    passEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                return true;
            }
        });

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setTitle(R.string.reauthenticate)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String emailStr = emailEditText.getText().toString();
                        String passStr = passEditText.getText().toString();

                        if (!TextUtils.isEmpty(emailStr) && isEmailValid(emailStr) && passStr.length() > 6) {
                            AuthCredential credential = EmailAuthProvider.getCredential(emailStr, passStr);
                            user.reauthenticate(credential).addOnCompleteListener(listener)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //TODO prevent from logout
                                            // If fails, logout is automatic
                                            Log.d(TAG, "User re-authenticated fails.");
                                            Toast.makeText(getActivity(), R.string.error_incorrect_password,
                                                    Toast.LENGTH_SHORT).show();

                                            // onBackPressed to display LoginActivity again
                                            getActivity().onBackPressed();
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private boolean isEmailValid(@NonNull String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
