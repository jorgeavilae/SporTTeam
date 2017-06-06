package com.usal.jorgeav.sportapp.events.searchevent;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class SearchEventsContract {

    public interface Presenter {
        void loadEvents();
        LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance();
    }

    public interface View {
        void showEvents(Cursor cursor);
        Context getActivityContext();
        SearchEventsContract.View getThis();
    }
}
