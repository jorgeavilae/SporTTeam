package com.usal.jorgeav.sportapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.usal.jorgeav.sportapp.R;

public class EventsAppWidget extends AppWidgetProvider {
    @SuppressWarnings("unused")
    private static final String TAG = EventsAppWidget.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.events_app_widget);

        //Widget title
        views.setImageViewResource(R.id.appwidget_image, R.drawable.logo_name_white);

        //Widget list
        Intent intent = new Intent(context, EventListWidgetService.class);
        views.setRemoteAdapter(R.id.appwidget_list, intent);

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

