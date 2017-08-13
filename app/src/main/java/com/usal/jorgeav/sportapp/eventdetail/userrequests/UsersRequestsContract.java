package com.usal.jorgeav.sportapp.eventdetail.userrequests;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

abstract class UsersRequestsContract {

    public interface Presenter {
        void loadUsersRequests(LoaderManager loaderManager, Bundle b);

        void acceptUserRequestToThisEvent(String eventId, String uid);
        void declineUserRequestToThisEvent(String eventId, String uid);
        void unblockUserParticipationRejectedToThisEvent(String eventId, String uid);
    }

    public interface View {
        void showUsersRequests(Cursor cursor);
        void showRejectedUsers(Cursor cursor);
        Context getActivityContext();
    }
}
