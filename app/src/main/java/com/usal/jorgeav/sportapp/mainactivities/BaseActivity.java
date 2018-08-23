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
import android.text.TextUtils;
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
import com.usal.jorgeav.sportapp.network.SportteamSyncInitialization;
import com.usal.jorgeav.sportapp.network.firebase.actions.UserFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.preferences.SettingsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ActivityContracts.NavigationDrawerManagement,
        ActivityContracts.FragmentManagement {

    public abstract void startMainFragment();

    private final static String TAG = BaseActivity.class.getSimpleName();
    private final static String BUNDLE_SAVE_FRAGMENT_INSTANCE = "BUNDLE_SAVE_FRAGMENT_INSTANCE";
    public static final String FRAGMENT_TAG_IS_MAP = "FRAGMENT_TAG_IS_MAP";

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

    // False when changing Activity to not detach listeners in that cases
    private Boolean shouldDetachFirebaseListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mNavigationView.setNavigationItemSelectedListener(this);

        hideContent();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser fuser = firebaseAuth.getCurrentUser();
                if (fuser != null) {
                    // User is signed in
                    Log.i(TAG, "FirebaseUser logged ID: "+fuser.getUid());
                    setUserInfoInNavigationDrawer();

                    // Initialization for populate Content Provider and init Service if needed
                    SportteamSyncInitialization.initialize(BaseActivity.this);

                    // Displayed fragment is null on Initialization OR when FieldsMapFragment is
                    // displayed since FieldsMapFragment is not a BaseFragment.
                    if(mDisplayedFragment == null && getSupportFragmentManager()
                            .findFragmentByTag(FRAGMENT_TAG_IS_MAP) == null)
                        startMainFragment();
                } else {
                    // User is signed out
                    Log.i(TAG, "FirebaseUser logged ID: null");

                    // Finalize service
                    SportteamSyncInitialization.finalize(BaseActivity.this);

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
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;
        ImageView image = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_image);
        TextView title = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_title);
        TextView subtitle = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_subtitle);

        title.setText(firebaseUser.getDisplayName());
        subtitle.setText(firebaseUser.getEmail());
        Glide.with(this)
                .load(firebaseUser.getPhotoUrl())
                .error(R.drawable.profile_picture_placeholder)
                .placeholder(R.drawable.profile_picture_placeholder)
                .into(image);
    }

    // Method used to debug some not enough verbose errors
    @SuppressWarnings("unused")
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDisplayedFragment != null && getSupportFragmentManager() != null)
            getSupportFragmentManager().putFragment(outState, BUNDLE_SAVE_FRAGMENT_INSTANCE, mDisplayedFragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mDisplayedFragment = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SAVE_FRAGMENT_INSTANCE)) {
            try {
                mDisplayedFragment = (BaseFragment) getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_SAVE_FRAGMENT_INSTANCE);
            } catch (IllegalStateException e) { e.printStackTrace(); }
        } else {
            onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_search_events));
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
        if (item.getItemId() == R.id.nav_sign_out) {
            signOut();
        } else if (item.getItemId() == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else {
            simulateNavigationItemSelected(item.getItemId(), null, null);
        }

        mNavigationView.setCheckedItem(item.getItemId());
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void simulateNavigationItemSelected(int menuItemId, String intentExtraKey, String intentExtraValue) {
        Intent intent;
        switch (menuItemId) {
            default: case R.id.nav_profile:
                intent = new Intent(this, ProfileActivity.class); break;
            case R.id.nav_events:
                intent = new Intent(this, EventsActivity.class); break;
            case R.id.nav_search_events:
                intent = new Intent(this, SearchEventsActivity.class); break;
            case R.id.nav_notifications:
                intent = new Intent(this, NotificationsActivity.class); break;
            case R.id.nav_friends:
                intent = new Intent(this, FriendsActivity.class); break;
            case R.id.nav_alarms:
                intent = new Intent(this, AlarmsActivity.class); break;
            case R.id.nav_fields:
                intent = new Intent(this, FieldsActivity.class); break;
        }
        // Do not invoke detachListeners in onPause if it's a
        // navigation between activities
        shouldDetachFirebaseListener = false;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (intentExtraKey != null && !TextUtils.isEmpty(intentExtraKey))
            intent.putExtra(intentExtraKey, intentExtraValue);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /* https://stackoverflow.com/a/6357330/4235666 */
        setIntent(intent);
    }

    @Override
    public void signOut() {
        if (mDisplayedFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(mDisplayedFragment).commit();
            mDisplayedFragment = null;
        }

        // Delete token to stop receiving new notifications
        UserFirebaseActions.deleteUserToken(Utiles.getCurrentUserId());
        mAuth.signOut();
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

        // This prevent from detach listeners on orientation changes and activity transitions
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

    protected void shouldDetachFirebaseListener(boolean shouldI) {
        shouldDetachFirebaseListener = shouldI;
    }
}
