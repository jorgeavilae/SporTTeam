package com.usal.jorgeav.sportapp.mainactivities;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.MenuItem;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.searchevent.EventsMapFragment;
import com.usal.jorgeav.sportapp.searchevent.advancedsearch.SearchEventsFragment;

/**
 * Actividad principal que hereda de {@link BaseActivity} y que aloja todos los Fragmentos
 * relacionados con la búsqueda de partidos. Actúa como puente entre los Fragmentos, para
 * transmitir los filtros establecidos en {@link SearchEventsFragment} para las búsquedas de
 * partidos realizadas en {@link EventsMapFragment}
 */
public class SearchEventsActivity extends BaseActivity
        implements SearchEventsFragment.OnSearchEventFilter {
    /**
     * Nombre de la clase
     */
    public static final String TAG = SearchEventsActivity.class.getSimpleName();

    /**
     * Clave para mantener en rotaciones del dispositivo, en el {@link Bundle} de estado,
     * el filtro del deporte seleccionado.
     */
    public static final String INSTANCE_SPORT_SELECTED = "INSTANCE_SPORT_SELECTED";
    /**
     * Deporte seleccionado para el filtro
     */
    public String mSportId;
    /**
     * Clave para mantener en rotaciones del dispositivo, en el {@link Bundle} de estado,
     * el filtro de la fecha inferior del periodo de fechas seleccionado.
     */
    public static final String INSTANCE_DATE_FROM_SELECTED = "INSTANCE_DATE_FROM_SELECTED";
    /**
     * Fecha inferior del periodo de fechas seleccionado para el filtro
     */
    public Long mDateFrom;
    /**
     * Clave para mantener en rotaciones del dispositivo, en el {@link Bundle} de estado,
     * el filtro de la fecha superior del periodo de fechas seleccionado.
     */
    public static final String INSTANCE_DATE_TO_SELECTED = "INSTANCE_DATE_TO_SELECTED";
    /**
     * Fecha superior del periodo de fechas seleccionado para el filtro
     */
    public Long mDateTo;
    /**
     * Clave para mantener en rotaciones del dispositivo, en el {@link Bundle} de estado,
     * el filtro de limite inferior de jugadores totales seleccionado.
     */
    public static final String INSTANCE_TOTAL_FROM_SELECTED = "INSTANCE_TOTAL_FROM_SELECTED";
    /**
     * Limite inferior de jugadores totales seleccionado para el filtro
     */
    public Integer mTotalFrom;
    /**
     * Clave para mantener en rotaciones del dispositivo, en el {@link Bundle} de estado,
     * el filtro de limite superior de jugadores totales seleccionado.
     */
    public static final String INSTANCE_TOTAL_TO_SELECTED = "INSTANCE_TOTAL_TO_SELECTED";
    /**
     * Limite superior de jugadores totales seleccionado para el filtro
     */
    public Integer mTotalTo;
    /**
     * Clave para mantener en rotaciones del dispositivo, en el {@link Bundle} de estado,
     * el filtro de limite inferior de puestos vacantes seleccionado.
     */
    public static final String INSTANCE_EMPTY_FROM_SELECTED = "INSTANCE_EMPTY_FROM_SELECTED";
    /**
     * Limite inferior de puestos vacantes seleccionado para el filtro
     */
    public Integer mEmptyFrom;
    /**
     * Clave para mantener en rotaciones del dispositivo, en el {@link Bundle} de estado,
     * el filtro de limite superior de puestos vacantes seleccionado.
     */
    public static final String INSTANCE_EMPTY_TO_SELECTED = "INSTANCE_EMPTY_TO_SELECTED";
    /**
     * Limite superior de puestos vacantes seleccionado para el filtro
     */
    public Integer mEmptyTo;

    /**
     * Crea el Fragmento principal que debe mostrar el mapa con los partidos encontrados.
     */
    @Override
    public void startMainFragment() {
        initFragment(EventsMapFragment.newInstance(), false);

        mNavigationView.setCheckedItem(R.id.nav_search_events);
    }

    /**
     * Comprueba que la entrada pulsada del menú lateral de navegación no es la
     * correspondiente a esta Actividad, en cuyo caso ignora la pulsación. Si no lo es,
     * invoca el mismo método de la superclase {@link BaseActivity#onNavigationItemSelected(MenuItem)}
     *
     * @param item elemento del menú pulsado
     * @return valor de {@link BaseActivity#onNavigationItemSelected(MenuItem)} o false si es
     * la misma entrada
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return item.getItemId() != R.id.nav_search_events && super.onNavigationItemSelected(item);
    }

    /**
     * Se invoca este método al destruir la Actividad. Guarda los filtros si los hubiera.
     *
     * @param outState Bundle para guardar el estado de la Actividad
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSportId != null)
            outState.putString(INSTANCE_SPORT_SELECTED, mSportId);

        if (mDateFrom != null)
            outState.putLong(INSTANCE_DATE_FROM_SELECTED, mDateFrom);
        if (mDateTo != null)
            outState.putLong(INSTANCE_DATE_TO_SELECTED, mDateTo);

        if (mTotalFrom != null)
            outState.putInt(INSTANCE_TOTAL_FROM_SELECTED, mTotalFrom);
        if (mTotalTo != null)
            outState.putInt(INSTANCE_TOTAL_TO_SELECTED, mTotalTo);

        if (mEmptyFrom != null)
            outState.putInt(INSTANCE_EMPTY_FROM_SELECTED, mEmptyFrom);
        if (mEmptyTo != null)
            outState.putInt(INSTANCE_EMPTY_TO_SELECTED, mEmptyTo);
    }

    /**
     * Se ejecuta en la recreación de la Actividad, después de {@link #onStart()}. Sólo se
     * ejecuta si <code>savedInstanceState != null</code>. Es el mismo {@link Bundle} de
     * {@link #onCreate(Bundle)}. Usado para extraer los filtros si los hubiera.
     *
     * @param savedInstanceState Bundle para extraer el estado de la Actividad
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(INSTANCE_SPORT_SELECTED))
            mSportId = savedInstanceState.getString(INSTANCE_SPORT_SELECTED);

        if (savedInstanceState.containsKey(INSTANCE_DATE_FROM_SELECTED))
            mDateFrom = savedInstanceState.getLong(INSTANCE_DATE_FROM_SELECTED);
        if (savedInstanceState.containsKey(INSTANCE_DATE_TO_SELECTED))
            mDateTo = savedInstanceState.getLong(INSTANCE_DATE_TO_SELECTED);

        if (savedInstanceState.containsKey(INSTANCE_TOTAL_FROM_SELECTED))
            mTotalFrom = savedInstanceState.getInt(INSTANCE_TOTAL_FROM_SELECTED);
        if (savedInstanceState.containsKey(INSTANCE_TOTAL_TO_SELECTED))
            mTotalTo = savedInstanceState.getInt(INSTANCE_TOTAL_TO_SELECTED);

        if (savedInstanceState.containsKey(INSTANCE_EMPTY_FROM_SELECTED))
            mEmptyFrom = savedInstanceState.getInt(INSTANCE_EMPTY_FROM_SELECTED);
        if (savedInstanceState.containsKey(INSTANCE_EMPTY_TO_SELECTED))
            mEmptyTo = savedInstanceState.getInt(INSTANCE_EMPTY_TO_SELECTED);
    }

    /**
     * Método invocado para recuperar los filtros escogidos para la búsqueda y finalizar
     * el Fragmento {@link SearchEventsFragment}.
     * Pertenece a la interfaz {@link SearchEventsFragment.OnSearchEventFilter}
     *
     * @param sportId   deporte
     * @param dateFrom  limite inferior del periodo de fechas
     * @param dateTo    limite superior del periodo de fechas
     * @param totalFrom limite inferior de jugadores totales
     * @param totalTo   limite superior de jugadores totales
     * @param emptyFrom limite inferior de puestos vacantes
     * @param emptyTo   limite superior de puestos vacantes
     */
    @Override
    public void onFilterSet(String sportId, Long dateFrom, Long dateTo, int totalFrom,
                            int totalTo, int emptyFrom, int emptyTo) {
        mSportId = sportId;
        mDateFrom = dateFrom;
        mDateTo = dateTo;
        mTotalFrom = totalFrom;
        mTotalTo = totalTo;
        mEmptyFrom = emptyFrom;
        mEmptyTo = emptyTo;

        onBackPressed();
    }
}
