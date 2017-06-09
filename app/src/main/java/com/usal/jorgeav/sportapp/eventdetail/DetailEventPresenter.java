package com.usal.jorgeav.sportapp.eventdetail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.FirebaseDatabaseActions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public class DetailEventPresenter implements DetailEventContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = DetailEventPresenter.class.getSimpleName();

    DetailEventContract.View mView;

    public DetailEventPresenter(@NonNull DetailEventContract.View view) {
        this.mView = view;
    }

    @Override
    public void openEvent(LoaderManager loaderManager, Bundle b) {
        loaderManager.initLoader(SportteamLoader.LOADER_EVENT_ID, b, this);
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String eventId = args.getString(DetailEventFragment.BUNDLE_EVENT_ID);
        switch (id) {
            case SportteamLoader.LOADER_EVENT_ID:
                return SportteamLoader
                        .cursorLoaderOneEvent(mView.getActivityContext(), eventId);
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                return SportteamLoader
                        .cursorLoaderEventParticipants(mView.getActivityContext(), eventId, true);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_EVENT_ID:
                showEventDetails(data);
                break;
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                mView.showParticipants(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_EVENT_ID:
                showEventDetails(null);
                break;
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                mView.showParticipants(null);
                break;
        }
    }

    private void showEventDetails(Cursor data) {
        if (data != null && data.moveToFirst()) {
            mView.showEventId(data.getString(SportteamContract.EventEntry.COLUMN_EVENT_ID));
            mView.showEventSport(data.getString(SportteamContract.EventEntry.COLUMN_SPORT));
            mView.showEventPlace(data.getString(SportteamContract.EventEntry.COLUMN_FIELD));
            mView.showEventDate(Utiles.millisToDateTimeString(data.getLong(SportteamContract.EventEntry.COLUMN_DATE)));
            mView.showEventOwner(data.getString(SportteamContract.EventEntry.COLUMN_OWNER));
            mView.showEventTotalPlayers(data.getInt(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS));
            mView.showEventEmptyPlayers(data.getInt(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS));
        } else {
            mView.showEventId("");
            mView.showEventSport("");
            mView.showEventPlace("");
            mView.showEventDate("");
            mView.showEventOwner("");
            mView.showEventTotalPlayers(-1);
            mView.showEventEmptyPlayers(-1);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RELATION_TYPE_ERROR, RELATION_TYPE_NONE, RELATION_TYPE_OWNER,
            RELATION_TYPE_I_SEND_REQUEST, RELATION_TYPE_I_RECEIVE_INVITATION,
            RELATION_TYPE_ASSISTANT, RELATION_TYPE_BLOCKED})
    @interface EventRelationType {}
    static final int RELATION_TYPE_ERROR = -1;
    static final int RELATION_TYPE_NONE = 0;
    static final int RELATION_TYPE_OWNER = 1;
    static final int RELATION_TYPE_I_SEND_REQUEST = 2;
    static final int RELATION_TYPE_I_RECEIVE_INVITATION = 3;
    static final int RELATION_TYPE_ASSISTANT = 4;
    static final int RELATION_TYPE_BLOCKED = 5;
    @Override
    @EventRelationType
    public int getRelationTypeBetweenThisEventAndI() {
        try {
            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //Owner?
            Cursor cursorOwner = mView.getActivityContext().getContentResolver().query(
                    SportteamContract.EventEntry.CONTENT_EVENT_URI,
                    SportteamContract.EventEntry.EVENT_COLUMNS,
                    SportteamContract.EventEntry.EVENT_ID + " = ? AND " + SportteamContract.EventEntry.OWNER + " = ?",
                    new String[]{mView.getEventID(), myUid},
                    null);
            if (cursorOwner != null && cursorOwner.getCount() > 0) {
                cursorOwner.close();
                return RELATION_TYPE_OWNER;
            }

            //I have received an Invitation?
            Cursor cursorReceiver = mView.getActivityContext().getContentResolver().query(
                    SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI,
                    SportteamContract.EventsInvitationEntry.EVENT_INVITATIONS_COLUMNS,
                    SportteamContract.EventsInvitationEntry.EVENT_ID + " = ?",
                    new String[]{mView.getEventID()},
                    null);
            if (cursorReceiver != null && cursorReceiver.getCount() > 0) {
                cursorReceiver.close();
                return RELATION_TYPE_I_RECEIVE_INVITATION;
            }

            //I have sent a EventRequest?
            Cursor cursorSender = mView.getActivityContext().getContentResolver().query(
                    SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI,
                    SportteamContract.EventRequestsEntry.EVENTS_REQUESTS_COLUMNS,
                    SportteamContract.EventRequestsEntry.SENDER_ID + " = ? AND " + SportteamContract.EventRequestsEntry.EVENT_ID + " = ?",
                    new String[]{myUid, mView.getEventID()},
                    null);
            if (cursorSender != null && cursorSender.getCount() > 0) {
                cursorSender.close();
                return RELATION_TYPE_I_SEND_REQUEST;
            }

            //I assist
            Cursor cursorAssist = mView.getActivityContext().getContentResolver().query(
                    SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                    SportteamContract.EventsParticipationEntry.EVENTS_PARTICIPATION_COLUMNS,
                    SportteamContract.EventsParticipationEntry.EVENT_ID + " = ? AND "
                            + SportteamContract.EventsParticipationEntry.USER_ID + " = ? AND "
                            + SportteamContract.EventsParticipationEntry.PARTICIPATES + " = ?",
                    new String[]{mView.getEventID(), myUid, String.valueOf(1)},
                    null);
            if (cursorAssist != null && cursorAssist.getCount() > 0) {
                cursorAssist.close();
                return RELATION_TYPE_ASSISTANT;
            }

            //I don't assist
            Cursor cursorNotAssist = mView.getActivityContext().getContentResolver().query(
                    SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                    SportteamContract.EventsParticipationEntry.EVENTS_PARTICIPATION_COLUMNS,
                    SportteamContract.EventsParticipationEntry.EVENT_ID + " = ? AND "
                            + SportteamContract.EventsParticipationEntry.USER_ID + " = ? AND "
                            + SportteamContract.EventsParticipationEntry.PARTICIPATES + " = ?",
                    new String[]{mView.getEventID(), myUid, String.valueOf(0)},
                    null);
            if (cursorNotAssist != null && cursorNotAssist.getCount() > 0) {
                cursorNotAssist.close();
                return RELATION_TYPE_BLOCKED;
            }

            //No relation
            return RELATION_TYPE_NONE;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return RELATION_TYPE_ERROR;
        }
    }

    @Override
    public void sendEventRequest(String eventId) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!TextUtils.isEmpty(eventId))
            FirebaseDatabaseActions.sendEventRequest(myUid, eventId);
    }

    @Override
    public void cancelEventRequest(String eventId) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!TextUtils.isEmpty(eventId))
            FirebaseDatabaseActions.cancelEventRequest(myUid, eventId);
    }

    @Override
    public void acceptEventInvitation(String eventId) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!TextUtils.isEmpty(eventId))
            FirebaseDatabaseActions.acceptEventInvitation(myUid, eventId);
    }

    @Override
    public void declineEventInvitation(String eventId) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!TextUtils.isEmpty(eventId))
            FirebaseDatabaseActions.declineEventInvitation(myUid, eventId);
    }

    @Override
    public void quitEvent(String eventId) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!TextUtils.isEmpty(eventId))
            FirebaseDatabaseActions.quitEvent(myUid, eventId);
    }
}
