package com.usal.jorgeav.sportapp.fields.fielddetail;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.maps.model.LatLng;

abstract class DetailFieldContract {

    public interface View {
        void showFieldName(String name);
        void showFieldPlace(String address, String city, LatLng coords);
        void showFieldTimes(long openTime, long closeTime);
        void showFieldCreator(String creator);
        void showSportCourts(Cursor cursor);
        void clearUI();

        Context getActivityContext();
    }

    public interface Presenter {
        void openField(LoaderManager loaderManager, Bundle b);

        boolean voteSportInField(String fieldId, String sportId, float rating);
        void deleteField(String mFieldId);
    }

}
