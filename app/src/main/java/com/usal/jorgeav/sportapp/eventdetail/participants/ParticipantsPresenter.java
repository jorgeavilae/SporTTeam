package com.usal.jorgeav.sportapp.eventdetail.participants;

import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public class ParticipantsPresenter implements ParticipantsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ParticipantsPresenter.class.getSimpleName();

    ParticipantsContract.View mParticipantsView;
    String mOwnerUid = "";

    public ParticipantsPresenter(ParticipantsContract.View mParticipantsView) {
        this.mParticipantsView = mParticipantsView;
    }

    @Override
    public void quitEvent(String userId, String eventId, boolean deleteSimulatedParticipant) {
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(eventId))
            FirebaseActions.quitEvent(userId, eventId, deleteSimulatedParticipant);
    }

    @Override
    public void deleteSimulatedUser(String simulatedUserId, String eventId) {
        if (!TextUtils.isEmpty(simulatedUserId) && !TextUtils.isEmpty(eventId))
            FirebaseActions.deleteSimulatedParticipant(simulatedUserId, eventId);

    }
    @Override
    public void loadParticipants(LoaderManager loaderManager, Bundle b) {
        if (b != null && b.containsKey(ParticipantsFragment.BUNDLE_OWNER_ID))
            mOwnerUid = b.getString(ParticipantsFragment.BUNDLE_OWNER_ID);
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID, b, this);
    }

    @Override
    public void loadSimulatedParticipants(LoaderManager loaderManager, Bundle b) {
        // Load Event to insert Simulated Participants into
        // ContentProvider and display recently added ones
        if (b != null && b.containsKey(ParticipantsFragment.BUNDLE_EVENT_ID)) {
            String eventId = b.getString(ParticipantsFragment.BUNDLE_EVENT_ID);
            if (eventId != null) FirebaseSync.loadAnEvent(eventId);
        }
        if (b != null && b.containsKey(ParticipantsFragment.BUNDLE_OWNER_ID))
            mOwnerUid = b.getString(ParticipantsFragment.BUNDLE_OWNER_ID);
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String eventId = args.getString(ParticipantsFragment.BUNDLE_EVENT_ID);
        switch (id) {
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                return SportteamLoader
                        .cursorLoaderEventParticipants(mParticipantsView.getActivityContext(), eventId, true);
            case SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID:
                return SportteamLoader
                        .cursorLoaderEventSimulatedParticipants(mParticipantsView.getActivityContext(), eventId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                /* ownerUid should have a value because this loader
                 * is initialized after this value was set in UI
                 */
                if (mOwnerUid != null && !TextUtils.isEmpty(mOwnerUid)) {
                    Cursor c = addParticipantToCursor(data, mOwnerUid);
                    mParticipantsView.showParticipants(c);
                }
                break;
            case SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID:
                mParticipantsView.showSimulatedParticipants(data);
                break;
        }
    }

    /* https://stackoverflow.com/a/16440093/4235666 */
    private Cursor addParticipantToCursor(Cursor data, String uid) {
        Cursor uidCursor = MyApplication.getAppContext().getContentResolver().query(
                SportteamContract.UserEntry.CONTENT_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.UserEntry.USER_ID + " = ? ",
                new String[]{uid},
                null);
        return new MergeCursor(new Cursor[] {uidCursor, data});
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                mParticipantsView.showParticipants(null);
                break;
            case SportteamLoader.LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID:
                mParticipantsView.showSimulatedParticipants(null);
                break;
        }
    }
}
