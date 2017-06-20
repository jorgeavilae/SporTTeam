package com.usal.jorgeav.sportapp.adduser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.usal.jorgeav.sportapp.ActivityContracts;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adduser.sportpractice.SportsListFragment;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.network.FirebaseActions;
import com.usal.jorgeav.sportapp.network.FirebaseDBContract;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewUserActivity extends AppCompatActivity implements ActivityContracts.FragmentManagement, SportsListFragment.OnSportsSelected{
    private final static String TAG = NewUserActivity.class.getSimpleName();
    private final static String BUNDLE_SAVE_FRAGMENT_INSTANCE = "BUNDLE_SAVE_FRAGMENT_INSTANCE";
    Fragment mDisplayedFragment;
    private static final int RC_PHOTO_PICKER = 2;

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
    EditText newUserPhoto;
    @BindView(R.id.new_user_photo_button)
    Button newUserPhotoButton;
    @BindView(R.id.new_user_city)
    EditText newUserCity;
    @BindView(R.id.new_user_add_sport_button)
    Button newUserAddSportButton;

    @BindView(R.id.new_user_create_button)
    Button newUserCreateButton;

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

        final SportsListFragment slf = SportsListFragment.newInstance(null, this);
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
                // TODO: 20/06/2017 pick fotos
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        newUserCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check emailEditText and PassEditText
                if (!TextUtils.isEmpty(newUserEmail.getText())
                        && TextUtils.isEmpty(newUserEmail.getError())
                        && !TextUtils.isEmpty(newUserPassword.getText())
                        && TextUtils.isEmpty(newUserPassword.getError())
                        && !TextUtils.isEmpty(newUserName.getText())
                        && TextUtils.isEmpty(newUserName.getError())
                        && !TextUtils.isEmpty(newUserAge.getText())
                        && !TextUtils.isEmpty(newUserPhoto.getText())
                        && !TextUtils.isEmpty(newUserCity.getText())
                        && sports.size() > 0) {
                    hideContent();
                    createAuthUser(newUserEmail.getText().toString(), newUserPassword.getText().toString());
                } else
                    Toast.makeText(getApplicationContext(), "Error: algun campo vacio", Toast.LENGTH_SHORT).show();
            }
        });
        showContent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            newUserPhoto.setText(selectedImageUri.toString());
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

                    Uri selectedImageUri = Uri.parse(newUserPhoto.getText().toString());
                    // Get a reference to store file at chat_photos/<FILENAME>
                    StorageReference mChatPhotosStorageReference = FirebaseStorage.getInstance().getReference()
                            .child(FirebaseDBContract.Storage.PROFILE_PICTURES);
                    StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

                    // Upload file to Firebase Storage
                    // Create the file metadata
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType("image/jpeg")
                            .build();

                    // Upload file and metadata to the path 'images/mountains.jpg'
                    UploadTask uploadTask = photoRef.putFile(selectedImageUri, metadata);

                    // Listen for state changes, errors, and completion of the upload.
                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            /* https://stackoverflow.com/a/42616488/4235666 */
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            System.out.println("Upload is " + progress + "% done");
                        }
                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Upload is paused");
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
