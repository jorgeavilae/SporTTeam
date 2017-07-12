package com.usal.jorgeav.sportapp.eventdetail.simulateparticipant;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.usal.jorgeav.sportapp.mainactivities.BaseActivity;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;

/**
 * Created by Jorge Avila on 12/07/2017.
 */

public class SimulateParticipantPresenter implements SimulateParticipantContract.Presenter {
    public static final String TAG = SimulateParticipantPresenter.class.getSimpleName();
    SimulateParticipantContract.View mView;
    String mEventId;
    String mName;
    int mAge;

    public SimulateParticipantPresenter(SimulateParticipantContract.View view){
        this.mView = view;
        mEventId = "";
        mName = "";
        mAge = -1;
    }

    @Override
    public void addSimulatedParticipant(String eventId, String name, Uri photo, String age) {
        if(eventId != null && !TextUtils.isEmpty(eventId) && TextUtils.isEmpty(name)
                && !TextUtils.isEmpty(age)) { //Validate arguments
            mEventId = eventId;
            mName = name;
            mAge = Integer.parseInt(age);
            if (photo != null)
                storePhotoOnFirebase(photo);
            else {
                storeSimulatedParticipantInFirebase(null);
            }
        }
        ((BaseActivity)mView.getActivityContext()).onBackPressed();
    }

    private void storePhotoOnFirebase(Uri photo) {
        FirebaseActions.storePhotoOnFirebase(photo, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                storeSimulatedParticipantInFirebase(downloadUrl);
            }
        });
    }

    private void storeSimulatedParticipantInFirebase(Uri photoUriInFirebase) {

        FirebaseActions.addSimulatedParticipant(mEventId, mName, photoUriInFirebase, mAge);
        Log.d(TAG, "addSimulatedParticipant: eventId "+mEventId);
        Log.d(TAG, "addSimulatedParticipant: name "+mName);
        Log.d(TAG, "addSimulatedParticipant: photo "+photoUriInFirebase);
        Log.d(TAG, "addSimulatedParticipant: age "+mAge);
    }
}
