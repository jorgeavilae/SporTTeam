package com.usal.jorgeav.sportapp;

import android.content.res.Configuration;
import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.usal.jorgeav.sportapp.events.EventsFragment;
import com.usal.jorgeav.sportapp.fields.FieldsFragment;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainActivityContract.ActionBarChangeIcon {

    Toolbar mToolbar;
    DrawerLayout mDrawer;
    NavigationView mNavigationView;

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
        onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_profile));
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
        mToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mToggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
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

        String title = "";
        if (id == R.id.nav_profile) {
            title = getString(R.string.profile);
            initFragment(ProfileFragment.newInstance());
        } else if (id == R.id.nav_events) {
            title = getString(R.string.events);
            initFragment(EventsFragment.newInstance());
        } else if (id == R.id.nav_fields) {
            title = getString(R.string.fields);
            initFragment(FieldsFragment.newInstance());
        }

        mToolbar.setTitle(title);
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFrame, fragment);
        transaction.commit();
    }

    @Override
    public void setToolbarAsNav() {
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.ic_hamburger);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawer.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    @Override
    public void setToolbarAsUp() {
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.ic_up);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }
}
