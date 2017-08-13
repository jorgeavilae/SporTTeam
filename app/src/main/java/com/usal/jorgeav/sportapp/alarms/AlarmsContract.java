package com.usal.jorgeav.sportapp.alarms;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

abstract class AlarmsContract {

    public interface Presenter {
        void loadAlarms(LoaderManager loaderManager, Bundle b);
    }

    public interface View {
        void showAlarms(Cursor cursor);
        Context getActivityContext();
    }
}
