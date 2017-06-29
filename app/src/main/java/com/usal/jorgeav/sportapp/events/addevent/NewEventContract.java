package com.usal.jorgeav.sportapp.events.addevent;

import android.content.Context;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class NewEventContract {

    public interface Presenter {
        void addEvent(String sport, String field, String name, String city, String date, String time, String total, String empty);
    }

    public interface View {
        Context getActivityContext();
    }
}
