package com.usal.jorgeav.sportapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.usal.jorgeav.sportapp.R;

public class UpdateEventsWidgetService extends IntentService {
    private static final String ACTION_UPDATE_EVENTS = "com.usal.jorgeav.sportapp.widget.action.UPDATE_EVENTS";

    private static final String EXTRA_USER_ID = "com.usal.jorgeav.sportapp.widget.extra.USER_ID";

    public UpdateEventsWidgetService() {
        super(UpdateEventsWidgetService.class.getSimpleName());
    }

    public static void startActionUpdateEvents(Context context, String userID) {
        Intent intent = new Intent(context, UpdateEventsWidgetService.class);
        intent.setAction(ACTION_UPDATE_EVENTS);
        intent.putExtra(EXTRA_USER_ID, userID);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_EVENTS.equals(action)) {
                final String userID= intent.getStringExtra(EXTRA_USER_ID);
                handleActionUpdateEvents(userID);
            }
        }
    }

    private void handleActionUpdateEvents(String userID) {
        // TODO Query number of events
        int count = 10;

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, EventsAppWidget.class));

        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_text);

        //Now update all widgets
        EventsAppWidget.updateEventsWidgets(this, appWidgetManager, count, appWidgetIds);
    }
}
