package com.usal.jorgeav.sportapp.eventdetail;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public abstract class DetailEventContract {

    public interface View {
        void showEventId(String id);
        void showEventSport(String sport);
        void showEventPlace(String place);
        void showEventDate(String date);
        void showEventOwner(String time);
        void showEventTotalPlayers(int totalPlayers);
        void showEventEmptyPlayers(int emptyPlayers);

    }

    public interface Presenter {
        void openEvent();
    }
}
