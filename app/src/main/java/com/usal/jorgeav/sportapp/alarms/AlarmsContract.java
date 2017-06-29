package com.usal.jorgeav.sportapp.alarms;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public abstract class AlarmsContract {

    public interface Presenter {
        void loadAlarms(LoaderManager loaderManager, Bundle b);
    }

    public interface View {
        void showAlarms(Cursor cursor);
        Context getActivityContext();
    }
}
