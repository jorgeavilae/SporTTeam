package com.usal.jorgeav.sportapp.eventdetail.simulateparticipant;

import android.content.Context;
import android.net.Uri;

import com.usal.jorgeav.sportapp.BaseFragment;

public abstract class SimulateParticipantContract {

    public interface Presenter {
        void addSimulatedParticipant(String eventId, String name, Uri photo, String age);
    }

    public interface View {
        void croppedResult(Uri photoCroppedUri);
        void showResult(String msg);
        void showError(int msgResource);
        void showContent();
        void hideContent();
        BaseFragment getThis();
        Context getActivityContext();
    }
}
