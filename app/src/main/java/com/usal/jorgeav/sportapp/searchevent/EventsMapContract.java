package com.usal.jorgeav.sportapp.searchevent;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

public abstract class EventsMapContract {

    public interface Presenter {
        void loadNearbyEvents(LoaderManager loaderManager, Bundle b);
    }

    public interface View {
        void showEvents(Cursor cursor);
        Context getActivityContext();
    }
}
