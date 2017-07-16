package com.usal.jorgeav.sportapp.fields.addfield;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.MyPlace;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public abstract class NewFieldContract {

    public interface Presenter {
//        void openField(LoaderManager loaderManager, Bundle b);
        void loadNearbyFields(LoaderManager loaderManager, Bundle b);
//        void addField(String id, String sport, String field, String name, String city,
//                      String date, String time, String total, String empty,
//                      HashMap<String, Boolean> participants);
    }

    public interface View {
        void showFieldSport(String sport);
        void showFieldPlace(MyPlace place);
        void showFieldName(String name);
        void showFieldOpenTime(long time);
        void showFieldCloseTime(long time);
        void showFieldRate(float rate);
        void clearUI();
        Context getActivityContext();
        void retrieveFields(ArrayList<Field> dataList);
    }
}
