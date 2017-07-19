package com.usal.jorgeav.sportapp.mainactivities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.eventdetail.simulateparticipant.SimulateParticipantContract;
import com.usal.jorgeav.sportapp.eventdetail.simulateparticipant.SimulateParticipantFragment;
import com.usal.jorgeav.sportapp.events.EventsFragment;
import com.usal.jorgeav.sportapp.events.addevent.selectfield.SelectFieldFragment;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class EventsActivity extends BaseActivity implements SelectFieldFragment.OnFieldSelected {
    private final static String TAG = EventsActivity.class.getSimpleName();

    public static final String EVENTID_PENDING_INTENT_EXTRA = "EVENTID_PENDING_INTENT_EXTRA";

    private static final String INSTANCE_NEW_EVENT_FIELD = "INSTANCE_NEW_EVENT_FIELD";
    public String newEventFieldSelected = null;
    private static final String INSTANCE_NEW_EVENT_FIELD_COORD = "INSTANCE_NEW_EVENT_FIELD_COORD";
    public LatLng newEventFieldSelectedCoord = null;
    private static final String INSTANCE_NEW_EVENT_CITY_NAME = "INSTANCE_NEW_EVENT_CITY_NAME";
    public String newEventCityName = null;

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
    public void retrieveFieldSelected(String fieldId, String city, LatLng coordinates) {
        newEventFieldSelected = fieldId;
        newEventFieldSelectedCoord = coordinates;
        newEventCityName = city;
    }
    //todo Cambiar por setAddress para eventos sin pista
    public void setCity(String city) {
        newEventCityName = city;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (newEventFieldSelected != null)
            outState.putString(INSTANCE_NEW_EVENT_FIELD, newEventFieldSelected);
        if (newEventFieldSelectedCoord != null)
            outState.putParcelable(INSTANCE_NEW_EVENT_FIELD_COORD, newEventFieldSelectedCoord);
        if (newEventCityName != null)
            outState.putString(INSTANCE_NEW_EVENT_CITY_NAME, newEventCityName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NEW_EVENT_FIELD))
            newEventFieldSelected = savedInstanceState.getString(INSTANCE_NEW_EVENT_FIELD);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NEW_EVENT_FIELD_COORD))
            newEventFieldSelectedCoord = savedInstanceState.getParcelable(INSTANCE_NEW_EVENT_FIELD_COORD);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NEW_EVENT_CITY_NAME))
            newEventCityName = savedInstanceState.getString(INSTANCE_NEW_EVENT_CITY_NAME);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Results of select image and crop Activity when add a simulated User
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
                    ((SimulateParticipantContract.View) mDisplayedFragment).croppedResult(UCrop.getOutput(data));
            } else {
                // Cancel after pick image and before crop
                if (mDisplayedFragment instanceof SimulateParticipantContract.View)
                    ((SimulateParticipantContract.View) mDisplayedFragment).croppedResult(null);
            }
        } else if (resultCode == UCrop.RESULT_ERROR)
            Log.e(TAG, "onActivityResult: error ", UCrop.getError(data));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SimulateParticipantFragment.RC_PERMISSIONS &&
                permissions[0].equals(WRITE_EXTERNAL_STORAGE) && permissions[1].equals(CAMERA)) {

            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                //Without WRITE_EXTERNAL_STORAGE it can't save cropped photo
                Toast.makeText(this, "Se necesita guardar la imagen", Toast.LENGTH_SHORT).show();
            else if (grantResults[1] == PackageManager.PERMISSION_DENIED)
                //The user can't take pictures
                EasyImage.openGallery(this, SimulateParticipantFragment.RC_PHOTO_PICKER);
            else if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                //The user can take pictures or pick an image
                EasyImage.openChooserWithGallery(this, "Elegir foto de...", SimulateParticipantFragment.RC_PHOTO_PICKER);
        }
    }
}
