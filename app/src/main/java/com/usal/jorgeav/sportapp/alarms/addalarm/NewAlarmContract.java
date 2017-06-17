package com.usal.jorgeav.sportapp.alarms.addalarm;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public abstract class NewAlarmContract {

    public interface Presenter {
        void addAlarm(String sport, String field, String city,
                      String dateFrom, String dateTo,
                      String totalFrom, String totalTo,
                      String emptyFrom, String emptyTo);
    }

    public interface View {
        Context getActivityContext();
        Fragment getThis();
    }
}
