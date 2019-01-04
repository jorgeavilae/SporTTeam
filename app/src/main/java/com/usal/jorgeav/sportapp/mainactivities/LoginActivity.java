package com.usal.jorgeav.sportapp.mainactivities;

import android.annotation.SuppressLint;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamDBHelper;
import com.usal.jorgeav.sportapp.network.SportteamSyncInitialization;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Actividad para el inicio de sesión. Se encarga de comprobar el email de usuario y su
 * contraseña.
 * <p>
 * Incorpora una funcionalidad para sugerir direcciones de email que ya hayan sido utilizadas para
 * iniciar sesión.
 * <p>
 * También contiene la funcionalidad que permite recibir un email para recuperar contraseñas
 * olvidadas.
 * <p>
 * Por último, contiene un botón para acceder a la pantalla de creación de usuarios.
 * <p>
 * Implementa {@link LoaderCallbacks} para permitir consultar, al Proveedor de Contenido,
 * el email de los usuarios que ya hayan iniciado sesión en alguna ocasión.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    /**
     * Nombre de la clase
     */
    private static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * Imagen del logo de la aplicación
     */
    @BindView(R.id.login_logo_name)
    ImageView loginLogo;
    /**
     * Cajón de edición de texto para la dirección de email
     */
    @BindView(R.id.email)
    AutoCompleteTextView mEmailView;
    /**
     * Cajón de edición de texto para la contraseña
     */
    @BindView(R.id.password)
    EditText mPasswordView;
    /**
     * Imagen para mostrar la contraseña
     */
    @BindView(R.id.login_visible_pass)
    ImageView visiblePassButton;
    /**
     * Botón para iniciar sesión
     */
    @BindView(R.id.email_sign_in_button)
    Button mEmailSignInButton;
    /**
     * Texto para iniciar el proceso de creación de usuario
     */
    @BindView(R.id.new_user_button)
    TextView mNewUserButton;
    /**
     * Texto para iniciar el proceso de recuperación de contraseña
     */
    @BindView(R.id.reset_password_button)
    TextView mResetPassword;
    /**
     * Barra de progreso
     */
    @BindView(R.id.login_progress)
    View mProgressView;
    /**
     * Contenedor donde se emplazan los elementos de la interfaz
     */
    @BindView(R.id.login_form)
    ConstraintLayout mLoginFormView;

    /**
     * Referencia a FirebaseAuth para comprobar si hay un usuario con la sesión iniciada
     *
     * @see <a href= "https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/auth/FirebaseAuth">
     * FirebaseAuth</a>
     */
    private FirebaseAuth mAuth;

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz, con la ayuda de
     * ButterKnife, y se inicializan todas las variables.
     *
     * @param savedInstanceState estado de la Actividad guardado en una posible rotación de
     *                           la pantalla, o null.
     * @see <a href= "http://jakewharton.github.io/butterknife/">ButterKnife</a>
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setSupportActionBar(null);

        Glide.with(this)
                .load(R.drawable.logo_name)
                .animate(android.R.anim.fade_in)
                .into(loginLogo);

        populateAutoCompleteTextViewEmail();

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_ACTION_GO) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        visiblePassButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                return true;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                attemptLogin();
            }
        });

        mNewUserButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewUserForResult();
            }
        });

        mResetPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showResetPasswordDialog(mEmailView.getText().toString());
            }
        });

        mAuth = FirebaseAuth.getInstance();
        //If Activity rotate while downloading Firebase data: User is signed in
        if (mAuth.getCurrentUser() != null) showProgress(true);
            //If the user is no logged yet
        else SportteamSyncInitialization.finalize(this);

    }

    /**
     * Inicia el {@link Loader} que carga el email de los usuarios que ya han iniciado sesión
     * en alguna ocasión en este teléfono
     */
    private void populateAutoCompleteTextViewEmail() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Método de {@link LoaderCallbacks} para la creación del {@link Loader}. Realiza la
     * consulta al Content Provider de los emails de usuarios.
     *
     * @param i      identificador de la consulta
     * @param bundle contenedor de parámetros para la consulta
     * @return el {@link Loader} de la consulta con un {@link Cursor} dónde se almacenará la
     * respuesta
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                SportteamContract.EmailLoggedEntry.CONTENT_EMAIL_LOGGED_URI,
                SportteamContract.EmailLoggedEntry.EMAIL_LOGGED_COLUMNS,
                null, null, null);
    }

    /**
     * Método de {@link LoaderCallbacks} para la finalización de la consulta del {@link Loader}.
     * Obtiene el {@link Cursor} con los resultados de la consulta y lo utiliza para sugerir
     * entradas para {@link #mEmailView}
     *
     * @param cursorLoader loader de la consulta
     * @param data         resultado de la consulta
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        addEmailsToAutoComplete(data);
    }

    /**
     * Método de {@link LoaderCallbacks} invocado cuando se reinicia el {@link Loader}. Utilizado
     * para borrar los resultados de la consulta anterior.
     *
     * @param cursorLoader Loader que se reinicia
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        addEmailsToAutoComplete(null);
    }

    /**
     * Invocado desde el {@link LoaderCallbacks} para establecer las entradas sugeridas en el
     * cajón de introducción de la dirección de email.
     * <p>
     * Del {@link Cursor} extrae las direcciones de email y utiliza esa lista para crear un
     * {@link ArrayAdapter} que asocia con {@link #mEmailView}
     *
     * @param data conjunto de emails obtenidos de la base de datos
     */
    private void addEmailsToAutoComplete(Cursor data) {
        ArrayAdapter<String> adapter = null;
        if (data != null) {
            List<String> emails = new ArrayList<>();

            //Loader reuses Cursor so move to first position
            for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext())
                emails.add(data.getString(SportteamContract.EmailLoggedEntry.COLUMN_EMAIL));

            adapter = new ArrayAdapter<>(LoginActivity.this,
                    android.R.layout.simple_dropdown_item_1line, emails);
        }

        mEmailView.setAdapter(adapter);
    }

    /**
     * Comprueba los datos introducidos de email y contraseña y, con ellos, intenta iniciar la
     * sesión a través de {@link #mAuth}
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancelLogin = false;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            cancelLogin = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            cancelLogin = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            cancelLogin = true;
        }

        if (!cancelLogin) {
            // Show a progress spinner, and perform the user login attempt.
            showProgress(true);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                showProgress(false);
                                Log.e(TAG, "signInWithEmailAndPassword: onComplete: ", task.getException());
                                mPasswordView.setError(getString(R.string.error_incorrect_password));
                                Toast.makeText(LoginActivity.this, R.string.error_incorrect_password,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                initLoadMyProfile(true);
                            }
                        }
                    });
        }
    }

    /**
     * Inicia la Actividad para crear un nuevo usuario {@link NewUserActivity}.
     * Método invocado al pulsar sobre {@link #mNewUserButton}.
     */
    private void startNewUserForResult() {
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * Se ejecuta al finalizar la ejecución de la Actividad de creación de usuario. Inicia la
     * carga de datos del usuario nuevo.
     *
     * @param requestCode código con el que se inició la Actividad de creación de usuario
     * @param resultCode  código con el que finaliza la Actividad de creación de usuario
     * @param data        datos asociados a la finalización de la Actividad de creación de usuario
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) //User created
            initLoadMyProfile(false);
    }

    /**
     * Inicia la carga de datos del usuario que acaba de iniciar sesión: borra los datos que
     * todavía pueda contener el Proveedor de Contenido de una sesión anterior e invoca
     * {@link SportteamSyncInitialization#initialize(Context)}
     * <p>
     * Comprueba que el email que se introdujo en el registro ha sido verificado y, en caso
     * contrario, exige que se compruebe antes finalizar el proceso de inicio de sesión.
     *
     * @param checkIfEmailIsVerified true si se requiere que el email haya sido verificado, false
     *                               si no se requiere comprobación (en la reciente creación de
     *                               usuario)
     */
    private void initLoadMyProfile(boolean checkIfEmailIsVerified) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) {
            if (!checkIfEmailIsVerified || fUser.isEmailVerified()) {
                deleteContentProvider();

                //Add email to emails logged table
                ContentValues cv = new ContentValues();
                cv.put(SportteamContract.EmailLoggedEntry.EMAIL, fUser.getEmail());
                getContentResolver().insert(SportteamContract.EmailLoggedEntry.CONTENT_EMAIL_LOGGED_URI, cv);

                // The user is logged and his data is in Firebase. Retrieve that data and
                // populate Content Provider. Later finishLoadMyProfile() will be invoked
                SportteamSyncInitialization.initialize(LoginActivity.this);
            } else {
                showProgress(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_title_verify_email)
                        .setMessage(R.string.dialog_msg_verify_email)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.i(TAG, "sendEmailVerification: onComplete: " + task.isSuccessful());
                                        Toast.makeText(LoginActivity.this, R.string.email_sent,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                FirebaseAuth.getInstance().signOut();
                            }
                        });
                builder.create().show();
            }
        }
    }

    /**
     * Borra el contenido de las tablas del Proveedor de Contenido
     */
    private void deleteContentProvider() {
        SportteamDBHelper db = new SportteamDBHelper(this);
        db.getWritableDatabase().execSQL("DELETE FROM " + SportteamContract.TABLE_USER);
        db.getWritableDatabase().execSQL("DELETE FROM " + SportteamContract.TABLE_USER_SPORTS);
        db.getWritableDatabase().execSQL("DELETE FROM " + SportteamContract.TABLE_EVENT);
        db.getWritableDatabase().execSQL("DELETE FROM " + SportteamContract.TABLE_EVENT_SIMULATED_PARTICIPANT);
        db.getWritableDatabase().execSQL("DELETE FROM " + SportteamContract.TABLE_FIELD);
        db.getWritableDatabase().execSQL("DELETE FROM " + SportteamContract.TABLE_FIELD_SPORTS);
        db.getWritableDatabase().execSQL("DELETE FROM " + SportteamContract.TABLE_ALARM);
        db.getWritableDatabase().execSQL("DELETE FROM " + SportteamContract.TABLE_FRIENDS);
        db.getWritableDatabase().execSQL("DELETE FROM " + SportteamContract.TABLE_FRIENDS_REQUESTS);
        db.getWritableDatabase().execSQL("DELETE FROM " + SportteamContract.TABLE_EVENTS_PARTICIPATION);
        db.getWritableDatabase().execSQL("DELETE FROM " + SportteamContract.TABLE_EVENT_INVITATIONS);
        db.getWritableDatabase().execSQL("DELETE FROM " + SportteamContract.TABLE_EVENTS_REQUESTS);
    }

    /**
     * Invocado al finalizar la carga de los datos del usuario en el Proveedor de Contenido,
     * lo que indica que ya pueden ser mostrados después del inicio de sesión.
     */
    public void finishLoadMyProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Muestra un cuadro de diálogo sobre la pantalla para confirmar el envío de un email para
     * la recuperación de la contraseña.
     *
     * @param emailAddress dirección email introducida para la cual se ha olvidado la contraseña.
     */
    private void showResetPasswordDialog(final String emailAddress) {
        if (!TextUtils.isEmpty(emailAddress) && isEmailValid(emailAddress)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                    .setTitle(R.string.forgot_password)
                    .setMessage(R.string.dialog_msg_forgot_password)
                    .setPositiveButton(R.string.send_it, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this,
                                                        R.string.email_sent,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null);
            builder.create().show();
        } else
            Toast.makeText(LoginActivity.this, R.string.error_invalid_email,
                    Toast.LENGTH_SHORT).show();
    }

    /**
     * Valida la cadena de texto introducida en el cajón de texto del email comprobando que
     * tenga un patrón de dirección de email: {@link Patterns#EMAIL_ADDRESS}.
     *
     * @param email cadena de texto introducida
     * @return true si la cadena de texto es válida, false en caso contrario
     */
    private boolean isEmailValid(@NonNull String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Valida la cadena de texto introducida en el cajón de texto de la contraseña comprobando
     * que tenga más de 6 caracteres.
     *
     * @param password cadena de texto introducida
     * @return true si la cadena de texto es válida, false en caso contrario
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    /**
     * Muestra la interfaz con los botones y los cajones de introducción de texto
     * {@link #mLoginFormView} o la barra de progreso {@link #mProgressView}.
     *
     * @param show true si se quiere mostrar la barra de progreso, false si se quiere mostrar el
     *             resto de la interfaz
     */
    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /**
     * Obtiene una referencia a la {@link View} que tiene el foco de la interfaz y, si esta
     * mostrando el teclado flotante en la pantalla, lo esconde
     *
     * @see <a href= "https://stackoverflow.com/a/17789187/4235666">
     * (StackOverflow) Close/hide the Android Soft Keyboard</a>
     */
    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

