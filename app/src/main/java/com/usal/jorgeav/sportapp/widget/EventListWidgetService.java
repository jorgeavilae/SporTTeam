package com.usal.jorgeav.sportapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

public class EventListWidgetService extends RemoteViewsService {
    @SuppressWarnings("unused")
    private static final String TAG = EventListWidgetService.class.getSimpleName();
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new EventListRemoteViewsFactory(this.getApplicationContext());
    }

    private class EventListRemoteViewsFactory implements RemoteViewsFactory {
        Context mContext;
        Cursor mCursor;

        EventListRemoteViewsFactory(Context applicationContext) {
            mContext = applicationContext;
        }

        @Override
        public void onCreate() {
            // In onCreate() you set up any connections / cursors to your data source. Heavy lifting,
            // for example downloading or creating content etc, should be deferred to onDataSetChanged()
            // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.

        }

        @Override
        public void onDataSetChanged() {
            if (mCursor != null) mCursor.close();
            String myUserID = Utiles.getCurrentUserId();
            if (TextUtils.isEmpty(myUserID)) return;
            mCursor = SportteamLoader.simpleQueryMyEventsAndEventsParticipation(mContext, myUserID);
        }

        @Override
        public void onDestroy() {
            if (mCursor != null) mCursor.close();
        }

        @Override
        public int getCount() {
            if (mCursor == null) return 0;
            else return mCursor.getCount();
        }

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
