package com.usal.jorgeav.sportapp.eventdetail.simulateparticipant;

import android.content.Context;
import android.net.Uri;

import com.usal.jorgeav.sportapp.BaseFragment;

/**
 * Created by Jorge Avila on 12/07/2017.
 */

public abstract class SimulateParticipantContract {

    public interface Presenter {
        void addSimulatedParticipant(String eventId, String name, Uri photo, String age);
    }

    public interface View {
        Context getActivityContext();
        void croppedResult(Uri photoCroppedUri);
        void startCropActivity(Uri uri);
        void showResult(String msg);
        void showContent();
        void hideContent();
        BaseFragment getThis();
    }
}
