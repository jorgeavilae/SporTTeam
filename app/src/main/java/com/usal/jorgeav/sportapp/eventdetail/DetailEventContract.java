package com.usal.jorgeav.sportapp.eventdetail;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.data.Invitation;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public abstract class DetailEventContract {

    public interface View {
        void showEventId(String id);
        void showEventSport(String sport);
        void showEventField(String fieldId, String address, LatLng coord, String sport);
        void showEventName(String name);
        void showEventDate(String date);
        void showEventOwner(String city);
        void showEventTotalPlayers(int totalPlayers);
        void showEventEmptyPlayers(int emptyPlayers);
        void showParticipants(Cursor cursor);
        void showSimulatedParticipants(Cursor cursor);
        void clearUI();
        Context getActivityContext();
        BaseFragment getThis();
        String getEventID();
        void uiSetupForEventRelation(@DetailEventPresenter.EventRelationType int relation);

    }

    public interface Presenter {
        void openEvent(LoaderManager loaderManager, Bundle b);
        void loadParticipants(LoaderManager loaderManager, Bundle b);
        void deleteEvent(Bundle b);
        void getRelationTypeBetweenThisEventAndI();

        void sendEventRequest(String eventId);
        void cancelEventRequest(String eventId);
        void acceptEventInvitation(String eventId, String sender);
        Invitation getEventInvitation();
        void declineEventInvitation(String eventId, String sender);
        void quitEvent(String eventId);
        void quitEvent(String userId, String eventId);
        void deleteSimulatedUser(String simulatedUserId, String eventId);

        void registerUserRelationObserver();
        void unregisterUserRelationObserver();
    }
}
