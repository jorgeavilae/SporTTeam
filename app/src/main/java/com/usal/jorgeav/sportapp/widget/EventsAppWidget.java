package com.usal.jorgeav.sportapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

public class EventsAppWidget extends AppWidgetProvider {
    @SuppressWarnings("unused")
    private static final String TAG = EventsAppWidget.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.events_app_widget);

        // Create a PendingIntent to launch EventFragment when clicked.
        // Use the same PendingIntent from launching the app from a notification
        PendingIntent pendingIntentTitle = UtilesNotification.contentEventIntent(context);

        //Widget title
        views.setImageViewResource(R.id.appwidget_image, R.drawable.logo_name_white);
        views.setOnClickPendingIntent(R.id.appwidget_image, pendingIntentTitle);

        // Create a PendingIntentTemplate to launch DetailEventFragment when clicked on list.
        // Use the same PendingIntent from launching the app from a notification
        PendingIntent pendingIntentList = UtilesNotification.contentEventIntent(context);

        //Widget list, set adapter
        Intent intent = new Intent(context, EventListAdapterWidgetService.class);
        views.setRemoteAdapter(R.id.appwidget_list, intent);
        views.setPendingIntentTemplate(R.id.appwidget_list, pendingIntentList);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        UpdateEventsWidgetService.startActionUpdateEvents(context);
    }

    public static void updateEventsWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}

