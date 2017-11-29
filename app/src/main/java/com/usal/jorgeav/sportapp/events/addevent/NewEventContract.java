package com.usal.jorgeav.sportapp.events.addevent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SimulatedUser;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class NewEventContract {

    public interface Presenter {
        void openEvent(LoaderManager loaderManager, Bundle b);
        void loadFields(LoaderManager loaderManager, Bundle b);
        void loadFriends(LoaderManager loaderManager, Bundle b);
        void addEvent(String id, String sport, String field, String address, LatLng coord, String name, String city,
                      String date, String time, String total, String empty,
                      HashMap<String, Boolean> participants,
                      HashMap<String, SimulatedUser> simulatedParticipants,
                      ArrayList<String> friendsId);

        void stopLoadFields(LoaderManager loaderManager);
        void stopLoadFriends(LoaderManager loaderManager);
    }

    public interface View {
        void showEventSport(String sport);
        void showEventField(String fieldId, String address, String city, LatLng coordinates);
        void showEventName(String name);
        void showEventDate(long date);
        void showEventTotalPlayers(int totalPlayers);
        void showEventEmptyPlayers(int emptyPlayers);
        void setParticipants(HashMap<String, Boolean> participants);
        void setSimulatedParticipants(HashMap<String, SimulatedUser> simulatedParticipants);
        void retrieveFields(ArrayList<Field> fieldList);
        void retrieveFriendsID(ArrayList<String> friendsIdList);
        void clearUI();
        Context getActivityContext();
        BaseFragment getThis();
    }
}
