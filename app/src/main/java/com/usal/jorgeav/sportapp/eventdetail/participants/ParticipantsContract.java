package com.usal.jorgeav.sportapp.eventdetail.participants;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

abstract class ParticipantsContract {

    public interface Presenter {
        void loadParticipants(LoaderManager loaderManager, Bundle b);
        void loadSimulatedParticipants(LoaderManager loaderManager, Bundle b);

        void quitEvent(String userId, String eventId, boolean deleteSimulatedParticipant);
        void deleteSimulatedUser(String simulatedUserId, String eventId);
    }

    public interface View {
        void showParticipants(Cursor cursor);
        void showSimulatedParticipants(Cursor cursor);
        Context getActivityContext();
    }
}
