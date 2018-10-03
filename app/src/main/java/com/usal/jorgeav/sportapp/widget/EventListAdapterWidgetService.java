package com.usal.jorgeav.sportapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.mainactivities.EventsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

/**
 * Esta clase hereda de {@link RemoteViewsService}, es un tipo de {@link android.app.Service}
 * especial que conecta al Proveedor de Contenido de la aplicación, los elementos de la interfaz de
 * un widget que se ejecuta en la aplicación launcher del dispositivo.
 * <p>
 * Lo único que hace es proporcionar una instancia de la clase
 * {@link android.widget.RemoteViewsService.RemoteViewsFactory} que hace las veces de Adaptador para
 * la lista de partidos mostradas en el widget de la aplicación, y que permite requerirle al
 * Proveedor de Contenido los datos que debe mostrar. Para ello, en el manifiesto de la aplicación
 * (AndroidManifest.xml), se declara el Proveedor de Contenido como accesible desde fuera del
 * ámbito de la aplicación (exported=true).
 */
public class EventListAdapterWidgetService extends RemoteViewsService {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = EventListAdapterWidgetService.class.getSimpleName();

    /**
     * Devuelve la clase {@link android.widget.RemoteViewsService.RemoteViewsFactory} que se usa
     * como Adaptador de la lista del widget.
     *
     * @param intent intent que lanza este Servicio
     * @return una nueva instancia de {@link EventListRemoteViewsFactory}
     */
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new EventListRemoteViewsFactory(this.getApplicationContext());
    }

    /**
     * Adaptador de la lista del widget. Se encarga de actualizar los datos mostrados cada vez que
     * se notifica de un cambio {@link android.appwidget.AppWidgetManager#notifyAppWidgetViewDataChanged(int, int)}.
     * <p>
     * Emplaza cada dato obtenido de la consulta al Proveedor de Contenido en sus respectivas
     * celdas de la lista, representadas por {@link android.widget.RemoteViews}.
     */
    private class EventListRemoteViewsFactory implements
            RemoteViewsService.RemoteViewsFactory {
        /**
         * Contexto bajo el que se ejecuta esta clase
         */
        Context mContext;
        /**
         * {@link Cursor} con la colección de partidos que deben mostrarse en la lista.
         */
        Cursor mCursor;

        /**
         * Constructor
         *
         * @param applicationContext contexto bajo el que se ejecuta esta clase
         */
        EventListRemoteViewsFactory(Context applicationContext) {
            mContext = applicationContext;
        }

        /**
         * Inicialización de los componentes de la clase. Sin embargo, operaciones costosas como
         * consultar al Proveedor de Contenido deben hacerse en {@link #onDataSetChanged()}.
         */
        @Override
        public void onCreate() {
            // In onCreate() you set up any connections / cursors to your data source. Heavy lifting,
            // for example downloading or creating content etc, should be deferred to onDataSetChanged()
            // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.

        }

        /**
         * Inicializa {@link #mCursor} realizando la consulta a la base de datos. Se ejecuta cada
         * vez que se inicia el widget y cada vez que se invoca
         * {@link android.appwidget.AppWidgetManager#notifyAppWidgetViewDataChanged(int, int)}
         *
         * @see SportteamLoader#simpleQueryMyEventsAndEventsParticipation(Context, String)
         */
        @Override
        public void onDataSetChanged() {
            if (mCursor != null) mCursor.close();
            String myUserID = Utiles.getCurrentUserId();
            if (TextUtils.isEmpty(myUserID)) return;
            mCursor = SportteamLoader.simpleQueryMyEventsAndEventsParticipation(mContext, myUserID);
        }

        /**
         * Invocado cuando el Adaptador es desvinculado de la lista. Cierra el {@link Cursor}
         */
        @Override
        public void onDestroy() {
            if (mCursor != null) mCursor.close();
        }

        /**
         * Invocado para conocer el tamaño de la colección que debe mostrar la lista.
         *
         * @return el número de partidos en {@link #mCursor}
         */
        @Override
        public int getCount() {
            if (mCursor == null) return 0;
            else return mCursor.getCount();
        }

        /**
         * Devuelve la vista de la celda con los datos correspondientes al partido que representa.
         * Los datos son emplazados en una {@link RemoteViews} con el aspecto descrito en el archivo
         * de layout event_item_app_widget.xml
         * <p>
         * Establece el icono del deporte, el nombre del partido, el lugar donde se juega, la fecha
         * y hora del encuentro y un icono representativo de la relación puestos vacantes/totales.
         * Además, completa el {@link android.app.PendingIntent} asociado a cada celda con el
         * identificador del partido, para que al pulsar sobre ella se abra su vista de detalles
         * {@link RemoteViews#setOnClickFillInIntent(int, Intent)}.
         * <p>
         * Método equivalente a
         * {@link android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
         *
         * @param position posición de la celda que debe cargarse
         * @return {@link RemoteViews} representando al ítem del partido en esa posición
         */
        @Override
        public RemoteViews getViewAt(int position) {
            if (mCursor != null && mCursor.moveToPosition(position)) {
                RemoteViews listItem = new RemoteViews(mContext.getPackageName(), R.layout.event_item_app_widget);

                // Set icon
                String sportId = mCursor.getString(SportteamContract.EventEntry.COLUMN_SPORT);
                listItem.setImageViewResource(R.id.event_item_widget_sport, Utiles.getSportIconFromResource(sportId));

                // Set title
                String name = mCursor.getString(SportteamContract.EventEntry.COLUMN_NAME);
                listItem.setTextViewText(R.id.event_item_widget_name, name);

                // Set subtitle
                String fieldName = UtilesContentProvider.getFieldNameFromContentProvider(
                        mCursor.getString(SportteamContract.EventEntry.COLUMN_FIELD));
                String city = mCursor.getString(SportteamContract.EventEntry.COLUMN_CITY);
                String address = mCursor.getString(SportteamContract.EventEntry.COLUMN_ADDRESS);
                if (fieldName == null)
                    listItem.setTextViewText(R.id.event_item_widget_place, address);
                else {
                    if (!TextUtils.isEmpty(fieldName)) fieldName = fieldName + ", ";
                    listItem.setTextViewText(R.id.event_item_widget_place, fieldName + city);
                }
                long date = mCursor.getLong(SportteamContract.EventEntry.COLUMN_DATE);
                listItem.setTextViewText(R.id.event_item_widget_date, UtilesTime.millisToDateTimeWidgetString(date));

                // Set icon two
                int totalPl = mCursor.getInt(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS);
                int emptyPl = mCursor.getInt(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS);
                int playerIcon = Utiles.getPlayerIconFromResource(emptyPl, totalPl);
                if (playerIcon != -1)
                    listItem.setImageViewResource(R.id.event_item_widget_player, playerIcon);

                //Set PendingIntent
                String eventId = mCursor.getString(SportteamContract.EventEntry.COLUMN_EVENT_ID);
                Intent fillInIntent = new Intent();
                fillInIntent.putExtra(EventsActivity.EVENTID_PENDING_INTENT_EXTRA, eventId);
                listItem.setOnClickFillInIntent(R.id.event_item_widget_container, fillInIntent);

                return listItem;
            }
            return null;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
