package com.usal.jorgeav.sportapp.fields.addfield;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SportCourt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public abstract class NewFieldContract {

    public interface Presenter {
        void openField(LoaderManager loaderManager, Bundle b);
        void loadNearbyFields(LoaderManager loaderManager, Bundle b);
        void addField(String id, String name, String address, LatLng coords, String city,
                      String openTime, String closeTime, String creator, List<SportCourt> sports);
    }

    public interface View {
        void showFieldPlace(String address, String city, LatLng coords);
        void showFieldName(String name);
        void showFieldTimes(long openTime, long closeTimes);
        void showFieldCreator(String creator);
        void setSportCourts(List<SportCourt> sports);
        void clearUI();
        Context getActivityContext();
        void retrieveFields(ArrayList<Field> dataList);
    }
}
