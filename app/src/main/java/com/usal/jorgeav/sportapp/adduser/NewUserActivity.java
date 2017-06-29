package com.usal.jorgeav.sportapp.adduser;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.usal.jorgeav.sportapp.GlideApp;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adduser.sportpractice.SportsListFragment;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.mainactivities.ActivityContracts;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class NewUserActivity extends AppCompatActivity implements ActivityContracts.FragmentManagement,
        SportsListFragment.OnSportsSelected {
    private final static String TAG = NewUserActivity.class.getSimpleName();
    private final static String BUNDLE_SAVE_FRAGMENT_INSTANCE = "BUNDLE_SAVE_FRAGMENT_INSTANCE";
    private static final int RC_PERMISSIONS = 3;
    Fragment mDisplayedFragment;
    private static final int RC_PHOTO_PICKER = 2;
    Uri selectedImageUri;
    Uri croppedImageUri;

    @BindView(R.id.new_user_toolbar)
    Toolbar newUseerToolbar;
    @BindView(R.id.new_user_progressbar)
    ProgressBar newUserProgressbar;
    @BindView(R.id.new_user_content)
    FrameLayout newUserContent;
    @BindView(R.id.new_user_email)
    EditText newUserEmail;
    @BindView(R.id.new_user_password)
    EditText newUserPassword;
    @BindView(R.id.new_user_name)
    EditText newUserName;
    @BindView(R.id.new_user_age)
    EditText newUserAge;
    @BindView(R.id.new_user_photo)
    ImageView newUserPhoto;
    @BindView(R.id.new_user_photo_button)
    Button newUserPhotoButton;
    @BindView(R.id.new_user_city)
    EditText newUserCity;
    @BindView(R.id.new_user_add_sport_button)
    Button newUserAddSportButton;

    ArrayList<Sport> sports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        ButterKnife.bind(this);

        setSupportActionBar(newUseerToolbar);
        newUseerToolbar.setTitle("Add User");
        newUseerToolbar.setNavigationIcon(R.drawable.ic_action_close);
        newUseerToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sports = new ArrayList<Sport>();

        final SportsListFragment slf = SportsListFragment.newInstance(null);
        newUserAddSportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFragment(slf, true);
            }
        });

        newUserEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                    checkUserEmailExists(newUserEmail.getText().toString());
            }
        });
        newUserPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                    if (newUserPassword.getText().toString().length() < 6)
                        newUserPassword.setError("Necesita al menos 6 caracteres");
            }
        });
        newUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                    checkUserNameExists(newUserName.getText().toString());
            }
        });

        newUserPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.configuration(NewUserActivity.this)
                        .setImagesFolderName(Environment.DIRECTORY_PICTURES)
                        .saveInAppExternalFilesDir();
                if (isStorageCameraPermissionGranted())
                    EasyImage.openChooserWithGallery(NewUserActivity.this, "Elegir foto de...", RC_PHOTO_PICKER);
            }
        });

        showContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ok, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_ok) {
            Log.d(TAG, "onOptionsItemSelected: Ok");
            //Check emailEditText and PassEditText
            if (!TextUtils.isEmpty(newUserEmail.getText())
                    && TextUtils.isEmpty(newUserEmail.getError())
                    && !TextUtils.isEmpty(newUserPassword.getText())
                    && TextUtils.isEmpty(newUserPassword.getError())
                    && !TextUtils.isEmpty(newUserName.getText())
                    && TextUtils.isEmpty(newUserName.getError())
                    && !TextUtils.isEmpty(newUserAge.getText())
                    && croppedImageUri != null
                    && !TextUtils.isEmpty(newUserCity.getText())
                    && sports.size() > 0) {
                hideContent();
                createAuthUser(newUserEmail.getText().toString(), newUserPassword.getText().toString());
            } else
                Toast.makeText(getApplicationContext(), "Error: algun campo vacio", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
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
                selectedImageUri = Uri.fromFile(imageFile);
                startCropActivity();
            }
        });

        if (resultCode == RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {
                croppedImageUri = UCrop.getOutput(data);
                Log.d(TAG, "onActivityResult: "+croppedImageUri);
                GlideApp.with(this)
                        .load(croppedImageUri)
                        .placeholder(R.drawable.profile_picture_placeholder)
                        .centerCrop()
                        .into(newUserPhoto);

            }
        } else if (resultCode == UCrop.RESULT_ERROR)
            Log.e(TAG, "onActivityResult: error ", UCrop.getError(data));
    }

    private void startCropActivity() {
        long millis = System.currentTimeMillis();
        croppedImageUri = getAlbumStorageDir(selectedImageUri.getLastPathSegment() + "_cropped" + millis);
        UCrop.of(selectedImageUri, croppedImageUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(512, 512)
                    .start(this);
    }

    private Uri getAlbumStorageDir(@NonNull String path) {
        // Get the directory for the user's public pictures directory.
        File f = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

    /* Checks if external storage is available for read and write */
    private  boolean isStorageCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permissions are granted");
                return true;
            } else {
                Log.v(TAG,"Permissions are revoked");
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, RC_PERMISSIONS);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permissions are granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSIONS &&
                permissions[0].equals(WRITE_EXTERNAL_STORAGE) && permissions[1].equals(CAMERA)) {

            if(grantResults[0] == PackageManager.PERMISSION_DENIED)
                //Without WRITE_EXTERNAL_STORAGE it can't save cropped photo
                Toast.makeText(this, "Se necesita guardar la imagen", Toast.LENGTH_SHORT).show();
            else if (grantResults[1] == PackageManager.PERMISSION_DENIED)
                //The user can't take pictures
                EasyImage.openGallery(this, RC_PHOTO_PICKER);
            else if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                //The user can take pictures or pick an image
                EasyImage.openChooserWithGallery(this, "Elegir foto de...", RC_PHOTO_PICKER);
        }
    }

    private void checkUserEmailExists(String email) {
        FirebaseActions.getUserEmailReferenceEqualTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            newUserEmail.setError("Email already exist");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }
    private void checkUserNameExists(String name) {
        FirebaseActions.getUserNameReferenceEqualTo(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            newUserName.setError("Name already exist");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }

    private void createAuthUser(String email, String pass) {
        final OnCompleteListener<AuthResult> onCompleteListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Log in is Successful", Toast.LENGTH_SHORT).show();

                    // Get a reference to store file at chat_photos/<FILENAME>
                    StorageReference mChatPhotosStorageReference = FirebaseStorage.getInstance().getReference()
                            .child(FirebaseDBContract.Storage.PROFILE_PICTURES);
                    StorageReference photoRef = mChatPhotosStorageReference.child(croppedImageUri.getLastPathSegment());

                    // Upload file to Firebase Storage
                    // Create the file metadata
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType("image/jpeg")
                            .build();

                    // Upload file and metadata to the path 'images/mountains.jpg'
                    UploadTask uploadTask = photoRef.putFile(croppedImageUri, metadata);

                    // Listen for state changes, errors, and completion of the upload.
                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            /* https://stackoverflow.com/a/42616488/4235666 */
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            Log.i(TAG, "createAuthUser:putFile:onProgress: Upload is " + progress + "% done");
                        }
                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.i(TAG, "createAuthUser:putFile:onPaused: Upload is paused");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Log.e(TAG, "createAuthUser:putFile:onFailure: ", exception);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Handle successful uploads on complete
                            /* https://stackoverflow.com/a/42616488/4235666 */
                            @SuppressWarnings("VisibleForTests")
                            StorageMetadata metadata = taskSnapshot.getMetadata();
                            if (metadata != null) {
                                Uri downloadUrl = metadata.getDownloadUrl();
                                User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                        newUserEmail.getText().toString(),
                                        newUserName.getText().toString(),
                                        newUserCity.getText().toString(),
                                        Integer.parseInt(newUserAge.getText().toString()),
                                        downloadUrl.toString(),
                                        sports);
                                FirebaseActions.addUser(user);

                                setResult(RESULT_OK); finish();
                            }
                        }
                    });
                } else {
                    showContent();
                    Toast.makeText(getApplicationContext(), "Error Login in", Toast.LENGTH_SHORT).show();
                }
            }
        };
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, onCompleteListener);
    }

    @Override
    public void retrieveSportsSelected(List<Sport> sportsSelected) {
        this.sports.clear();
        this.sports.addAll(sportsSelected);
    }

    @Override
    public void startMainFragment() {

    }

    @Override
    public void initFragment(@NotNull Fragment fragment, boolean isOnBackStack) {
        hideContent();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.new_user_content, fragment);
        if (isOnBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void setCurrentDisplayedFragment(String title, Fragment fragment) {
        getSupportActionBar().setTitle(title);
        mDisplayedFragment = fragment;
    }

    @Override
    public void showContent() {
        newUserContent.setVisibility(View.VISIBLE);
        newUserProgressbar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideContent() {
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
            } catch (IllegalStateException e) { e.printStackTrace(); }
        }
    }
}
