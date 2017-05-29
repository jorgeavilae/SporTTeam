package com.usal.jorgeav.sportapp.eventdetail.sendinvitation;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public abstract class SendInvitationContract {

    public interface Presenter {
        void loadFriends();
        LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance();
    }

    public interface View {
        void showFriends(Cursor cursor);
        Context getActivityContext();
        SendInvitationFragment getThis();
    }
}
