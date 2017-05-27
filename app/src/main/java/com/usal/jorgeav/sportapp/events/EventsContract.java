package com.usal.jorgeav.sportapp.events;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public abstract class EventsContract {

    public interface Presenter {
        void loadEvents();
        LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance();
    }

    public interface View {
        void showMyOwnEvents(Cursor cursor);
        void showParticipatesEvents(Cursor cursor);
        Context getActivityContext();
        Fragment getThis();
    }
}
