package com.usal.jorgeav.sportapp.events;

import com.usal.jorgeav.sportapp.data.Event;

import java.util.List;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class EventsContract {

    public interface Presenter {
        void loadEvents();
    }

    public interface View {
        void showEvents(List<Event> events);
    }
}
