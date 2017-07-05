package com.usal.jorgeav.sportapp.events;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.usal.jorgeav.sportapp.BaseFragment;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public abstract class EventsContract {

    public interface Presenter {
        void loadEvents(LoaderManager loaderManager, Bundle b);
    }

    public interface View {
        void showMyOwnEvents(Cursor cursor);
        void showParticipatesEvents(Cursor cursor);
        Context getActivityContext();
        BaseFragment getThis();
    }
}
