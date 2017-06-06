package com.usal.jorgeav.sportapp.events.addevent.selectfield;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class SelectFieldContract {

    public interface Presenter {
        void loadFieldsWithSport(String sportId);
        LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance();
    }

    public interface View {
        void showFields(Cursor cursor);
        Context getActivityContext();
        SelectFieldFragment getThis();
    }
}
