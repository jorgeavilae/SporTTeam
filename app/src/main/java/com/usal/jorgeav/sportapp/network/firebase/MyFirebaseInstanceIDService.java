package com.usal.jorgeav.sportapp.network.firebase;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.usal.jorgeav.sportapp.network.firebase.actions.UserFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInstIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        String myUserID = Utiles.getCurrentUserId();
        if(!TextUtils.isEmpty(myUserID))
            UserFirebaseActions.updateUserToken(myUserID, refreshedToken);
    }
}
