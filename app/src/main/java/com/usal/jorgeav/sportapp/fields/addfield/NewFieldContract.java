package com.usal.jorgeav.sportapp.fields.addfield;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.usal.jorgeav.sportapp.data.Field;

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
//        void showEventSport(String sport);
//        void showEventPlace(String place);
//        void showEventName(String name);
//        void showEventDate(long date);
//        void showEventCity(String city);
//        void showEventTotalPlayers(int totalPlayers);
//        void showEventEmptyPlayers(int emptyPlayers);
//        void setParticipants(HashMap<String, Boolean> map);
        void clearUI();
        Context getActivityContext();
        void retrieveFields(ArrayList<Field> dataList);
    }
}
