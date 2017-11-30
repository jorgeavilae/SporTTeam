package com.usal.jorgeav.sportapp.mainactivities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.MyPlace;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.eventdetail.simulateparticipant.SimulateParticipantContract;
import com.usal.jorgeav.sportapp.eventdetail.simulateparticipant.SimulateParticipantFragment;
import com.usal.jorgeav.sportapp.events.EventsFragment;
import com.usal.jorgeav.sportapp.events.addevent.NewEventContract;
import com.usal.jorgeav.sportapp.events.addevent.NewEventFragment;
import com.usal.jorgeav.sportapp.events.addevent.SelectSportFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class EventsActivity extends BaseActivity implements SelectSportFragment.OnSportSelectedListener {
    private final static String TAG = EventsActivity.class.getSimpleName();

    public static final String EVENTID_PENDING_INTENT_EXTRA = "EVENTID_PENDING_INTENT_EXTRA";
    public static final String CREATE_NEW_EVENT_INTENT_EXTRA = "CREATE_NEW_EVENT_INTENT_EXTRA";
    public static final int REQUEST_CODE_ADDRESS = 23;

    private static final String INSTANCE_FIELD_ID_SELECTED = "INSTANCE_FIELD_ID_SELECTED";
    public String mFieldId;
    private static final String INSTANCE_ADDRESS_SELECTED = "INSTANCE_ADDRESS_SELECTED";
    public String mAddress;
    private static final String INSTANCE_CITY_SELECTED = "INSTANCE_CITY_SELECTED";
    public String mCity;
    private static final String INSTANCE_COORD_SELECTED = "INSTANCE_COORD_SELECTED";
    public LatLng mCoord;

    @Override
    public void startMainFragment() {
        super.startMainFragment();

        String eventId = getIntent().getStringExtra(EVENTID_PENDING_INTENT_EXTRA);

        initFragment(EventsFragment.newInstance(), false);
        if (eventId != null)
            initFragment(DetailEventFragment.newInstance(eventId), true);
        else if (getIntent().hasExtra(CREATE_NEW_EVENT_INTENT_EXTRA))
            initFragment(SelectSportFragment.newInstance(), true);
    }

    @Override
    public void onSportSelected(String sportId) {
        Fragment fragment = NewEventFragment.newInstance(null, sportId);
        initFragment(fragment, true);
    }

    // Start MapActivity to pick a place for new Events
    public void startMapActivityForResult(ArrayList<Field> dataList, boolean onlyField) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(MapsActivity.INTENT_EXTRA_FIELD_LIST, dataList);
        intent.putExtra(MapsActivity.INTENT_EXTRA_ONLY_FIELDS, onlyField);
        startActivityForResult(intent, REQUEST_CODE_ADDRESS);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFieldId != null)
            outState.putString(INSTANCE_FIELD_ID_SELECTED, mFieldId);
        if (mAddress != null)
            outState.putString(INSTANCE_ADDRESS_SELECTED, mAddress);
        if (mCity != null)
            outState.putString(INSTANCE_CITY_SELECTED, mCity);
        if (mCoord != null)
            outState.putParcelable(INSTANCE_COORD_SELECTED, mCoord);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FIELD_ID_SELECTED))
            mFieldId = savedInstanceState.getString(INSTANCE_FIELD_ID_SELECTED);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ADDRESS_SELECTED))
            mAddress = savedInstanceState.getString(INSTANCE_ADDRESS_SELECTED);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_CITY_SELECTED))
            mCity = savedInstanceState.getString(INSTANCE_CITY_SELECTED);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_COORD_SELECTED))
            mCoord = savedInstanceState.getParcelable(INSTANCE_COORD_SELECTED);

        if (mFieldId != null || mAddress != null || mCity != null || mCoord != null)
            if (mDisplayedFragment instanceof NewEventContract.View)
                ((NewEventContract.View) mDisplayedFragment).showEventField(mFieldId, mAddress, mCity, mCoord);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ADDRESS) {
            if (resultCode == RESULT_OK) {
                // Expect a Field where play a new Event,
                // or an address (MyPlace) to meet for non-field sports
                if (data.hasExtra(MapsActivity.FIELD_SELECTED_EXTRA)) {
                    Field field = data.getParcelableExtra(MapsActivity.FIELD_SELECTED_EXTRA);
                    mFieldId = field.getId();
                    mAddress = field.getAddress();
                    mCity = field.getCity();
                    mCoord = new LatLng(field.getCoord_latitude(), field.getCoord_longitude());
                } else if (data.hasExtra(MapsActivity.PLACE_SELECTED_EXTRA)) {
                    MyPlace myPlace = data.getParcelableExtra(MapsActivity.PLACE_SELECTED_EXTRA);
                    mFieldId = null;
                    mAddress = myPlace.getAddress();
                    mCity = myPlace.getCity();
                    mCoord = myPlace.getCoordinates();
                } else if (data.hasExtra(MapsActivity.ADD_FIELD_SELECTED_EXTRA)) {
                    Utiles.startFieldsActivityAndNewField(this);
                }
                if (mDisplayedFragment instanceof NewEventContract.View)
                    ((NewEventContract.View) mDisplayedFragment).showEventField(mFieldId, mAddress, mCity, mCoord);
            } else {
                Toast.makeText(this, R.string.toast_should_select_place, Toast.LENGTH_SHORT).show();
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
                Utiles.startCropActivity(Uri.fromFile(imageFile), EventsActivity.this);
            }
        });

        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
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
        if (requestCode == Utiles.RC_GALLERY_CAMERA_PERMISSIONS &&
                permissions[0].equals(WRITE_EXTERNAL_STORAGE) && permissions[1].equals(CAMERA)) {

            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                //Without WRITE_EXTERNAL_STORAGE it can't save cropped photo
                Toast.makeText(this, R.string.toast_need_write_permission, Toast.LENGTH_SHORT).show();
            else if (grantResults[1] == PackageManager.PERMISSION_DENIED)
                //The user can't take pictures
                EasyImage.openGallery(this, SimulateParticipantFragment.RC_PHOTO_PICKER);
            else if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                //The user can take pictures or pick an image
                EasyImage.openChooserWithGallery(this, getString(R.string.pick_photo_from), SimulateParticipantFragment.RC_PHOTO_PICKER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // This prevent from detach listeners on orientation changes and activity back transition
        shouldDetachFirebaseListener(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // This prevent from detach listeners on orientation changes and activity back transition
        shouldDetachFirebaseListener(true);
    }
}
