package com.usal.jorgeav.sportapp.profile.eventinvitations;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 27/05/2017.
 */

public class EventInvitationsContract {

    public interface Presenter {
        void loadEventInvitations();
        LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance();
    }

    public interface View {
        void showEventInvitations(Cursor cursor);
        Context getActivityContext();
        EventInvitationsFragment getThis();
    }
}
