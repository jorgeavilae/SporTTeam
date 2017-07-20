package com.usal.jorgeav.sportapp.eventdetail.simulateparticipant;

import android.net.Uri;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;

/**
 * Created by Jorge Avila on 12/07/2017.
 */

public class SimulateParticipantPresenter implements SimulateParticipantContract.Presenter {
    public static final String TAG = SimulateParticipantPresenter.class.getSimpleName();
    SimulateParticipantContract.View mView;
    String mEventId;
    String mName;
    Long mAge;

    public SimulateParticipantPresenter(SimulateParticipantContract.View view){
        this.mView = view;
        mEventId = "";
        mName = "";
        mAge = -1L;
    }

    @Override
    public void addSimulatedParticipant(String eventId, String name, Uri photo, String age) {
        if(eventId != null && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(name)
                && !TextUtils.isEmpty(age)) { //Validate arguments
            mEventId = eventId;
            mName = name;
            mAge = Long.parseLong(age);
            if (photo != null)
                storePhotoOnFirebase(photo);
            else {
                String myUserID = Utiles.getCurrentUserId();
                if(TextUtils.isEmpty(myUserID)) return;
                SimulatedUser su = new SimulatedUser(mName, null, mAge, myUserID);
                FirebaseActions.addSimulatedParticipant(mView.getThis(), mEventId, su);
            }
        }
        mView.hideContent();
    }

    private void storePhotoOnFirebase(Uri photo) {
        FirebaseActions.storePhotoOnFirebase(photo, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                String myUserID = Utiles.getCurrentUserId();
                if(TextUtils.isEmpty(myUserID)) return;
                String photo = downloadUrl!=null?downloadUrl.toString():null;
                SimulatedUser su = new SimulatedUser(mName, photo, mAge, myUserID);
                FirebaseActions.addSimulatedParticipant(mView.getThis(), mEventId, su);
            }
        });
    }
}
