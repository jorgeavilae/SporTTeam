package com.usal.jorgeav.sportapp.profile.sendinvitation;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

abstract class SendInvitationContract {

    public interface Presenter {
        void loadEventsForInvitation(LoaderManager loaderManager, Bundle b);
        void sendInvitationToThisUser(String eventId, String uid);
    }

    public interface View {
        void showEventsForInvitation(Cursor cursor);
        Context getActivityContext();
    }
}
