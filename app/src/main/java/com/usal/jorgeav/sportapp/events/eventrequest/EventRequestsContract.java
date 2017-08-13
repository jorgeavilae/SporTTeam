package com.usal.jorgeav.sportapp.events.eventrequest;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

abstract class EventRequestsContract {

    public interface Presenter {
        void loadEventRequests(LoaderManager loaderManager, Bundle b);
    }

    public interface View {
        void showEventRequests(Cursor cursor);
        Context getActivityContext();
    }
}
