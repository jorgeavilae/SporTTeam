package com.usal.jorgeav.sportapp.eventdetail.simulateparticipant;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.GlideApp;
import com.usal.jorgeav.sportapp.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Jorge Avila on 12/07/2017.
 */

public class SimulateParticipantFragment extends BaseFragment implements SimulateParticipantContract.View {
    public static final String TAG = SimulateParticipantFragment.class.getSimpleName();
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";
    private static final int RC_PERMISSIONS = 3;
    private static final int RC_PHOTO_PICKER = 2;


    SimulateParticipantContract.Presenter mPresenter;
    @BindView(R.id.new_simulated_user_photo)
    ImageView simulatedUserPhoto;
    @BindView(R.id.new_simulated_user_photo_button)
    Button simulatedUserPhotoButton;
    Uri photoUri = null;
    @BindView(R.id.new_simulated_user_name)
    EditText simulatedUserName;
    @BindView(R.id.new_simulated_user_age)
    EditText simulatedUserAge;

    public SimulateParticipantFragment() {
    }

    public static SimulateParticipantFragment newInstance(@NonNull String eventId) {
        SimulateParticipantFragment fragment = new SimulateParticipantFragment();
        Bundle b = new Bundle();
        b.putString(BUNDLE_EVENT_ID, eventId);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPresenter = new SimulateParticipantPresenter(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_ok, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        hideSoftKeyboard();
        if (item.getItemId() == R.id.action_ok) {
            Log.d(TAG, "onOptionsItemSelected: Ok");

            String eventId = "";
            if (getArguments() != null && getArguments().containsKey(BUNDLE_EVENT_ID))
                eventId = getArguments().getString(BUNDLE_EVENT_ID);

            mPresenter.addSimulatedParticipant(
                    eventId,
                    simulatedUserName.getText().toString(),
                    photoUri,
                    simulatedUserAge.getText().toString());
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_simulated_user, container, false);
        ButterKnife.bind(this, root);


        simulatedUserPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.configuration(getActivity())
                        .setImagesFolderName(Environment.DIRECTORY_PICTURES)
                        .saveInAppExternalFilesDir();
                if (isStorageCameraPermissionGranted())
                    EasyImage.openChooserWithGallery(getActivity(), "Elegir foto de...", RC_PHOTO_PICKER);
            }
        });
        return root;
    }

    /* Checks if external storage is available for read and write */
    private  boolean isStorageCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && getActivity().checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permissions are granted");
                return true;
            } else {
                Log.v(TAG,"Permissions are revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, RC_PERMISSIONS);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permissions are granted");
            return true;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment("Simular participante", this);
        mActionBarIconManagementListener.setToolbarAsUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentManagementListener.showContent();
    }

    @Override
    public void startCropActivity(Uri imageFileUri) {
        long millis = System.currentTimeMillis();
        // Uri to store cropped photo in filesystem
        if (imageFileUri.getLastPathSegment().contains("."))
            photoUri = getAlbumStorageDir(imageFileUri.getLastPathSegment().replace(".","_cropped" + millis + "."));
        else
            photoUri = getAlbumStorageDir(imageFileUri.getLastPathSegment() + "_cropped" + millis);
        UCrop.of(imageFileUri, photoUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(512, 512)
                .start(getActivity());
    }

    private Uri getAlbumStorageDir(@NonNull String path) {
        // Get the directory for the user's public pictures directory.
        File f = getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Uri uri = Uri.fromFile(f).buildUpon().appendPath(path).build();
        File file = new File(uri.getPath());
        if (!file.exists()) {
            try {
                if (!file.createNewFile())
                    Log.e(TAG, "getAlbumStorageDir: file not created");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Uri.fromFile(file);
    }


    @Override
    public void croppedResultOk(Uri photoCroppedUri) {
        // Uri from cropped photo as result of UCrop
        photoUri = photoCroppedUri;
        GlideApp.with(this)
                .load(photoUri)
                .placeholder(R.drawable.profile_picture_placeholder)
                .centerCrop()
                .into(simulatedUserPhoto);
    }


}
