package com.usal.jorgeav.sportapp.eventdetail.inviteuser;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

abstract class InviteUserContract {

    public interface Presenter {
        void loadFriends(LoaderManager loaderManager, Bundle bundle);
        void sendInvitationToThisEvent(String eventId, String uid);
    }

    public interface View {
        void showFriends(Cursor cursor);
        Context getActivityContext();
    }
}
