package com.usal.jorgeav.sportapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.usal.jorgeav.sportapp.R;

public class UpdateEventsWidgetService extends IntentService {
    @SuppressWarnings("unused")
    private static final String TAG = UpdateEventsWidgetService.class.getSimpleName();

    private static final String ACTION_UPDATE_EVENTS = "com.usal.jorgeav.sportapp.widget.action.UPDATE_EVENTS";

    public UpdateEventsWidgetService() {
        super(UpdateEventsWidgetService.class.getSimpleName());
    }

    public static void startActionUpdateEvents(Context context) {
        Intent intent = new Intent(context, UpdateEventsWidgetService.class);
        intent.setAction(ACTION_UPDATE_EVENTS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_EVENTS.equals(action)) {
                handleActionUpdateEvents();
            }
        }
    }

    private void handleActionUpdateEvents() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, EventsAppWidget.class));

        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_list);

        //Now update all widgets
        EventsAppWidget.updateEventsWidgets(this, appWidgetManager, appWidgetIds);
    }
}
