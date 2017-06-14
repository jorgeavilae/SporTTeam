package com.usal.jorgeav.sportapp.eventdetail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public abstract class DetailEventContract {

    public interface View {
        void showEventId(String id);
        void showEventSport(String sport);
        void showEventPlace(String place);
        void showEventDate(String date);
        void showEventOwner(String time);
        void showEventTotalPlayers(int totalPlayers);
        void showEventEmptyPlayers(int emptyPlayers);
        void showParticipants(Cursor cursor);
        FragmentActivity getActivityContext();
        Fragment getThis();
        String getEventID();
        void uiSetupForEventRelation();

    }

    public interface Presenter {
        void openEvent(LoaderManager loaderManager, Bundle b);
        @DetailEventPresenter.EventRelationType
        int getRelationTypeBetweenThisEventAndI();

        void sendEventRequest(String eventId);
        void cancelEventRequest(String eventId);
        void acceptEventInvitation(String eventId);
        void declineEventInvitation(String eventId);
        void quitEvent(String eventId);

        void registerUserRelationObserver();
        void unregisterUserRelationObserver();
    }
}
