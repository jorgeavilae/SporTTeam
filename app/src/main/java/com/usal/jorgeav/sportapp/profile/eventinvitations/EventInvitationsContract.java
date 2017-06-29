package com.usal.jorgeav.sportapp.profile.eventinvitations;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 27/05/2017.
 */

public abstract class EventInvitationsContract {

    public interface Presenter {
        void loadEventInvitations(LoaderManager loaderManager, Bundle b);
    }

    public interface View {
        void showEventInvitations(Cursor cursor);
        Context getActivityContext();
    }
}
