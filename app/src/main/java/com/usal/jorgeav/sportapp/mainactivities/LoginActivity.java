package com.usal.jorgeav.sportapp.mainactivities;

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
import com.usal.jorgeav.sportapp.network.SportteamSyncUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private static final String TAG = LoginActivity.class.getSimpleName();


    @BindView(R.id.login_logo_name)
    ImageView loginLogo;
    @BindView(R.id.email)
    AutoCompleteTextView mEmailView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.login_visible_pass)
    ImageView visiblePassButton;
    @BindView(R.id.email_sign_in_button)
    Button mEmailSignInButton;
    @BindView(R.id.new_user_button)
    TextView mNewUserButton;
    @BindView(R.id.reset_password_button)
    TextView mResetPassword;
    @BindView(R.id.login_progress)
    View mProgressView;
    @BindView(R.id.login_form)
    View mLoginFormView;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

        // Set up the login form.
        populateAutoComplete();

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
                final String emailAddress = mEmailView.getText().toString();
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
                                                        Toast.makeText(LoginActivity.this, R.string.email_sent,
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
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        //If Activity rotate while downloading Firebase data
        if (mAuth.getCurrentUser() != null) showProgress(true);
            //If the user is no logged yet
        else SportteamSyncUtils.finalize(this);

    }

    private void startNewUserForResult() {
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) //User created
            initLoadMyProfile();
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, LoginActivity.this);
    }

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
                                initLoadMyProfile();
                            }
                        }
                    });
        }
    }

    private void initLoadMyProfile() {
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) { //TODO isEmailVerified()
//            if (fUser.isEmailVerified()) {
            deleteContentProvider();

            //Add email to emails logged table
            ContentValues cv = new ContentValues();
            cv.put(SportteamContract.EmailLoggedEntry.EMAIL, fUser.getEmail());
            getContentResolver().insert(SportteamContract.EmailLoggedEntry.CONTENT_EMAIL_LOGGED_URI, cv);

            // The user is logged and his data is in Firebase. Retrieve that data and
            // populate Content Provider. Later finishLoadMyProfile() will be invoked
            SportteamSyncUtils.initialize(LoginActivity.this);
//            } else {
//                showProgress(false);
//                AlertDialog.Builder builder = new AlertDialog.Builder(this)
//                        .setTitle(R.string.dialog_title_verify_email)
//                        .setMessage(R.string.dialog_msg_verify_email)
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                fUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        Log.d(TAG, "sendEmailVerification: onComplete: "+task.isSuccessful());
//                                        Toast.makeText(LoginActivity.this, R.string.email_sent,
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        })
//                        .setNegativeButton(android.R.string.no, null)
//                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
//                            @Override
//                            public void onDismiss(DialogInterface dialogInterface) {
//                                FirebaseAuth.getInstance().signOut();
//                            }
//                        });
//                builder.create().show();
//            }
        }
    }

    public void finishLoadMyProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

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

    private boolean isEmailValid(@NonNull String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                SportteamContract.EmailLoggedEntry.CONTENT_EMAIL_LOGGED_URI,
                SportteamContract.EmailLoggedEntry.EMAIL_LOGGED_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        List<String> emails = new ArrayList<>();
        //Loader reuses Cursor so move to first position
        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext())
            emails.add(data.getString(SportteamContract.EmailLoggedEntry.COLUMN_EMAIL));
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mEmailView.setAdapter(null);
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /* https://stackoverflow.com/a/1109108/4235666 */
    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

