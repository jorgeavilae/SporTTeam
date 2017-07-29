package com.usal.jorgeav.sportapp.preferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.usal.jorgeav.sportapp.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        SettingsFragment settingsFragment = SettingsFragment.newInstance();

        getFragmentManager().beginTransaction()
                .replace(R.id.contentFrame, settingsFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
