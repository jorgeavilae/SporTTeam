package com.usal.jorgeav.sportapp.preferences;

import android.annotation.SuppressLint;
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
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.actions.UserFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

/**
 * Fragmento utilizado para mostrar y controlar el menú de preferencias de la aplicación.
 * Carga el menú desde el archivo de recursos correspondiente (preferences.xml) y asigna un
 * Listener para cada uno de los elementos.
 * <p>
 * Las opciones para borrar los datos del usuario tienen asociadas un Listener que muestra un cuadro
 * de diálogo para confirmar la acción. El resto utilizan
 * {@link SharedPreferences.OnSharedPreferenceChangeListener} para escuchar los cambios.
 * <p>
 * Las opciones más sensibles como son las de cambio de email, cambio de contraseña o borrado del
 * usuario del sistema, puede que requieran de autenticación para confirmar los cambios. Para ello
 * se muestra un cuadro de diálogo con los cuadros de texto necesarios para re-introducir el par
 * email/contraseña.
 * <p>
 * Además, desde este Fragmento se puede cambiar la ciudad del usuario. Para esto se crea un
 * {@link CityAutocompleteEditTextPreference} que se muestra al seleccionar esta opción. Como en
 * otros lugares de la aplicación, sugiere ciudades a las que cambiar. Este cambio se almacena en
 * {@link SharedPreferences}, además de en el servidor, para que sea accesible desde cualquier
 * punto de la aplicación.
 */
