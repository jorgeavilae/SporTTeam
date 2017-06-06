package com.usal.jorgeav.sportapp.events.addevent;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class NewEventContract {

    public interface Presenter {
        void addEvent(String sport, String field, String city, String date, String time, String total, String empty);
        LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance();
    }

    public interface View {
        Context getActivityContext();
        Fragment getThis();
    }
}
