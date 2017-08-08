package com.usal.jorgeav.sportapp.mainactivities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamDBHelper;
import com.usal.jorgeav.sportapp.fields.FieldsMapFragment;
import com.usal.jorgeav.sportapp.network.SportteamSyncUtils;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.preferences.SettingsActivity;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ActivityContracts.ActionBarIconManagement,
        ActivityContracts.FragmentManagement {
    private final static String TAG = BaseActivity.class.getSimpleName();
    private final static String BUNDLE_SAVE_FRAGMENT_INSTANCE = "BUNDLE_SAVE_FRAGMENT_INSTANCE";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.contentFrame)
    FrameLayout mContentFrame;
    @BindView(R.id.main_activity_progressbar)
    ProgressBar mProgressbar;
    BaseFragment mDisplayedFragment;
    ActionBarDrawerToggle mToggle;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Boolean shouldDetachFirebaseListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO borrar
//        debugTrickyErrors();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mToggle);
        //https://stackoverflow.com/questions/17025957/disable-gesture-listener-on-drawerlayout
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mNavigationView.setNavigationItemSelectedListener(this);

        hideContent();

        //TODO borrar
//        reiniciarContentProviderYSalir();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser fuser = firebaseAuth.getCurrentUser();
                if (fuser != null) {
                    // User is signed in
                    Log.d(TAG, "userID: "+fuser.getUid());
                    setUserInfoInNavigationDrawer();
                    // Initialization is cases when user is already logged.
                    SportteamSyncUtils.initialize(BaseActivity.this);

                    // Diplayed fragment is null on Initialization OR when FieldsMapFragment is
                    // displayed since FieldsMapFragment is not a BaseFragment.
                    if(mDisplayedFragment == null && getSupportFragmentManager()
                            .findFragmentByTag(FieldsMapFragment.FRAGMENT_TAG) == null)
                        startMainFragment();
                } else {
                    // User is signed out
                    Log.d(TAG, "userID: null");
                    SportteamSyncUtils.finalize(BaseActivity.this);
                    UtilesNotification.clearAllNotifications(BaseActivity.this);

                    Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        };

        shouldDetachFirebaseListener = true;
    }

    @Override
    public void setUserInfoInNavigationDrawer() {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser == null) return;
        ImageView image = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_image);
        TextView title = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_title);
        TextView subtitle = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_subtitle);

        title.setText(fuser.getDisplayName());
        subtitle.setText(fuser.getEmail());
        Glide.with(this)
                .load(fuser.getPhotoUrl())
                .error(R.drawable.profile_picture_placeholder)
                .placeholder(R.drawable.profile_picture_placeholder)
                .into(image);
    }

    private void debugTrickyErrors() {
        /* https://stackoverflow.com/a/29846562/4235666 */
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

    private void reiniciarContentProviderYSalir() {
        FirebaseAuth.getInstance().signOut();
        SportteamDBHelper db = new SportteamDBHelper(this);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_EMAIL_LOGGED);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_USER);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_USER_SPORTS);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_EVENT);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_EVENT_SIMULATED_PARTICIPANT);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_FIELD);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_FIELD_SPORTS);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_ALARM);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_FRIENDS);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_FRIENDS_REQUESTS);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_EVENTS_PARTICIPATION);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_EVENT_INVITATIONS);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_EVENTS_REQUESTS);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDisplayedFragment != null && getSupportFragmentManager() != null)
            getSupportFragmentManager().putFragment(outState, BUNDLE_SAVE_FRAGMENT_INSTANCE, mDisplayedFragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: ");
        mDisplayedFragment = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SAVE_FRAGMENT_INSTANCE)) {
            try {
                mDisplayedFragment = (BaseFragment) getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_SAVE_FRAGMENT_INSTANCE);
            } catch (IllegalStateException e) { e.printStackTrace(); }
        } else {
            onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_profile));
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
            mToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideSoftKeyboard();
        mToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "onNavigationItemSelected: "+item.getTitle());

        if (id == R.id.nav_sign_out) {
            if (mDisplayedFragment != null || getSupportFragmentManager()
                    .findFragmentByTag(FieldsMapFragment.FRAGMENT_TAG) != null) {
                getSupportFragmentManager().beginTransaction().remove(mDisplayedFragment).commit();
                mDisplayedFragment = null;
            }
            mAuth.signOut();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else {
            Intent intent;
            switch (id) {
                default: case R.id.nav_profile:
                    intent = new Intent(this, ProfileActivity.class); break;
                case R.id.nav_notifications:
                    intent = new Intent(this, NotificationsActivity.class); break;
                case R.id.nav_events:
                    intent = new Intent(this, EventsActivity.class); break;
                case R.id.nav_friends:
                    intent = new Intent(this, FriendsActivity.class); break;
                case R.id.nav_alarms:
                    intent = new Intent(this, AlarmsActivity.class); break;
                case R.id.nav_fields:
                    intent = new Intent(this, FieldsActivity.class); break;
            }
            // Do not invoke detachListeners in onPause if it's a
            // navigation between activities of Nav menu
            shouldDetachFirebaseListener = false;

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        mNavigationView.setCheckedItem(id);
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* https://stackoverflow.com/a/6357330/4235666 */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void startMainFragment() {
        // Activities must implement this
    }

    @Override
    public void initFragment(@NonNull Fragment fragment, boolean isOnBackStack) {
        hideContent();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFrame, fragment);
        if (isOnBackStack) transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
    @Override
    public void initFragment(@NonNull Fragment fragment, boolean isOnBackStack, String tag) {
        hideContent();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFrame, fragment, tag);
        if (isOnBackStack) transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void setCurrentDisplayedFragment(String title, BaseFragment fragment) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
        mDisplayedFragment = fragment;
    }

    @Override
    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

    @Override
    public void setToolbarAsNav() {
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawer.openDrawer(GravityCompat.START);
                }
            });
        }
        toolbarIconTransition.postDelayed(transitionToNav, 0);
    }

    @Override
    public void setToolbarAsUp() {
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
        toolbarIconTransition.postDelayed(transitionToUp, 0);
    }

    Handler toolbarIconTransition = new Handler();
    float currentOffset = 0;
    Runnable transitionToUp = new Runnable() {
        @Override
        public void run() {
            float offset = currentOffset + 0.1f;
            if (offset > 1f) {
                mToggle.onDrawerOpened(mDrawer);
                currentOffset = 1f;
                toolbarIconTransition.removeCallbacks(transitionToUp);
            } else {
                mToggle.onDrawerSlide(mDrawer, offset);
                currentOffset = offset;
                toolbarIconTransition.postDelayed(this, 10);
            }
        }
    };
    Runnable transitionToNav = new Runnable() {
        @Override
        public void run() {
            float offset = currentOffset - 0.1f;
            if (offset < 0f) {
                mToggle.onDrawerClosed(mDrawer);
                currentOffset = 0f;
                toolbarIconTransition.removeCallbacks(transitionToNav);
            } else {
                mToggle.onDrawerSlide(mDrawer, offset);
                currentOffset = offset;
                toolbarIconTransition.postDelayed(this, 10);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        toolbarIconTransition.removeCallbacks(transitionToNav);
        toolbarIconTransition.removeCallbacks(transitionToUp);

        // This prevent to detach listeners on orientation changes and activity transitions
        if (isFinishing() && shouldDetachFirebaseListener) FirebaseSync.detachListeners();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void showContent() {
        mContentFrame.setVisibility(View.VISIBLE);
        mProgressbar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideContent() {
        mContentFrame.setVisibility(View.INVISIBLE);
        mProgressbar.setVisibility(View.VISIBLE);
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