public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * Nombre de la clase
     */
    private final static String TAG = SettingsFragment.class.getSimpleName();

    /**
     * Identificador de la preferencia de cambio de ciudad del usuario
     */
    public static final String KEY_PREF_CITY = "pref_city";
    /**
     * Identificador de la preferencia de cambio de dirección de email del usuario
     */
    public static final String KEY_PREF_EMAIL = "pref_email";
    /**
     * Identificador de la preferencia de cambio de contraseña
     */
    public static final String KEY_PREF_PASSWORD = "pref_password";
    /**
     * Identificador de la preferencia de reinicio de los datos del usuario y sus acciones
     */
    public static final String KEY_PREF_RESET = "pref_reset";
    /**
     * Identificador de la preferencia de borrado del usuario
     */
    public static final String KEY_PREF_DELETE = "pref_delete";

    /**
     * Constructor sin argumentos
     */
    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @return una nueva instancia de SettingsFragment
     */
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    /**
     * Carga las preferencias desde el archivo de recursos y les asigna un Listener o un sumario que
     * las describa.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
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

    /**
     * Asigna el Listener correspondiente a la preferencia de reinicio del usuario. Muestra un
     * cuadro de diálogo para confirma la acción que se realiza mediante
     * {@link UserFirebaseActions#deleteCurrentUser(String, SettingsFragment, boolean)}
     *
     * @param deletePref referencia a la preferencia a la que vincular el Listener
     */
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
                                    UserFirebaseActions.deleteCurrentUser(user.getUid(), SettingsFragment.this, false);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null);
                dialog.create().show();
                return true;
            }
        });
    }

    /**
     * Asigna el Listener correspondiente a la preferencia de borrado del usuario. Muestra un
     * cuadro de diálogo para confirma la acción que se realiza mediante
     * {@link UserFirebaseActions#deleteCurrentUser(String, SettingsFragment, boolean)}. Además de
     * borrar al usuario de la base de datos, también lo elimina de Firebase Auth. Puede que
     * requiera re-autenticación.
     *
     * @param deletePref referencia a la preferencia a la que vincular el Listener
     */
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
                                                UserFirebaseActions.deleteCurrentUser(uid, SettingsFragment.this, true);
                                            } else if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                                                displayAuthenticateDialog(user, new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getActivity(), R.string.toast_login_success, Toast.LENGTH_SHORT).show();
                                                            Log.i(TAG, "User re-authenticated.");
                                                            UserFirebaseActions.deleteCurrentUser(uid, SettingsFragment.this, true);
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

    /**
     * Invocado cuando finaliza el proceso de borrado de los datos del usuario. Dependiendo de
     * <var>deleteUser</var> muestra un mensaje de completado o continua borrando al usuario del
     * sistema. En este caso, finaliza el proceso borrando la foto de Firebase Storage, borrando el
     * perfil de Firebase Realtime Database y finalizando este Fragmento, volviendo atrás a la
     * pantalla de inicio de sesión.
     *
     * @param myUserID       identificador del usuario actual, que va a borrarse
     * @param profilePicture dirección de la foto de perfil que debe borrarse de Firebase Storage
     * @param deleteUser     true si se debe proceder con este proceso, o false si sólo deben borrarse
     *                       los datos de las acciones del usuario y no el usuario completo.
     */
    public void userDataDeleted(final String myUserID, final String profilePicture, final boolean deleteUser) {
        Log.i(TAG, "userDataDeleted: " + myUserID);
        /* https://stackoverflow.com/a/3875204/4235666
         * https://developer.android.com/reference/android/app/Activity.html#runOnUiThread(java.lang.Runnable)
         */
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (deleteUser) {
                        UserFirebaseActions.deleteOldUserPhoto(profilePicture);
                        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                                .child(myUserID).removeValue();
                        getActivity().onBackPressed();
                    } else
                        Toast.makeText(getActivity(), R.string.toast_user_reset_success, Toast.LENGTH_SHORT).show();

                }
            });
    }

    /**
     * Borra este Fragmento como {@link SharedPreferences.OnSharedPreferenceChangeListener}
     */
    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Registra este Fragmento como {@link SharedPreferences.OnSharedPreferenceChangeListener}
     */
    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Método de {@link SharedPreferences.OnSharedPreferenceChangeListener} invocado cada vez que
     * una de las preferencias cambia. Dependiendo de cual, actualiza la ciudad, el email o la
     * contraseña en los servidores de la aplicación.
     *
     * @param sharedPreferences nuevo valor de la preferencia
     * @param key               identificador de la preferencia actualizada
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case KEY_PREF_CITY:
                CityAutocompleteEditTextPreference cityPref = (CityAutocompleteEditTextPreference) findPreference(key);

                //Update User and reload data
                String myUid = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(myUid)
                        || cityPref.citySelectedName == null || cityPref.citySelectedCoord == null) {
                    Toast.makeText(getActivity(), R.string.error_invalid_city, Toast.LENGTH_SHORT).show();
                    break;
                }
                UserFirebaseActions.updateUserCityAndReload(myUid, cityPref.citySelectedName, cityPref.citySelectedCoord);

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

    /**
     * Actualiza la dirección de email del usuario en Firebase Authentication y Firebase Realtime
     * Database y en el Proveedor de Contenido. También envía un email de verificación de la nueva
     * dirección de correo.
     * <p>
     * Es posible que requiera de re-autenticación.
     *
     * @param user     usuario actual cuya sesión está iniciada
     * @param newEmail dirección de email nueva
     * @param key      identificador de la preferencia del cambio de email
     */
    private void updateEmail(final FirebaseUser user, final String newEmail, final String key) {
        final ProgressDialog progressDialog = ProgressDialog
                .show(getActivity(), "", getString(R.string.loading), true, false);

        //Update email in FirebaseAuth
        final String oldEmail = user.getEmail();
        user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), R.string.toast_email_updated, Toast.LENGTH_SHORT).show();

                    //Update Preference summary
                    EditTextPreference emailPref = (EditTextPreference) findPreference(key);
                    emailPref.setSummary(newEmail);
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
                    UserFirebaseActions.updateUserEmail(user.getUid(), newEmail);

                    //Update email in ContentProvider Email_logged
                    ContentValues cv = new ContentValues();
                    cv.put(SportteamContract.EmailLoggedEntry.EMAIL, newEmail);
                    getActivity().getContentResolver().update(
                            SportteamContract.EmailLoggedEntry.CONTENT_EMAIL_LOGGED_URI, cv,
                            SportteamContract.EmailLoggedEntry.EMAIL + " = ? ",
                            new String[]{oldEmail});
                } else if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                    // Re-authenticate and retry update email
                    displayAuthenticateDialog(user, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), R.string.toast_login_success, Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "User re-authenticated.");
                                updateEmail(user, newEmail, key);
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

    /**
     * Actualiza la contraseña del usuario en Firebase Authentication. Es posible que requiera de
     * re-autenticación.
     *
     * @param user        usuario actual cuya sesión está iniciada
     * @param newPassword contraseña nueva
     */
    private void updatePassword(final FirebaseUser user, final String newPassword) {
        final ProgressDialog progressDialog = ProgressDialog
                .show(getActivity(), "", getString(R.string.loading), true, false);

        //Update password in FirebaseAuth
        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), R.string.toast_password_updated, Toast.LENGTH_SHORT).show();
                } else if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                    displayAuthenticateDialog(user, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), R.string.toast_login_success, Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "User re-authenticated.");
                                updatePassword(user, newPassword);
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

    /**
     * Crea y muestra el cuadro de diálogo para re-autenticarse. Se compone de un elemento para
     * escribir la dirección de email, otro para escribir la contraseña, un botón para mostrar la
     * contraseña escrita. Si la autenticación falla se cierra la sesión automáticamente.
     *
     * @param user     usuario cuya sesión está iniciada actualmente
     * @param listener Listener con las acciones a realizar al finalizar la autenticación
     */
    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    private void displayAuthenticateDialog(final FirebaseUser user, final OnCompleteListener<Void> listener) {
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
                                            // If fails, logout is automatic
                                            Log.i(TAG, "User re-authenticated fails: "+e.getLocalizedMessage());
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

    /**
     * Comprueba si el patrón de la cadena de texto proporcionada coincide con el patrón de una
     * dirección de email.
     *
     * @param email cadena de texto a comprobar
     * @return true si coincide, false en otro caso.
     */
    private boolean isEmailValid(@NonNull String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
