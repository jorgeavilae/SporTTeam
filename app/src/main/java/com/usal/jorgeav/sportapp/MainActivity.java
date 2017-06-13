package com.usal.jorgeav.sportapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamDBHelper;
import com.usal.jorgeav.sportapp.events.EventsFragment;
import com.usal.jorgeav.sportapp.fields.FieldsFragment;
import com.usal.jorgeav.sportapp.friends.FriendsFragment;
import com.usal.jorgeav.sportapp.network.FirebaseDatabaseActions;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ActivityContracts.ActionBarIconManagement,
        ActivityContracts.FragmentManagement {
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String BUNDLE_SAVE_FRAGMENT_INSTANCE = "BUNDLE_SAVE_FRAGMENT_INSTANCE";

    Toolbar mToolbar;
    DrawerLayout mDrawer;
    NavigationView mNavigationView;
    Fragment mDisplayedFragment;
    ActionBarDrawerToggle mToggle;
    FrameLayout mContentFrame;
    ProgressBar mProgressbar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: init");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mToggle);
        //https://stackoverflow.com/questions/17025957/disable-gesture-listener-on-drawerlayout
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mContentFrame = (FrameLayout) findViewById(R.id.contentFrame);
        mProgressbar = (ProgressBar) findViewById(R.id.main_activity_progressbar);
        hideContent();
//        FirebaseDatabaseActions.syncFirebaseDatabase();

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
                    FirebaseDatabaseActions.syncFirebaseDatabase();
                    if(mDisplayedFragment == null)
                        onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_profile));
                } else {
                    // User is signed out
                    FirebaseDatabaseActions.detachListeners();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
        Log.d(TAG, "onCreate: fin");
    }

    private void reiniciarContentProviderYSalir() {
        FirebaseAuth.getInstance().signOut();
        SportteamDBHelper db = new SportteamDBHelper(this);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_EVENT);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_FIELD);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_USER);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_USER_SPORTS);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_FRIENDS_REQUESTS);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_FRIENDS);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_EVENTS_PARTICIPATION);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_EVENT_INVITATIONS);
        db.getWritableDatabase().execSQL("DELETE FROM "+ SportteamContract.TABLE_EVENTS_REQUESTS);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO IllegalStateException: Fragment no longer exists for key BUNDLE_SAVE_FRAGMENT_INSTANCE: index 0
        if (mDisplayedFragment != null && getSupportFragmentManager() != null)
            getSupportFragmentManager().putFragment(outState, BUNDLE_SAVE_FRAGMENT_INSTANCE, mDisplayedFragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: ");
        mDisplayedFragment = null;
        //TODO IllegalStateException: Fragment no longer exists for key BUNDLE_SAVE_FRAGMENT_INSTANCE: index 0
        // arreglado al mover el codigo a este metodo??
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SAVE_FRAGMENT_INSTANCE)) {
            try {
                mDisplayedFragment = getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_SAVE_FRAGMENT_INSTANCE);
            } catch (IllegalStateException e) { e.printStackTrace(); }
        } else {
            onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_profile));
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //TODO toggle default icon smaller
        // mToggle.syncState(); pone el icono que le corresponde al DrawerLayout
        // Si esta cerrado este icono es ic_hamburguer.
        // Si el icono ahora es ic_up no quiero que ponga ic_hamburguer aunque este cerrado
        Drawable upIcon = ContextCompat.getDrawable(this, R.drawable.ic_up);
        Drawable icon = mToolbar.getNavigationIcon();
        if (icon == null || !upIcon.getConstantState().equals(icon.getConstantState())) {
            mToggle.syncState();
            Log.d(TAG, "syncState");
        }
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
        mToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event

        try {
            Log.d(TAG, "onOptionsItemSelected: mLoggeduser "
                    + FirebaseAuth.getInstance().getCurrentUser().getUid());
        } catch (NullPointerException e) { e.printStackTrace(); }

        if (mToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "toogle");
            return true;
        }
        Log.d(TAG, "default");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        mNavigationView.setCheckedItem(id);
        Log.d(TAG, "onNavigationItemSelected: "+item.getTitle());

        String userId = null;
        try { userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) { e.printStackTrace(); }

        if (id == R.id.nav_profile && userId != null){
            initFragment(ProfileFragment.newInstance(userId), false);
        } else if (id == R.id.nav_events) {
            initFragment(EventsFragment.newInstance(), false);
        } else if (id == R.id.nav_fields) {
            initFragment(FieldsFragment.newInstance(), false);
        } else if (id == R.id.nav_friends) {
            initFragment(FriendsFragment.newInstance(), false);
        } else if (id == R.id.nav_sign_out) {
            getSupportFragmentManager().beginTransaction().remove(mDisplayedFragment).commit();
            mDisplayedFragment = null;
            mAuth.signOut();
        } else if (id == R.id.nav_other) {
            //TODO Borrar
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void initFragment(@NonNull Fragment fragment, boolean isOnBackStack) {
        hideContent();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFrame, fragment);
        if (isOnBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void setCurrentDisplayedFragment(String title, Fragment fragment) {
        getSupportActionBar().setTitle(title);
        mDisplayedFragment = fragment;
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
                toolbarIconTransition.postDelayed(this, 19);
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
                toolbarIconTransition.postDelayed(this, 19);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        toolbarIconTransition.removeCallbacks(transitionToNav);
        toolbarIconTransition.removeCallbacks(transitionToUp);
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseDatabaseActions.detachListeners();
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
}
