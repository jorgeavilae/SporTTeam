package com.usal.jorgeav.sportapp.events.searchevent;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public abstract class SearchEventsContract {

    public interface Presenter {
        void loadNearbyEvents(LoaderManager loaderManager, Bundle b);
        void loadNearbyEventsWithSport(LoaderManager loaderManager, Bundle b);
    }

    public interface View {
        void showEvents(Cursor cursor);
        Context getActivityContext();
        SearchEventsContract.View getThis();
    }
}
