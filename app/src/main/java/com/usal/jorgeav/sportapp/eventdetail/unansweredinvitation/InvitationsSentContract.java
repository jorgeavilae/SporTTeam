package com.usal.jorgeav.sportapp.eventdetail.unansweredinvitation;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

abstract class InvitationsSentContract {

    public interface Presenter {
        void loadEventInvitationsSent(LoaderManager loaderManager, Bundle bundle);
        void deleteInvitationToThisEvent(String eventId, String uid);
    }

    public interface View {
        void showEventInvitationsSent(Cursor cursor);
        Context getActivityContext();
    }
}
