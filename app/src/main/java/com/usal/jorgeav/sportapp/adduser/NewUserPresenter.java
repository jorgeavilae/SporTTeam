package com.usal.jorgeav.sportapp.adduser;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

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
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

class NewUserPresenter implements NewUserContract.Presenter {
    private static final String TAG = NewUserPresenter.class.getSimpleName();

    private NewUserContract.View mView;

    NewUserPresenter(NewUserContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void checkUserEmailExists(String email) {
        if (email != null && !TextUtils.isEmpty(email)) {
            if (isEmailValid(email))
                FirebaseActions.getUserEmailReferenceEqualTo(email)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    mView.setEmailError(R.string.error_not_unique_email);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            else
                mView.setEmailError(R.string.error_invalid_email);
        }
    }

    private boolean isEmailValid(@NonNull String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void checkUserNameExists(String name) {
        if (name != null && !TextUtils.isEmpty(name))
            FirebaseActions.getUserNameReferenceEqualTo(name)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                mView.setNameError(R.string.error_not_unique_name);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

    }

    @Override
    public void createAuthUser(final String email, String pass, final String name,
                               final Uri croppedImageFileSystemUri, final String city,
                               final LatLng coords, final Long age, final ArrayList<Sport> sportsList) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(mView.getHostActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mView.getActivityContext(), R.string.toast_login_success, Toast.LENGTH_SHORT).show();

                    //Add email to emails logged table
                    ContentValues cv = new ContentValues();
                    cv.put(SportteamContract.EmailLoggedEntry.EMAIL, email);
                    mView.getActivityContext().getContentResolver()
                            .insert(SportteamContract.EmailLoggedEntry.CONTENT_EMAIL_LOGGED_URI, cv);

                    if (croppedImageFileSystemUri != null) {
                        // Get a reference to store file at chat_photos/<FILENAME>
                        StorageReference mChatPhotosStorageReference = FirebaseStorage.getInstance().getReference()
                                .child(FirebaseDBContract.Storage.PROFILE_PICTURES);
                        StorageReference photoRef = mChatPhotosStorageReference
                                .child(croppedImageFileSystemUri.getLastPathSegment());

                        // Create the file metadata
                        StorageMetadata metadata = new StorageMetadata.Builder()
                                .setContentType("image/jpeg")
                                .build();

                        // Upload file and metadata to the path 'images/name.jpg' into Firebase Storage
                        UploadTask uploadTask = photoRef.putFile(croppedImageFileSystemUri, metadata);

                        // Listen for errors, and completion of the upload.
                        uploadTask.addOnFailureListener(new OnFailureListener() {
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
                                    final Uri downloadUrl = metadata.getDownloadUrl();
                                    if (downloadUrl != null)
                                        setUserDataAndFinish(downloadUrl, name, city, coords, age, sportsList);
                                    else setUserDataAndFinish(null, name, city, coords, age, sportsList);
                                } else setUserDataAndFinish(null, name, city, coords, age, sportsList);
                            }
                        });
                    } else {
                        setUserDataAndFinish(null, name, city, coords, age, sportsList);
                    }
                } else {
                    mView.showContent();
                    Toast.makeText(mView.getActivityContext(), R.string.toast_login_failure, Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(mView.getHostActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mView.getActivityContext(), R.string.toast_create_user_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUserDataAndFinish(Uri photoUri, String name, String city, LatLng coords,
                                      Long age, ArrayList<Sport> sportsList) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser == null) return;

        UserProfileChangeRequest.Builder profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name);
        if(photoUri != null) profileUpdates = profileUpdates.setPhotoUri(photoUri);
        fUser.updateProfile(profileUpdates.build());

        fUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Verification email sent.");
                        }
                    }
                });

        //TODO validate data
        String photoUriStr = "";
        if (photoUri != null) photoUriStr = photoUri.toString();
        User user = new User(fUser.getUid(), fUser.getEmail(),
                name, city, coords.latitude, coords.longitude, age, photoUriStr,
                sportsArrayToHashMap(sportsList));
        FirebaseActions.addUser(user);

        // Return to LoginActivity
        mView.getHostActivity().setResult(RESULT_OK);
        mView.getHostActivity().finish();
    }

    private Map<String,Double> sportsArrayToHashMap(List<Sport> sports) {
        HashMap<String, Double> result = new HashMap<>();
        for (Sport s : sports)
            result.put(s.getSportID(), (double) s.getPunctuation());
        return result;
    }
}
