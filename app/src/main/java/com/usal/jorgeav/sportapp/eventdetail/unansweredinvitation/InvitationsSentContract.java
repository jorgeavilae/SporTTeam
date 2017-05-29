package com.usal.jorgeav.sportapp.eventdetail.unansweredinvitation;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public abstract class InvitationsSentContract {

    public interface Presenter {
        void loadEventInvitationsSent();
        LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance();
    }

    public interface View {
        void showEventInvitationsSent(Cursor cursor);
        Context getActivityContext();
        InvitationsSentFragment getThis();
    }
}
