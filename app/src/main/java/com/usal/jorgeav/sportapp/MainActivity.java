package com.usal.jorgeav.sportapp;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.usal.jorgeav.sportapp.events.EventsFragment;
import com.usal.jorgeav.sportapp.fields.FieldsFragment;
import com.usal.jorgeav.sportapp.network.FirebaseDatabaseActions;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainActivityContract.ActionBarChangeIcon,
        MainActivityContract.FragmentManagement {

    private final static String BUNDLE_SAVE_FRAGMENT_INSTANCE = "BUNDLE_SAVE_FRAGMENT_INSTANCE";

    Toolbar mToolbar;
    DrawerLayout mDrawer;
    NavigationView mNavigationView;
    Fragment mDisplayedFragment;

    ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mToggle);


        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mDisplayedFragment = null;
        if (savedInstanceState != null) {
            mDisplayedFragment = getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_SAVE_FRAGMENT_INSTANCE);

        }
        if (mDisplayedFragment == null) {
            onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_profile));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, BUNDLE_SAVE_FRAGMENT_INSTANCE, mDisplayedFragment);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //TODO toggle default icon smaller than mine
        Drawable upIcon = ContextCompat.getDrawable(this, R.drawable.ic_up);
        Drawable icon = mToolbar.getNavigationIcon();
        if (icon == null || !upIcon.getConstantState().equals(icon.getConstantState())) {
            mToggle.syncState();
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
        return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        mNavigationView.setCheckedItem(id);

        if (id == R.id.nav_profile) {
            initFragment(ProfileFragment.newInstance(), false);
        } else if (id == R.id.nav_events) {
            initFragment(EventsFragment.newInstance(), false);
        } else if (id == R.id.nav_fields) {
            initFragment(FieldsFragment.newInstance(), false);
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void initFragment(Fragment fragment, boolean isOnBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFrame, fragment);
        if (isOnBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void setCurrentDisplayedFragment(String title, Fragment fragment) {
        //TODO a veces no se pone el titulo
        mToolbar.setTitle(title);
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
        FirebaseDatabaseActions.loadFields(this); //Intalaciones: una vez
        //FirebaseDatabaseActions; //Mi perfil: cuando cambie
        FirebaseDatabaseActions.loadEvents(this); //Eventos de mi ciudad: cuando cambie (para la alarma)
        super.onResume();
    }

    @Override
    protected void onPause() {
        toolbarIconTransition.removeCallbacks(transitionToNav);
        toolbarIconTransition.removeCallbacks(transitionToUp);
        super.onPause();
    }
}
