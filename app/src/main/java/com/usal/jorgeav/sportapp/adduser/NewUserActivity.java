package com.usal.jorgeav.sportapp.adduser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adduser.sportpractice.SportsListFragment;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.mainactivities.ActivityContracts;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class NewUserActivity extends AppCompatActivity implements
        ActivityContracts.FragmentManagement,
        SportsListFragment.OnSportsSelected {
    private final static String TAG = NewUserActivity.class.getSimpleName();
    private final static String BUNDLE_SAVE_FRAGMENT_INSTANCE = "BUNDLE_SAVE_FRAGMENT_INSTANCE";

    Fragment mDisplayedFragment;

    ArrayList<Sport> sports;

    @BindView(R.id.new_user_toolbar)
    Toolbar newUserToolbar;
    @BindView(R.id.new_user_progressbar)
    ProgressBar newUserProgressbar;
    @BindView(R.id.new_user_content)
    FrameLayout newUserContent;

    boolean sportsInitialize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        ButterKnife.bind(this);

        setSupportActionBar(newUserToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.new_user_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            newUserToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        sportsInitialize = false;
        sports = new ArrayList<>();

        startMainFragment();
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
                Utiles.startCropActivity(Uri.fromFile(imageFile), NewUserActivity.this);
            }
        });

        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                if (mDisplayedFragment instanceof NewUserContract.View)
                    ((NewUserContract.View) mDisplayedFragment).croppedResult(UCrop.getOutput(data));
            } else {
                // Cancel after pick image and before crop
                if (mDisplayedFragment instanceof NewUserContract.View)
                    ((NewUserContract.View) mDisplayedFragment).croppedResult(null);
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
                EasyImage.openGallery(this, NewUserFragment.RC_PHOTO_PICKER);
            else if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                //The user can take pictures or pick an image
                EasyImage.openChooserWithGallery(this, getString(R.string.pick_photo_from), NewUserFragment.RC_PHOTO_PICKER);
        }
    }

    @Override
    public void retrieveSportsSelected(String id, List<Sport> sportsSelected) {
        this.sports.clear();
        this.sports.addAll(sportsSelected);
        sportsInitialize = true;
        onBackPressed();
    }

    @Override
    public void startMainFragment() {
        initFragment(NewUserFragment.newInstance(), false);
    }

    @Override
    public void initFragment(@NonNull Fragment fragment, boolean isOnBackStack) {
        hideContent();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.new_user_content, fragment);
        if (isOnBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public void initFragment(@NonNull Fragment fragment, boolean isOnBackStack, String tag) {
        hideContent();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.new_user_content, fragment, tag);
        if (isOnBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void setCurrentDisplayedFragment(String title, BaseFragment fragment) {
        setActionBarTitle(title);
        mDisplayedFragment = fragment;
    }

    private void setActionBarTitle(String title) {
        if (getSupportActionBar() != null && title != null)
            getSupportActionBar().setTitle(title);
    }

    @Override
    public void showContent() {
        newUserToolbar.setVisibility(View.VISIBLE);
        newUserContent.setVisibility(View.VISIBLE);
        newUserProgressbar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideContent() {
        newUserToolbar.setVisibility(View.INVISIBLE);
        newUserContent.setVisibility(View.INVISIBLE);
        newUserProgressbar.setVisibility(View.VISIBLE);
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
                mDisplayedFragment = getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_SAVE_FRAGMENT_INSTANCE);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    /* https://stackoverflow.com/a/1109108/4235666 */
    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
