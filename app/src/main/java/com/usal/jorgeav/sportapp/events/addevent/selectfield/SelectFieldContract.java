package com.usal.jorgeav.sportapp.events.addevent.selectfield;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public abstract class SelectFieldContract {

    public interface Presenter {
        void loadFieldsWithSport(LoaderManager loaderManager, Bundle b);
    }

    public interface View {
        void showFields(Cursor cursor);
        Context getActivityContext();
    }
}
