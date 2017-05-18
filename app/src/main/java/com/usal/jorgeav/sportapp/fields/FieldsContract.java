package com.usal.jorgeav.sportapp.fields;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public abstract class FieldsContract {

    public interface Presenter {
        void loadFields();
        LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance();
    }

    public interface View {
        void showFields(Cursor cursor);
        Context getContext();
    }
}
