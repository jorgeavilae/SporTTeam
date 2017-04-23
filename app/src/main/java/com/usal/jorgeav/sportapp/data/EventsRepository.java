package com.usal.jorgeav.sportapp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class EventsRepository {
    private ArrayList<Event> mDataset;

    public EventsRepository() {
    }

    public List<Event> getDataset() {
        if (this.mDataset == null) {
            mDataset = new ArrayList<>();
            loadEvents();
        }
        return mDataset;
    }

    public void loadEvents(/*query arguments*/) {
        ArrayList<Event> eventsLoaded = new ArrayList<>();
        eventsLoaded.add(new Event("Sport", "Place", "Fe/cha/1992", "Ho:ra", 10));
        eventsLoaded.add(new Event("Sport", "Place", "Fe/cha/1992", "Ho:ra", 10));
        eventsLoaded.add(new Event("Sport", "Place", "Fe/cha/1992", "Ho:ra", 10));
        eventsLoaded.add(new Event("Sport", "Place", "Fe/cha/1992", "Ho:ra", 10));
        eventsLoaded.add(new Event("Sport", "Place", "Fe/cha/1992", "Ho:ra", 10));
        eventsLoaded.add(new Event("Sport", "Place", "Fe/cha/1992", "Ho:ra", 10));
        eventsLoaded.add(new Event("Sport", "Place", "Fe/cha/1992", "Ho:ra", 10));
        eventsLoaded.add(new Event("Sport", "Place", "Fe/cha/1992", "Ho:ra", 10));
        eventsLoaded.add(new Event("Sport", "Place", "Fe/cha/1992", "Ho:ra", 10));
        eventsLoaded.add(new Event("Sport", "Place", "Fe/cha/1992", "Ho:ra", 10));

        mDataset.addAll(eventsLoaded);
    }
}
