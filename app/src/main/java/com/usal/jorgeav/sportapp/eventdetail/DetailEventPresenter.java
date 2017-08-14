package com.usal.jorgeav.sportapp.eventdetail;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.Invitation;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.actions.FirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DetailEventPresenter implements DetailEventContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    @SuppressWarnings("unused")
    private static final String TAG = DetailEventPresenter.class.getSimpleName();

    private DetailEventContract.View mView;
    private ContentObserver mContentObserver;

    private String ownerUid = "";
    private Invitation mInvitation = null;

    DetailEventPresenter(@NonNull DetailEventContract.View view) {
        this.mView = view;

        mContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                getRelationTypeBetweenThisEventAndI();
            }
        };
    }

    @Override
    public void openEvent(LoaderManager loaderManager, Bundle b) {
        String eventId = b.getString(DetailEventFragment.BUNDLE_EVENT_ID);
        if (eventId != null) FirebaseSync.loadAnEvent(eventId);
        loaderManager.initLoader(SportteamLoader.LOADER_EVENT_ID, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_EVENT_ID:
                String eventId = args.getString(DetailEventFragment.BUNDLE_EVENT_ID);
                if (eventId == null) return null;
                return SportteamLoader
                        .cursorLoaderOneEvent(mView.getActivityContext(), eventId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        showEventDetails(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        showEventDetails(null);
    }

    private void showEventDetails(Cursor data) {
        if (data != null && data.moveToFirst()) {
            mView.showEventSport(data.getString(SportteamContract.EventEntry.COLUMN_SPORT));

            Field field = UtilesContentProvider.getFieldFromContentProvider(
                    data.getString(SportteamContract.EventEntry.COLUMN_FIELD));
            String address = data.getString(SportteamContract.EventEntry.COLUMN_ADDRESS);
            double latitude = data.getDouble(SportteamContract.EventEntry.COLUMN_FIELD_LATITUDE);
            double longitude = data.getDouble(SportteamContract.EventEntry.COLUMN_FIELD_LONGITUDE);
            LatLng coord = null; if (latitude != 0 && longitude != 0) coord = new LatLng(latitude, longitude);
            mView.showEventField(field, address, coord);

            mView.showEventName(data.getString(SportteamContract.EventEntry.COLUMN_NAME));
            mView.showEventDate(data.getLong(SportteamContract.EventEntry.COLUMN_DATE));
            mView.showEventPlayers(data.getInt(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS),
                    data.getInt(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS));

            ownerUid = data.getString(SportteamContract.EventEntry.COLUMN_OWNER);
            mView.showEventOwner(ownerUid);
        } else {
            ownerUid = null;
            mInvitation = null;
            mView.clearUI();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RELATION_TYPE_ERROR, RELATION_TYPE_NONE, RELATION_TYPE_OWNER,
            RELATION_TYPE_I_SEND_REQUEST, RELATION_TYPE_I_RECEIVE_INVITATION,
            RELATION_TYPE_ASSISTANT, RELATION_TYPE_BLOCKED})
    public @interface EventRelationType {}
    public static final int RELATION_TYPE_ERROR = -1;
    public static final int RELATION_TYPE_NONE = 0;
    public static final int RELATION_TYPE_OWNER = 1; //Event.owner = me
    public static final int RELATION_TYPE_I_SEND_REQUEST = 2; //Request.sender = me
    public static final int RELATION_TYPE_I_RECEIVE_INVITATION = 3; //Invitation.receiver = me
    public static final int RELATION_TYPE_ASSISTANT = 4; //Participation true
    public static final int RELATION_TYPE_BLOCKED = 5; //Participation false
    @Override
    public void getRelationTypeBetweenThisEventAndI() {
        // Do it in AsyncTask to avoid query ContentProvider in UI Thread
        AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    String myUid = Utiles.getCurrentUserId();
                    if (TextUtils.isEmpty(myUid)) return RELATION_TYPE_ERROR;

                    //Owner?
                    Cursor cursorOwner = mView.getActivityContext().getContentResolver().query(
                            SportteamContract.EventEntry.CONTENT_EVENT_URI,
                            new String[]{SportteamContract.EventEntry.EVENT_ID_TABLE_PREFIX},
                            SportteamContract.EventEntry.EVENT_ID + " = ? AND "
                                    + SportteamContract.EventEntry.OWNER + " = ?",
                            new String[]{mView.getEventID(), myUid},
                            null);
                    if (cursorOwner != null) {
                        if(cursorOwner.getCount() > 0) {
                            cursorOwner.close();
                            return RELATION_TYPE_OWNER;
                        }
                        cursorOwner.close();
                    }

                    //I have received an Invitation?
                    Cursor cursorReceiver = mView.getActivityContext().getContentResolver().query(
                            SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI,
                            SportteamContract.EventsInvitationEntry.EVENT_INVITATIONS_COLUMNS,
                            SportteamContract.EventsInvitationEntry.EVENT_ID + " = ? AND "
                                    + SportteamContract.EventsInvitationEntry.RECEIVER_ID + " = ? ",
                            new String[]{mView.getEventID(), myUid},
                            null);
                    if (cursorReceiver != null) {
                        if(cursorReceiver.getCount() > 0 && cursorReceiver.moveToFirst()) {
                            // In this case, store Invitation to accept it or decline it later
                            String sender = cursorReceiver.getString(SportteamContract.EventsInvitationEntry.COLUMN_SENDER_ID);
                            Long date = cursorReceiver.getLong(SportteamContract.EventsInvitationEntry.COLUMN_DATE);
                            mInvitation = new Invitation(sender, myUid, mView.getEventID(), date);

                            cursorReceiver.close();
                            return RELATION_TYPE_I_RECEIVE_INVITATION;
                        }
                        cursorReceiver.close();
                    }

                    //I have sent a EventRequest?
                    Cursor cursorSender = mView.getActivityContext().getContentResolver().query(
                            SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI,
                            new String[]{SportteamContract.EventRequestsEntry.EVENT_ID_TABLE_PREFIX},
                            SportteamContract.EventRequestsEntry.SENDER_ID + " = ? AND "
                                    + SportteamContract.EventRequestsEntry.EVENT_ID + " = ?",
                            new String[]{myUid, mView.getEventID()},
                            null);
                    if (cursorSender != null) {
                        if(cursorSender.getCount() > 0) {
                            cursorSender.close();
                            return RELATION_TYPE_I_SEND_REQUEST;
                        }
                        cursorSender.close();
                    }

                    //I assist
                    Cursor cursorAssist = mView.getActivityContext().getContentResolver().query(
                            SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                            new String[]{SportteamContract.EventsParticipationEntry.EVENT_ID_TABLE_PREFIX},
                            SportteamContract.EventsParticipationEntry.EVENT_ID + " = ? AND "
                                    + SportteamContract.EventsParticipationEntry.USER_ID + " = ? AND "
                                    + SportteamContract.EventsParticipationEntry.PARTICIPATES + " = ?",
                            new String[]{mView.getEventID(), myUid, String.valueOf(1)}, /*1 -> true*/
                            null);
                    if (cursorAssist != null) {
                        if(cursorAssist.getCount() > 0) {
                            cursorAssist.close();
                            return RELATION_TYPE_ASSISTANT;
                        }
                        cursorAssist.close();
                    }

                    //I don't assist
                    Cursor cursorNotAssist = mView.getActivityContext().getContentResolver().query(
                            SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                            new String[]{SportteamContract.EventsParticipationEntry.EVENT_ID_TABLE_PREFIX},
                            SportteamContract.EventsParticipationEntry.EVENT_ID + " = ? AND "
                                    + SportteamContract.EventsParticipationEntry.USER_ID + " = ? AND "
                                    + SportteamContract.EventsParticipationEntry.PARTICIPATES + " = ?",
                            new String[]{mView.getEventID(), myUid, String.valueOf(0)}, /*0 -> false*/
                            null);
                    if (cursorNotAssist != null) {
                        if(cursorNotAssist.getCount() > 0) {
                            cursorNotAssist.close();
                            return RELATION_TYPE_BLOCKED;
                        }
                        cursorNotAssist.close();
                    }

                    //No relation
                    return RELATION_TYPE_NONE;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    return RELATION_TYPE_ERROR;
                }
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                mView.uiSetupForEventRelation(integer);
            }
        };

        task.execute();
    }

    @Override
    public void sendEventRequest(String eventId) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(ownerUid))
            FirebaseActions.sendEventRequest(myUid, eventId, ownerUid);
    }

    @Override
    public void cancelEventRequest(String eventId) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(ownerUid))
            FirebaseActions.cancelEventRequest(myUid, eventId, ownerUid);
    }

    @Override
    public void acceptEventInvitation(String eventId, String sender) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(sender))
            FirebaseActions.acceptEventInvitation(myUid, eventId, sender);
    }

    @Override
    public Invitation getEventInvitation() {
        return mInvitation;
    }

    @Override
    public void declineEventInvitation(String eventId, String sender) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(sender))
            FirebaseActions.declineEventInvitation(myUid, eventId, sender);
    }

    @Override
    public void quitEvent(String eventId, boolean deleteSimulatedParticipant) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId))
            FirebaseActions.quitEvent(myUid, eventId, deleteSimulatedParticipant);
    }

    @Override
    public void deleteEvent(Bundle b) {
        String eventId = b.getString(DetailEventFragment.BUNDLE_EVENT_ID);
        FirebaseActions.deleteEvent(mView.getThis(), eventId);
    }

    @Override
    public void registerUserRelationObserver() {
        // Register observer to listen for changes in event-user relation
        mView.getActivityContext().getContentResolver().registerContentObserver(
                SportteamContract.UserEntry.CONTENT_USER_RELATION_EVENT_URI, false, mContentObserver);
    }

    @Override
    public void unregisterUserRelationObserver() {
        mView.getActivityContext().getContentResolver().unregisterContentObserver(mContentObserver);
    }
}
