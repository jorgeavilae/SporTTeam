package com.usal.jorgeav.sportapp.events.detail;

import android.support.annotation.NonNull;
import android.util.Log;

import com.usal.jorgeav.sportapp.data.Event;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public class DetailEventPresenter implements DetailEventContract.Presenter {
    private static final String TAG = DetailEventPresenter.class.getSimpleName();

    Event mEvent;
    DetailEventContract.View mView;

    public DetailEventPresenter(@NonNull Event event, @NonNull DetailEventContract.View view) {
        Log.d(TAG, "DetailEventPresenter");
        this.mEvent = event;
        this.mView = view;
    }

    @Override
    public void openEvent() {
        mView.showEventId(mEvent.getmId());
        mView.showEventSport(mEvent.getmSport());
        mView.showEventPlace(mEvent.getmPlace());
        mView.showEventDate(mEvent.getmDate());
        mView.showEventTime(mEvent.getmTime());
        mView.showEventTotalPlayers(mEvent.getmTotalPlayers());
        mView.showEventEmptyPlayers(mEvent.getmEmptyPlayers());
    }
}
