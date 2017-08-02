package com.usal.jorgeav.sportapp.adduser;

import android.content.ContentValues;
import android.content.Context;
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
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.PlaceAutocompleteAdapter;
import com.usal.jorgeav.sportapp.adduser.sportpractice.SportsListFragment;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.mainactivities.ActivityContracts;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final String INSTANCE_NEW_USER_CITY_NAME = "INSTANCE_NEW_USER_CITY_NAME";
    private static final String INSTANCE_NEW_USER_CITY_COORD = "INSTANCE_NEW_USER_CITY_COORD";
    private static final int RC_PERMISSIONS = 3;
    private static final int RC_PHOTO_PICKER = 2;

    Fragment mDisplayedFragment;
    Uri selectedImageUri;
    Uri croppedImageUri;
    String newUserCitySelectedName = null;
    LatLng newUserCitySelectedCoord = null;
    // Static prevent double initialization with same ID
    static GoogleApiClient mGoogleApiClient;
    PlaceAutocompleteAdapter mAdapter;

    @BindView(R.id.new_user_toolbar)
    Toolbar newUserToolbar;
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
    AutoCompleteTextView newUserAutocompleteCity;

    boolean sportsInitialize;
    ArrayList<Sport> sports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGoogleApiClient == null)
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.e(TAG, "onConnectionFailed: Google Api Client is not connected");
                        }
                    })
                    .build();
        else mGoogleApiClient.connect();

        setContentView(R.layout.activity_new_user);
        ButterKnife.bind(this);

        setSupportActionBar(newUserToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add User");
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

        checkUserEmailExists(newUserEmail.getText().toString());
        checkUserNameExists(newUserName.getText().toString());
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
        ImageButton visibleButton = (ImageButton) findViewById(R.id.new_user_visible_pass);
        visibleButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    newUserPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    newUserPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                return true;
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
                hideSoftKeyboard();
                EasyImage.configuration(NewUserActivity.this)
                        .setImagesFolderName(Environment.DIRECTORY_PICTURES)
                        .saveInAppExternalFilesDir();
                if (isStorageCameraPermissionGranted())
                    EasyImage.openChooserWithGallery(NewUserActivity.this, "Elegir foto de...", RC_PHOTO_PICKER);
            }
        });

        setAutocompleteTextView();

        showContent();
    }

    private void setAutocompleteTextView() {
        // Set up the adapter that will retrieve suggestions from
        // the Places Geo Data API that cover Spain
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                /*https://developers.google.com/android/reference/com/google/android/gms/location/places/AutocompleteFilter.Builder.html#setCountry(java.lang.String)*/
                .setCountry("ES"/*https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#ES*/)
                .build();

        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null, typeFilter);
        newUserAutocompleteCity.setAdapter(mAdapter);

        newUserAutocompleteCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newUserCitySelectedName = null;
                newUserCitySelectedCoord = null;
            }
        });

        newUserAutocompleteCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                /*
                 Retrieve the place ID of the selected item from the Adapter.
                 The adapter stores each Place suggestion in a AutocompletePrediction from which we
                 read the place ID and title.
                  */
                AutocompletePrediction item = mAdapter.getItem(position);
                if (item != null) {
                    Log.i(TAG, "Autocomplete item selected: " + item.getPlaceId());
                    Places.GeoDataApi.getPlaceById(mGoogleApiClient, item.getPlaceId())
                            .setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(@NonNull PlaceBuffer places) {
                                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                        Place myPlace = places.get(0);
                                        newUserCitySelectedName = myPlace.getName().toString();
                                        newUserCitySelectedCoord = myPlace.getLatLng();
                                        Log.i(TAG, "Place found: Name - " + myPlace.getName()
                                                + " LatLng - " + myPlace.getLatLng());
                                    } else {
                                        Log.e(TAG, "Place not found");
                                    }
                                    places.release();
                                }
                            });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ok, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        // Check if there is another fragment with onOptionsItemSelected implemented
        /* https://stackoverflow.com/a/17767406/4235666 */
        if (mDisplayedFragment != null) return false;

        if (item.getItemId() == R.id.action_ok) {
            Log.d(TAG, "onOptionsItemSelected: Ok");
            hideSoftKeyboard();

            if (!TextUtils.isEmpty(newUserEmail.getText())
                    && TextUtils.isEmpty(newUserEmail.getError())
                    && !TextUtils.isEmpty(newUserPassword.getText())
                    && TextUtils.isEmpty(newUserPassword.getError())
                    && !TextUtils.isEmpty(newUserName.getText())
                    && TextUtils.isEmpty(newUserName.getError())
                    && !TextUtils.isEmpty(newUserAge.getText())
                    && newUserCitySelectedName != null
                    && newUserCitySelectedCoord != null) {
                SportsListFragment slf = SportsListFragment.newInstance("", sports);
                initFragment(slf, true);
            } else {
                Toast.makeText(getApplicationContext(), "Error: algun campo vacio", Toast.LENGTH_SHORT).show();
            }
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

        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                croppedImageUri = UCrop.getOutput(data);
                Glide.with(this)
                        .load(croppedImageUri)
                        .placeholder(R.drawable.profile_picture_placeholder)
                        .centerCrop()
                        .into(newUserPhoto);

            } else {
                // Cancel after pick image and before crop
                croppedImageUri = null;
            }
        } else if (resultCode == UCrop.RESULT_ERROR)
            Log.e(TAG, "onActivityResult: error ", UCrop.getError(data));
    }

    private void startCropActivity() {
        long millis = System.currentTimeMillis();
        if (selectedImageUri.getLastPathSegment().contains("."))
            croppedImageUri = getAlbumStorageDir(selectedImageUri.getLastPathSegment().replace(".", "_cropped" + millis + "."));
        else
            croppedImageUri = getAlbumStorageDir(selectedImageUri.getLastPathSegment() + "_cropped" + millis);
        UCrop.of(selectedImageUri, croppedImageUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(512, 512)
                .start(this); // Start Activity with requestCode UCrop.REQUEST_CROP
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
    private boolean isStorageCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permissions are granted");
                return true;
            } else {
                Log.v(TAG, "Permissions are revoked");
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, RC_PERMISSIONS);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permissions are granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSIONS &&
                permissions[0].equals(WRITE_EXTERNAL_STORAGE) && permissions[1].equals(CAMERA)) {

            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
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
        if (email != null && !TextUtils.isEmpty(email) && isEmailValid(email))
            FirebaseActions.getUserEmailReferenceEqualTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                newUserEmail.setError("Email already exist");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
    }

    private boolean isEmailValid(@NonNull String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void checkUserNameExists(String name) {
        if (name != null && !TextUtils.isEmpty(name))
            FirebaseActions.getUserNameReferenceEqualTo(name)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                newUserName.setError("Name already exist");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
    }

    private void createAuthUser(final String email, String pass) {
        final OnCompleteListener<AuthResult> onCompleteListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Log in is Successful", Toast.LENGTH_SHORT).show();

                    //Add email to emails logged table
                    ContentValues cv = new ContentValues();
                    cv.put(SportteamContract.EmailLoggedEntry.EMAIL, email);
                    getContentResolver().insert(SportteamContract.EmailLoggedEntry.CONTENT_EMAIL_LOGGED_URI, cv);

                    if (croppedImageUri != null) {
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
                                if (metadata == null) return;
                                final Uri downloadUrl = metadata.getDownloadUrl();
                                if (downloadUrl == null) return;

                                setUserDataAndFinish(downloadUrl);
                            }
                        });
                    } else {
                        setUserDataAndFinish(null);
                    }
                } else {
                    showContent();
                    Toast.makeText(getApplicationContext(), "Error Login in", Toast.LENGTH_SHORT).show();
                }
            }
        };
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, onCompleteListener);
    }

    private void setUserDataAndFinish(Uri photoUri) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser == null) return;

        UserProfileChangeRequest.Builder profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUserName.getText().toString());
        if(photoUri != null) profileUpdates = profileUpdates.setPhotoUri(photoUri);
        fUser.updateProfile(profileUpdates.build());

        fUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });

        String photoUriStr = "";
        if (photoUri != null) photoUriStr = photoUri.toString();
        User user = new User(fUser.getUid(), fUser.getEmail(),
                newUserName.getText().toString(), newUserCitySelectedName,
                newUserCitySelectedCoord.latitude, newUserCitySelectedCoord.longitude,
                Long.parseLong(newUserAge.getText().toString()), photoUriStr,
                sportsArrayToHashMap(sports));
        FirebaseActions.addUser(user);

        // Return to LoginActivity
        setResult(RESULT_OK);
        finish();
    }


    private Map<String,Double> sportsArrayToHashMap(List<Sport> sports) {
        HashMap<String, Double> result = new HashMap<>();
        for (Sport s : sports)
            result.put(s.getName(), (double) s.getPunctuation());
        return result;
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
    public void setCurrentDisplayedFragment(String title, BaseFragment fragment) {
        setActionBarTitle(title);
        mDisplayedFragment = fragment;
    }

    public void setActionBarTitle(String title) {
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
        if (newUserCitySelectedName != null)
            outState.putString(INSTANCE_NEW_USER_CITY_NAME, newUserCitySelectedName);
        if (newUserCitySelectedCoord != null)
            outState.putParcelable(INSTANCE_NEW_USER_CITY_COORD, newUserCitySelectedCoord);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NEW_USER_CITY_NAME))
            newUserCitySelectedName = savedInstanceState.getString(INSTANCE_NEW_USER_CITY_NAME);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NEW_USER_CITY_COORD))
            newUserCitySelectedCoord = savedInstanceState.getParcelable(INSTANCE_NEW_USER_CITY_COORD);

        mDisplayedFragment = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SAVE_FRAGMENT_INSTANCE)) {
            try {
                mDisplayedFragment = getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_SAVE_FRAGMENT_INSTANCE);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        newUserToolbar.setTitle("Add User");
        hideSoftKeyboard();

        if (this.sports != null && sportsInitialize) {
            hideContent();
            createAuthUser(newUserEmail.getText().toString(), newUserPassword.getText().toString());
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
