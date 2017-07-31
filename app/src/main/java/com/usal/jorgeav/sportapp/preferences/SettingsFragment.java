package com.usal.jorgeav.sportapp.preferences;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
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
    public static final String KEY_PREF_PASSWORD = "pref_password";
    public static final String KEY_PREF_DELETE = "pref_delete";

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

        Preference deletePref = findPreference(KEY_PREF_DELETE);
        deletePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("BORRAR USUARIO")
                        .setMessage("Estas seguro de que quieres BORRAR este usuario y" +
                                " PERDER todos los datos guardados?")
                        .setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //TODO
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create();
                dialog.show();
                return true;
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
        if (key.equals(KEY_PREF_CITY)) {
            String myUid = Utiles.getCurrentUserId();
            if (TextUtils.isEmpty(myUid)) return;

            CityAutocompleteEditTextPreference cityPref = (CityAutocompleteEditTextPreference)findPreference(key);
            // Set summary to be the user-description for the selected value
            cityPref.setSummary(sharedPreferences.getString(key, ""));

            //Update User and reload data
            FirebaseActions.updateUserCityAndReload(myUid, cityPref.citySelectedName, cityPref.citySelectedCoord);
        } else if (key.equals(KEY_PREF_EMAIL)) {
            final String email = sharedPreferences.getString(key, "");
            if (!TextUtils.isEmpty(email) && isEmailValid(email)) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String oldEmail = user.getEmail();
                    if (oldEmail != null && !oldEmail.equals(email)) {
                        updateEmail(user, email, sharedPreferences, key);
                    } else
                        Toast.makeText(getActivity(), "That is your current email", Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(getActivity(), "Not a valid email", Toast.LENGTH_SHORT).show();
        } else if (key.equals(KEY_PREF_PASSWORD)) {
            final String password = sharedPreferences.getString(key, "");
            if (password.length() > 6) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null)
                    updatePassword(user, password);
            } else
                Toast.makeText(getActivity(), "Not a valid password", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmail(final FirebaseUser user, final String email,
                             final SharedPreferences sharedPreferences, final String key) {
        //Update email in FirebaseAuth
        final String oldEmail = user.getEmail();
        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");

                            //Update Preference summary
                            Preference emailPref = findPreference(key);
                            emailPref.setSummary(sharedPreferences.getString(key, ""));

                            //Send email verification
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Tu email ha sido cambiado")
                                            .setMessage("Recibiras un correo en tu direccion que proporcionaste al registrarte" +
                                                    " por primera vez por si quieres revertir el proceso, y otro correo en" +
                                                    " la nueva direccion para confirmarlo.\n" +
                                                    "Para volver a identificarte cuando cierres la sesion, debes confirmar el" +
                                                    " correo de la nueva direccion.")
                                            .setPositiveButton("Ok", null);
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
                            displayReauthenticateDialog(user, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User re-authenticated.");
                                        updateEmail(user, email, sharedPreferences, key);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Error updating email", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onComplete: Error updating email", task.getException());
                            task.getException().printStackTrace();
                        }

                    }
                });
    }

    private void updatePassword(final FirebaseUser user, final String password) {
        //Update password in FirebaseAuth
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                            Toast.makeText(getActivity(), "Password updated.", Toast.LENGTH_SHORT).show();

                        } else if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                            displayReauthenticateDialog(user, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User re-authenticated");
                                        updatePassword(user, password);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Error updating email", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onComplete: Error updating email", task.getException());
                            task.getException().printStackTrace();
                        }

                    }
                });
    }

    private void displayReauthenticateDialog(final FirebaseUser user, final OnCompleteListener<Void> listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.reauthenticate_dialog, null);
        builder.setView(dialogView);

        final EditText emailEditText = (EditText) dialogView.findViewById(R.id.reauthenticate_dialog_email);
        final EditText passEditText = (EditText) dialogView.findViewById(R.id.reauthenticate_dialog_password);
        //TODO button show password
//        final Button visibleButton = (Button) dialogView.findViewById(R.id.reauthenticate_dialog_visible_pass);
//        visibleButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS)
//                    passEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                else if (event.getAction() == MotionEvent.ACTION_BUTTON_RELEASE)
//                    passEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                return true;
//            }
//        });

        builder.setTitle("Reauthenticate")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String emailStr = emailEditText.getText().toString();
                        String passStr = passEditText.getText().toString();

                        if (!TextUtils.isEmpty(emailStr) && isEmailValid(emailStr) && passStr.length() > 6) {
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(emailStr, passStr);
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(listener)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "User re-authenticated fails.");
                                    Toast.makeText(getActivity(), "Incorrect email/password", Toast.LENGTH_SHORT).show();
                                    getActivity().onBackPressed();
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("Cancel", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
