package com.usal.jorgeav.sportapp.searchevent.advancedsearch;

import android.content.Context;

abstract class SearchEventsContract {

    public interface Presenter {
        boolean validateData(Long dateFrom, Long dateTo, int totalFrom, int totalTo,
                             int emptyFrom, int emptyTo);
    }

    public interface View {
        Context getActivityContext();
    }
}
