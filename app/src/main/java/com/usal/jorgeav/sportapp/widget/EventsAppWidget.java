package com.usal.jorgeav.sportapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.Locale;

public class EventsAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int count, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.events_app_widget);
        CharSequence widgetText = String.format(Locale.getDefault(), "%2d", count);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // TODO cambiar count 13
        String currentUserID = Utiles.getCurrentUserId();
        UpdateEventsWidgetService.startActionUpdateEvents(context, currentUserID);
    }

    public static void updateEventsWidgets(Context context, AppWidgetManager appWidgetManager, int count, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, count, appWidgetId);
        }
    }
}

