package com.usal.jorgeav.sportapp.profile.invitationreceived;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

abstract class InvitationsReceivedContract {

    public interface Presenter {
        void loadEventInvitations(LoaderManager loaderManager, Bundle b);
    }

    public interface View {
        void showEventInvitations(Cursor cursor);
        Context getActivityContext();
    }
}
