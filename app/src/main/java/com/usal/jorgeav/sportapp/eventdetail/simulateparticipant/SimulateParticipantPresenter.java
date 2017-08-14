package com.usal.jorgeav.sportapp.eventdetail.simulateparticipant;

import android.net.Uri;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
import com.usal.jorgeav.sportapp.network.firebase.actions.FirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;

class SimulateParticipantPresenter implements SimulateParticipantContract.Presenter {
    public static final String TAG = SimulateParticipantPresenter.class.getSimpleName();

    private SimulateParticipantContract.View mView;
    private String mEventId;
    private String mName;
    private Long mAge;

    SimulateParticipantPresenter(SimulateParticipantContract.View view) {
        this.mView = view;
        mEventId = "";
        mName = "";
        mAge = -1L;
    }

    @Override
    public void addSimulatedParticipant(String eventId, String name, Uri photo, String age) {
        if (eventId != null && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(name)
                && !TextUtils.isEmpty(age)) { //Validate arguments

            try {
                mAge = Long.parseLong(age);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                mView.showError(R.string.new_simulated_user_invalid_arg);
                return;
            }

            if (mAge <= 12 || mAge >= 100) {
                mView.showError(R.string.error_incorrect_age);
                return;
            }

            mEventId = eventId;
            mName = name;
            if (photo != null)
                storePhotoOnFirebase(photo);
            else {
                String myUserID = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(myUserID)) return;
                SimulatedUser su = new SimulatedUser(mName, null, mAge, myUserID);
                FirebaseActions.addSimulatedParticipant(mView.getThis(), mEventId, su);
            }
            mView.hideContent();
        } else
            mView.showError(R.string.new_simulated_user_invalid_arg);
    }

    private void storePhotoOnFirebase(Uri photo) {
        FirebaseActions.storePhotoOnFirebase(photo, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                String myUserID = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(myUserID)) return;
                String photo = downloadUrl != null ? downloadUrl.toString() : null;
                SimulatedUser su = new SimulatedUser(mName, photo, mAge, myUserID);
                FirebaseActions.addSimulatedParticipant(mView.getThis(), mEventId, su);
            }
        });
    }
}
