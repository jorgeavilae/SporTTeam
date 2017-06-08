package com.usal.jorgeav.sportapp.eventdetail.sendinvitation;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public abstract class SendInvitationContract {

    public interface Presenter {
        void loadFriends(LoaderManager loaderManager, Bundle bundle);
        void sendInvitationToThisEvent(String eventId, String uid);
    }

    public interface View {
        void showFriends(Cursor cursor);
        Context getActivityContext();
        SendInvitationFragment getThis();
    }
}
