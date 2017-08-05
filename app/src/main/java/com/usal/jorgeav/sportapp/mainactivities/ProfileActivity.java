package com.usal.jorgeav.sportapp.mainactivities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adduser.sportpractice.SportsListFragment;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.profile.ProfileContract;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Jorge Avila on 26/06/2017.
 */

public class ProfileActivity extends BaseActivity implements SportsListFragment.OnSportsSelected {
    public static final String TAG = ProfileActivity.class.getSimpleName();

    @Override
    public void startMainFragment() {
        super.startMainFragment();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();

        initFragment(ProfileFragment.newInstance(myUserID), false);
        mNavigationView.setCheckedItem(R.id.nav_profile);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_profile && super.onNavigationItemSelected(item);
    }

    @Override
    public void retrieveSportsSelected(String myUserID, List<Sport> sportsSelected) {
        if (myUserID != null && !TextUtils.isEmpty(myUserID)) {
            HashMap<String, Float> sportsMap = new HashMap<>();
            if (sportsSelected != null)
                for (Sport sport : sportsSelected)
                    sportsMap.put(sport.getSportID(), sport.getPunctuation());
            FirebaseActions.updateSports(myUserID, sportsMap);
        }
        onBackPressed();
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
                if (mDisplayedFragment instanceof ProfileContract.View)
                    ((ProfileContract.View) mDisplayedFragment).startCropActivity(Uri.fromFile(imageFile));
            }
        });

        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                if (mDisplayedFragment instanceof ProfileContract.View)
                    ((ProfileContract.View) mDisplayedFragment).croppedResult(UCrop.getOutput(data));
            } else {
                // Cancel after pick image and before crop
                if (mDisplayedFragment instanceof ProfileContract.View)
                    ((ProfileContract.View) mDisplayedFragment).croppedResult(null);
            }
        } else if (resultCode == UCrop.RESULT_ERROR)
            Log.e(TAG, "onActivityResult: error ", UCrop.getError(data));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ProfileFragment.RC_PERMISSIONS &&
                permissions[0].equals(WRITE_EXTERNAL_STORAGE) && permissions[1].equals(CAMERA)) {

            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                //Without WRITE_EXTERNAL_STORAGE it can't save cropped photo
                Toast.makeText(this, "Se necesita guardar la imagen", Toast.LENGTH_SHORT).show();
            else if (grantResults[1] == PackageManager.PERMISSION_DENIED)
                //The user can't take pictures
                EasyImage.openGallery(this, ProfileFragment.RC_PHOTO_PICKER);
            else if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                //The user can take pictures or pick an image
                EasyImage.openChooserWithGallery(this, "Elegir foto de...", ProfileFragment.RC_PHOTO_PICKER);
        }
    }
}
