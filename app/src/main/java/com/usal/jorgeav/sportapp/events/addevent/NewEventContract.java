package com.usal.jorgeav.sportapp.events.addevent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public abstract class NewEventContract {

    public interface Presenter {
        void openEvent(LoaderManager loaderManager, Bundle b);
        void addEvent(String id, String sport, String field, LatLng coord, String name, String city,
                      String date, String time, String total, String empty,
                      HashMap<String, Boolean> participants);
    }

    public interface View {
        void showEventSport(String sport);
        void showEventField(String fieldId, LatLng coordinates);
        void showEventName(String name);
        void showEventDate(long date);
        void showEventCity(String city);
        void showEventTotalPlayers(int totalPlayers);
        void showEventEmptyPlayers(int emptyPlayers);
        void setParticipants(HashMap<String, Boolean> map);
        void clearUI();
        Context getActivityContext();

    }
}
