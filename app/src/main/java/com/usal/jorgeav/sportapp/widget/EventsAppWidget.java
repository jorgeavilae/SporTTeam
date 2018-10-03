package com.usal.jorgeav.sportapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

/**
 * Esta clase representa a los widget (puede haber varios) que se muestran en la aplicación launcher
 * del teléfono. Cada uno consiste en una lista de los próximos partidos a los que debe asistir el
 * usuario cuya sesión esté iniciada. Las pulsaciones sobre la cabecera de la lista abren la
 * pantalla con el calendario de partidos, las pulsaciones sobre cada uno de los partidos de la
 * lista abren la pantalla con los detalles de dicho partido.
 * <p>
 * Los Widget, incluido este, deben extender de {@link AppWidgetProvider} que es una clase derivada de
 * {@link android.content.BroadcastReceiver}, por lo que hay que declararlo así en el manifiesto de
 * la aplicación (AndroidManifest.xml).
 * <p>
 * Para ejecutar el código de esta clase, debe activarse el Widget enviando un broadcast que lance
 * el método {@link #onReceive(Context, Intent)} o al cumplirse un periodo de tiempo especificado en
 * el archivo de metadatos del Widget (events_app_widget_info.xml) lo que lanzará el método
 * {@link #onUpdate(Context, AppWidgetManager, int[])}.
 * <p>
 * Además de esos métodos, en esta clase se implementa el código de creación e iniciación de las
 * {@link RemoteViews} que conforman el Widget en el launcher.
 */
public class EventsAppWidget extends AppWidgetProvider {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = EventsAppWidget.class.getSimpleName();

    /**
     * Se ejecuta cuando se recibe un mensaje broadcast destinado a este Broadcast Receiver.
     * Invoca {@link #onUpdate(Context, AppWidgetManager, int[])} en su método de la superclase.
     *
     * @param context contexto del Broadcast Receiver
     * @param intent  Intent recibido
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    /**
     * Debe actualizar la interfaz y el contenido mostrado en los widgets. Como se ejecuta en el hilo
     * de la interfaz, invoca la creación de {@link UpdateEventsWidgetService}, un
     * {@link android.app.IntentService} desde el que consultar al Proveedor de Contenido la nueva
     * colección de partidos a mostrar.
     * <p>
     * Invocado desde {@link #onReceive(Context, Intent)} o al cumplirse un periodo de tiempo
     * especificado en el archivo de metadatos del Widget (events_app_widget_info.xml).
     *
     * @param context          contexto bajo el que se está ejecutando
     * @param appWidgetManager un objeto {@link AppWidgetManager} para actualizar los widgets del
     *                         launcher
     * @param appWidgetIds     lista de identificadores de los widget que necesitan actualizarse
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        UpdateEventsWidgetService.startActionUpdateEvents(context);
    }

    /**
     * Método estático utilizado para actualizar todos los widgets del launcher. Es lanzado desde
     * {@link UpdateEventsWidgetService}. Ocurre fuera del hilo principal de ejecución.
     *
     * @param context          contexto bajo el que se está ejecutando
     * @param appWidgetManager un objeto {@link AppWidgetManager} para actualizar los widgets del
     *                         launcher
     * @param appWidgetIds     lista de identificadores de los widget que necesitan actualizarse
     */
    public static void updateEventsWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     * Método estático utilizado para actualizar uno de los widgets del launcher. Ocurre fuera del
     * hilo principal de ejecución.
     * <p>
     * Crea la {@link RemoteViews} a partir de un archivo de layout, obtiene las referencias a sus
     * elementos, establece la cabecera de la lista y asocia los {@link PendingIntent} que se
     * lanzarán al pulsar sobre el widget.
     * <p>
     * Para las celdas de la lista se establece una plantilla de {@link PendingIntent} con
     * {@link RemoteViews#setPendingIntentTemplate(int, PendingIntent)}, que será completada con el
     * identificador del partido en el adaptador.
     * <p>
     * Para establecer el Adaptador utilizado se utiliza
     * {@link RemoteViews#setRemoteAdapter(int, Intent)} y la clase
     * {@link EventListAdapterWidgetService}.
     *
     * @param context          contexto bajo el que se está ejecutando
     * @param appWidgetManager un objeto {@link AppWidgetManager} para actualizar los widgets del
     *                         launcher
     * @param appWidgetId      identificador de uno de los widget que necesitan actualizarse
     */
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

        // Instruct the widget manager to update the widget. Like commit changes.
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

