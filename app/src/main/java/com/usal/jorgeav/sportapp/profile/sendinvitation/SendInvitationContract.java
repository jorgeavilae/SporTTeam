package com.usal.jorgeav.sportapp.profile.sendinvitation;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public abstract class SendInvitationContract {

    public interface Presenter {
        void loadEventsForInvitation(LoaderManager loaderManager, Bundle b);
    }

    public interface View {
        void showEventsForInvitation(Cursor cursor);
        Context getActivityContext();
    }
}
