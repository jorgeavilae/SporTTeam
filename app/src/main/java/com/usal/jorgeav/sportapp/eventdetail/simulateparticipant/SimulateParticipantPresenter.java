package com.usal.jorgeav.sportapp.eventdetail.simulateparticipant;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.utils.Utiles;

/**
 * Created by Jorge Avila on 12/07/2017.
 */

public class SimulateParticipantPresenter implements SimulateParticipantContract.Presenter {
    public static final String TAG = SimulateParticipantPresenter.class.getSimpleName();
    SimulateParticipantContract.View mView;
    String mEventId;
    String mName;
    String mAge;

    public SimulateParticipantPresenter(SimulateParticipantContract.View view){
        this.mView = view;
        String mEventId = "";
        String mName = "";
        String mAge = "";
    }

    @Override
    public void addSimulatedParticipant(String eventId, String name, Uri photo, String age) {
        if(true) {//Validate arguments
            mEventId = eventId;
            mName = name;
            mAge = age;
            if (photo != null)
                storePhotoOnFirebase(photo);
            else {
                storeSimulatedParticipantInFirebase(null);
            }
        }

//        ((BaseActivity)mView.getActivityContext()).onBackPressed();
    }

    private void storePhotoOnFirebase(Uri photo) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference photoRef = storage.getReferenceFromUrl(Utiles.getFirebaseStorageRootReference())
                .child(FirebaseDBContract.Storage.PROFILE_PICTURES).child(photo.getLastPathSegment());

        // Upload file to Firebase Storage
        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        // Upload file and metadata to the path
        UploadTask uploadTask = photoRef.putFile(photo, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "storePhotoOnFirebase:putFile:onFailure: ", exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                storeSimulatedParticipantInFirebase(downloadUrl);
            }
        });
    }

    private void storeSimulatedParticipantInFirebase(Uri photoUriInFirebase) {
        Log.d(TAG, "addSimulatedParticipant: eventId "+mEventId);
        Log.d(TAG, "addSimulatedParticipant: name "+mName);
        Log.d(TAG, "addSimulatedParticipant: photo "+photoUriInFirebase);
        Log.d(TAG, "addSimulatedParticipant: age "+mAge);
    }
}
