package com.usal.jorgeav.sportapp.notifications;

import android.content.Context;

import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.data.MyNotification;

import java.util.HashMap;

abstract class NotificationsContract {

    public interface Presenter {
        void loadNotifications();
        void deleteNotification(String key);
        void deleteAllNotifications();
    }

    public interface View {
        void showNotifications(HashMap<String, MyNotification> notifications);
        Context getActivityContext();
        public BaseFragment getThis();
    }
}
