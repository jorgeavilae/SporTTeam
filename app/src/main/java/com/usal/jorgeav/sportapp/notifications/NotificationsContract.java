package com.usal.jorgeav.sportapp.notifications;

import android.content.Context;

import com.usal.jorgeav.sportapp.data.MyNotification;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public abstract class NotificationsContract {

    public interface Presenter {
        void loadNotifications();
    }

    public interface View {
        void showNotifications(ArrayList<MyNotification> notifications);
        Context getActivityContext();
    }
}
