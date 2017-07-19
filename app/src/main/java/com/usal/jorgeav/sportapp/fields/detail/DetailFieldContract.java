package com.usal.jorgeav.sportapp.fields.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public abstract class DetailFieldContract {

    public interface View {
        void showFieldId(String id);
        void showFieldName(String name);
        void showFieldAddress(String address, LatLng coordinates);
        void showFieldRating(Float rating);
        void showFieldSport(String sport);
        void showFieldOpeningTime(String opening);
        void showFieldClosingTime(String closing);
        void clearUI();
        Context getActivityContext();
    }

    public interface Presenter {
        void openField(LoaderManager loaderManager, Bundle b);
        void voteField(Bundle b, float rating);
    }

}
