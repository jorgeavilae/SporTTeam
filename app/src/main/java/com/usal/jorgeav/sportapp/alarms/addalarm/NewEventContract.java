package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class NewEventContract {

    public interface Presenter {
        void addEvent(String sport, String field, String city, String date, String time, String total, String empty);
    }

    public interface View {
        Context getActivityContext();
        Fragment getThis();
    }
}
