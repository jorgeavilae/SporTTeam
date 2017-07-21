package com.usal.jorgeav.sportapp.mainactivities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.MyPlace;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.eventdetail.simulateparticipant.SimulateParticipantContract;
import com.usal.jorgeav.sportapp.eventdetail.simulateparticipant.SimulateParticipantFragment;
import com.usal.jorgeav.sportapp.events.EventsFragment;
import com.usal.jorgeav.sportapp.events.addevent.NewEventContract;
import com.usal.jorgeav.sportapp.fields.addfield.NewFieldContract;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class EventsActivity extends BaseActivity {
    private final static String TAG = EventsActivity.class.getSimpleName();

    public static final String EVENTID_PENDING_INTENT_EXTRA = "EVENTID_PENDING_INTENT_EXTRA";

    public static final String INTENT_EXTRA_FIELD_LIST = "INTENT_EXTRA_FIELD_LIST";
    public static final int REQUEST_CODE_ADDRESS = 23;
    private static final String INSTANCE_PLACE_SELECTED = "INSTANCE_PLACE_SELECTED";
    public MyPlace mPlaceSelected;

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

    public void startMapActivityForResult(ArrayList<Field> dataList) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(INTENT_EXTRA_FIELD_LIST, dataList);
        startActivityForResult(intent, REQUEST_CODE_ADDRESS);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPlaceSelected != null)
            outState.putParcelable(INSTANCE_PLACE_SELECTED, mPlaceSelected);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_PLACE_SELECTED)) {
            mPlaceSelected = savedInstanceState.getParcelable(INSTANCE_PLACE_SELECTED);

            if (mPlaceSelected != null && mDisplayedFragment instanceof NewFieldContract.View)
                ((NewFieldContract.View) mDisplayedFragment).showFieldPlace(
                        mPlaceSelected.getAddress(), //TODO sustituir por fieldId
                        mPlaceSelected.getShortNameLocality(),
                        mPlaceSelected.getCoordinates());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ADDRESS) {
            if (resultCode == RESULT_OK) {
                mPlaceSelected = data.getParcelableExtra(MapsActivity.PLACE_SELECTED_EXTRA);
                if (mDisplayedFragment instanceof NewFieldContract.View)
                    ((NewEventContract.View) mDisplayedFragment).showEventField(
                            mPlaceSelected.getAddress(), //TODO sustituir por fieldId
                            mPlaceSelected.getShortNameLocality(),
                            mPlaceSelected.getCoordinates());
            } else {
                Toast.makeText(this, "You should select a place", Toast.LENGTH_SHORT).show();
            }
            return;
        }

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
