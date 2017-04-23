package com.usal.jorgeav.sportapp.events;

import com.usal.jorgeav.sportapp.data.EventsRepository;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class EventsPresenter implements EventsContract.Presenter {

    EventsContract.View mEventsView;
    EventsRepository mEventsRepository;

    public EventsPresenter(EventsRepository eventsRepository, EventsContract.View eventsView) {
        this.mEventsRepository = eventsRepository;
        this.mEventsView = eventsView;
    }

    @Override
    public void loadEvents() {
        mEventsView.showEvents(mEventsRepository.getDataset());
    }
}
