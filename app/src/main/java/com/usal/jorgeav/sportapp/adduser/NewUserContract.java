package com.usal.jorgeav.sportapp.adduser;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.data.Sport;

import java.util.ArrayList;

class NewUserContract {

    public interface Presenter {
        void checkUserEmailExists(String email);
        void checkUserNameExists(String name);

        void createAuthUser(String email, String pass, String name, Uri croppedImageFileSystemUri, String city, LatLng coords,
                            Long age, ArrayList<Sport> sportsList);
    }

    public interface View {
        void croppedResult(Uri photoCroppedUri);

        Context getActivityContext();
        Activity getHostActivity();

        void setEmailError(int stringRes);
        void setNameError(int stringRes);

        void showContent();
    }
}
