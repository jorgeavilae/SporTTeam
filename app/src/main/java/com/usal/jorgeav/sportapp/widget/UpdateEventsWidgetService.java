package com.usal.jorgeav.sportapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.usal.jorgeav.sportapp.R;

/**
 * {@link IntentService} utilizado para las actualizaciones del widget de la colección de partidos.
 * Dado que requiere hacer una consulta al Proveedor de Contenido de la aplicación, se crea este
 * {@link IntentService} para realizar dicha consulta en segundo plano. Finaliza automáticamente al
 * terminar la ejecución, no se repite.
 * <p>
 * Implementa un método estático que lanza este mismo Servicio
 * {@link #startActionUpdateEvents(Context)}. Este método será invocado por el widget cuando necesite
 * actualizarse y por {@link com.usal.jorgeav.sportapp.network.firebase.sync.EventsFirebaseSync}
 * cuando necesite actualizar la colección mostrada en el widget.
 */
public class UpdateEventsWidgetService extends IntentService {
    /**
     * Nombre de la clase
     */
    private static final String TAG = UpdateEventsWidgetService.class.getSimpleName();

    /**
     * Etiqueta identificativa de la acción que debe realizar este Service: actualizar los partidos
     * del widget.
     */
    private static final String ACTION_UPDATE_EVENTS =
            "com.usal.jorgeav.sportapp.widget.action.UPDATE_EVENTS";

    /**
     * Constructor. Debe invocar el constructor de la superclase con el nombre de esta clase.
     */
    public UpdateEventsWidgetService() {
        super(TAG);
    }

    /**
     * Método estático utilizado para lanzar este {@link IntentService} con la acción
     * {@link #ACTION_UPDATE_EVENTS}.
     *
     * @param context contexto en el que se ejecuta
     */
    public static void startActionUpdateEvents(Context context) {
        Intent intent = new Intent(context, UpdateEventsWidgetService.class);
        intent.setAction(ACTION_UPDATE_EVENTS);
        context.startService(intent);
    }

    /**
     * Invocado al iniciar el Servicio. Identifica la acción que lo inició. Como sólo existe
     * {@link #ACTION_UPDATE_EVENTS}, ejecuta {@link #handleActionUpdateEvents()}.
     *
     * @param intent intent recibido que lanza la ejecución
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_EVENTS.equals(action)) {
                handleActionUpdateEvents();
            }
        }
    }

    /**
     * Actualiza los widgets de esta aplicación, alojados en el launcher. Para ello obtiene una
     * referencia a {@link AppWidgetManager} y con ella la lista de identificadores de todos los
     * widget de la interfaz. Con ambas, ejecuta
     * {@link EventsAppWidget#updateEventsWidgets(Context, AppWidgetManager, int[])}
     * <p>
     * Además, actualiza la lista de partidos notificando su cambio, lo que provoca que se ejecute
     * {@link EventListAdapterWidgetService.EventListRemoteViewsFactory#onDataSetChanged()}
     */
    private void handleActionUpdateEvents() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, EventsAppWidget.class));

        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_list);

        //Now update all widgets
        EventsAppWidget.updateEventsWidgets(this, appWidgetManager, appWidgetIds);
    }
}
