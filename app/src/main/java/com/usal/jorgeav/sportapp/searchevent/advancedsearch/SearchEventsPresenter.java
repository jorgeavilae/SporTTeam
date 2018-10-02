package com.usal.jorgeav.sportapp.searchevent.advancedsearch;

import android.text.format.DateUtils;
import android.widget.Toast;

import com.usal.jorgeav.sportapp.R;

/**
 * Presentador utilizado para establecer los filtros de la búsqueda de partidos. Aquí se validan
 * todos los parámetros del filtro introducidos en la Vista {@link SearchEventsContract.View}.
 * Implementa la interfaz {@link SearchEventsContract.Presenter} para la comunicación con esta clase.
 */
class SearchEventsPresenter implements
        SearchEventsContract.Presenter {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = SearchEventsPresenter.class.getSimpleName();

    /**
     * Vista correspondiente a este Presentador
     */
    private SearchEventsContract.View mSearchEventsView;

    /**
     * Constructor
     *
     * @param mSearchEventsView Vista correspondiente a este Presentador
     */
    SearchEventsPresenter(SearchEventsContract.View mSearchEventsView) {
        this.mSearchEventsView = mSearchEventsView;
    }

    /**
     * Valida los parámetros indicados, que son los que ha introducido el usuario a través del
     * Fragmento. Muestra un {@link Toast} indicando el error, en caso de que lo haya.
     *
     * @param dateFrom  limite inferior del período de fechas
     * @param dateTo    límite superior del período de fechas
     * @param totalFrom límite inferior del rango de puestos totales
     * @param totalTo   límite superior del rango de puestos totales
     * @param emptyFrom límite inferior del rango de puestos vacantes
     * @param emptyTo   límite superior del rango de puestos vacantes
     * @return true si son válidos, false en caso contrario
     */
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
        if (totalFrom < emptyTo && totalFrom > 0) {
            Toast.makeText(mSearchEventsView.getActivityContext(), R.string.toast_players_relation_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Comprueba que las fechas indicadas son correctas
     *
     * @param dateFrom limite inferior de fechas, en milisegundos
     * @param dateTo   limite superior de fechas, en milisegundos
     * @return true si las fechas son correctas, false en caso contrario.
     */
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

    /**
     * Comprueba que las puestos totales indicados son correctas
     *
     * @param totalFrom limite inferior de puestos totales
     * @param totalTo   limite superior de puestos totales
     * @return true si las fechas son correctas, false en caso contrario.
     */
    private boolean isTotalPlayersCorrect(int totalFrom, int totalTo) {
        return !(totalFrom > -1 && totalTo > -1 && totalFrom > totalTo);
    }

    /**
     * Comprueba que las puestos vacantes indicados son correctas
     *
     * @param emptyFrom limite inferior de puestos vacantes
     * @param emptyTo   limite superior de puestos vacantes
     * @return true si las fechas son correctas, false en caso contrario.
     */
    private boolean isEmptyPlayersCorrect(int emptyFrom, int emptyTo) {
        return !(emptyFrom > -1 && emptyTo > -1 && emptyFrom > emptyTo);

    }
}
