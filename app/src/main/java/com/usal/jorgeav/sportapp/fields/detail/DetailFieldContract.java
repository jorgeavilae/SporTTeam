package com.usal.jorgeav.sportapp.fields.detail;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public abstract class DetailFieldContract {

    public interface View {
        void showFieldId(String id);
        void showFieldName(String name);
        void showFieldAddress(String address);
        void showFieldRating(Float rating);
        void showFieldSport(String sport);
        void showFieldOpeningTime(String opening);
        void showFieldClosingTime(String closing);
        Context getActivityContext();
    }

    public interface Presenter {
        void openField();
        LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance();
    }

}
