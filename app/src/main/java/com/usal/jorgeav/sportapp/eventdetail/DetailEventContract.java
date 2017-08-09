package com.usal.jorgeav.sportapp.eventdetail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.Invitation;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public abstract class DetailEventContract {

    public interface View {
        void showEventSport(String sport);
        void showEventField(Field field, String address, LatLng coord);
        void showEventName(String name);
        void showEventDate(long date);
        void showEventOwner(String city);
        void showEventPlayers(int emptyPlayers, int totalPlayers);
        void clearUI();
        Context getActivityContext();
        BaseFragment getThis();
        String getEventID();
        void uiSetupForEventRelation(@DetailEventPresenter.EventRelationType int relation);

    }

    public interface Presenter {
        void openEvent(LoaderManager loaderManager, Bundle b);
        void deleteEvent(Bundle b);
        void getRelationTypeBetweenThisEventAndI();

        void sendEventRequest(String eventId);
        void cancelEventRequest(String eventId);
        void acceptEventInvitation(String eventId, String sender);
        Invitation getEventInvitation();
        void declineEventInvitation(String eventId, String sender);
        void quitEvent(String eventId, boolean deleteSimulatedParticipants);

        void registerUserRelationObserver();
        void unregisterUserRelationObserver();
    }
}
