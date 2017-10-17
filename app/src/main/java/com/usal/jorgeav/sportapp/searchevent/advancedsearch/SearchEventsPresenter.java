package com.usal.jorgeav.sportapp.searchevent.advancedsearch;

import android.text.format.DateUtils;
import android.widget.Toast;

import com.usal.jorgeav.sportapp.R;

class SearchEventsPresenter implements SearchEventsContract.Presenter {
    @SuppressWarnings("unused")
    private static final String TAG = SearchEventsPresenter.class.getSimpleName();

    private SearchEventsContract.View mSearchEventsView;

    SearchEventsPresenter(SearchEventsContract.View mSearchEventsView) {
        this.mSearchEventsView = mSearchEventsView;
    }

    @Override
    public boolean validateData(Long dateFrom, Long dateTo, int totalFrom, int totalTo,
                                int emptyFrom, int emptyTo) {
        // dateFrom must be at least today and dateTo should be greater than dateFrom or null
        if (!isDateCorrect(dateFrom, dateTo)) {
            Toast.makeText(mSearchEventsView.getActivityContext(), R.string.toast_date_period_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }

        // totalFrom could be null and totalTo should be greater than totalFrom or null
        if (!isTotalPlayersCorrect(totalFrom, totalTo)) {
            Toast.makeText(mSearchEventsView.getActivityContext(), R.string.toast_total_player_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }

        // emptyFrom could be null and emptyTo should be greater than emptyFrom or null
        if (!isEmptyPlayersCorrect(emptyFrom, emptyTo)) {
            Toast.makeText(mSearchEventsView.getActivityContext(), R.string.toast_empty_players_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }

        // totalFrom must be grater than emptyTo
        if (totalFrom < emptyTo && totalFrom > 0 && emptyTo > 0) {
            Toast.makeText(mSearchEventsView.getActivityContext(), R.string.toast_players_relation_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isDateCorrect(Long dateFrom, Long dateTo) {
        if (dateFrom == -1 && dateTo == -1) return true;
        if (dateFrom > 0)
            if (dateTo > 0)
                return (DateUtils.isToday(dateFrom) || System.currentTimeMillis() < dateFrom)
                        && dateFrom <= dateTo;
            else
                return DateUtils.isToday(dateFrom) || System.currentTimeMillis() < dateFrom;
        return false;
    }

    private boolean isTotalPlayersCorrect(int totalFrom, int totalTo) {
        return !(totalFrom > -1 && totalTo > -1 && totalFrom > totalTo);
    }

    private boolean isEmptyPlayersCorrect(int emptyFrom, int emptyTo) {
        return !(emptyFrom > -1 && emptyTo > -1 && emptyFrom > emptyTo);

    }
}
