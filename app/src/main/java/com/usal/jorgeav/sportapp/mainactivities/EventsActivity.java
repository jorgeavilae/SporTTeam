package com.usal.jorgeav.sportapp.mainactivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.eventdetail.simulateparticipant.SimulateParticipantContract;
import com.usal.jorgeav.sportapp.events.EventsFragment;
import com.usal.jorgeav.sportapp.events.addevent.selectfield.SelectFieldFragment;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class EventsActivity extends BaseActivity implements SelectFieldFragment.OnFieldSelected {
    private final static String TAG = EventsActivity.class.getSimpleName();

    public static final String EVENTID_PENDING_INTENT_EXTRA = "EVENTID_PENDING_INTENT_EXTRA";

    private static final String INSTANCE_NEW_EVENT_FIELD = "INSTANCE_NEW_EVENT_FIELD";
    public String newEventFieldSelected = null;

    private static final String INSTANCE_NEW_EVENT_CITY = "INSTANCE_NEW_EVENT_CITY";
    public String newEventCitySelected = null;

    @Override
    public void startMainFragment() {
        super.startMainFragment();
        String eventId = getIntent().getStringExtra(EVENTID_PENDING_INTENT_EXTRA);

        initFragment(EventsFragment.newInstance(), false);
        if (eventId != null) {
            initFragment(DetailEventFragment.newInstance(eventId), true);
        }
        mNavigationView.setCheckedItem(R.id.nav_events);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_events && super.onNavigationItemSelected(item);
    }

    @Override
    public void retrieveFieldSelected(String fieldId) {
        newEventFieldSelected = fieldId;
    }
    public void setCity(String city) {
        newEventCitySelected = city;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (newEventFieldSelected != null)
            outState.putString(INSTANCE_NEW_EVENT_FIELD, newEventFieldSelected);
        if (newEventCitySelected != null)
            outState.putString(INSTANCE_NEW_EVENT_CITY, newEventCitySelected);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NEW_EVENT_FIELD))
            newEventFieldSelected = savedInstanceState.getString(INSTANCE_NEW_EVENT_FIELD);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NEW_EVENT_CITY))
            newEventCitySelected = savedInstanceState.getString(INSTANCE_NEW_EVENT_CITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                if (mDisplayedFragment instanceof SimulateParticipantContract.View)
                    ((SimulateParticipantContract.View) mDisplayedFragment).startCropActivity(Uri.fromFile(imageFile));
            }
        });

        if (resultCode == RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {
                if (mDisplayedFragment instanceof SimulateParticipantContract.View)
                    ((SimulateParticipantContract.View) mDisplayedFragment).croppedResultOk(UCrop.getOutput(data));
            }
        } else if (resultCode == UCrop.RESULT_ERROR)
            Log.e(TAG, "onActivityResult: error ", UCrop.getError(data));
    }
}
