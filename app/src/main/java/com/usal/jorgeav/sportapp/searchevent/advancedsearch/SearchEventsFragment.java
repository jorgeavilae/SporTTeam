package com.usal.jorgeav.sportapp.searchevent.advancedsearch;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.SportSpinnerAdapter;
import com.usal.jorgeav.sportapp.mainactivities.SearchEventsActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para establecer los filtros de la búsqueda de partidos. Se encarga de
 * inicializar los componentes de edición de la interfaz para que el usuario pueda introducir los
 * parámetros, entre los que se encuentran dos {@link DatePickerDialog} y un botón que muestra un
 * diálogo para escoger deporte.
 * <p>
 * Implementa la interfaz {@link SearchEventsContract.View} para la comunicación con esta clase.
 * También aloja una interfaz {@link OnSearchEventFilter} para comunicar a la Actividad contenedora,
 * a través de ella, los filtros establecidos después de comprobar su validez.
 */
public class SearchEventsFragment extends BaseFragment implements
        SearchEventsContract.View {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = SearchEventsFragment.class.getSimpleName();

    /**
     * Presentador correspondiente a esta Vista
     */
    SearchEventsContract.Presenter mSearchEventsPresenter;

    /**
     * Etiqueta para almacenar en el estado del Fragmento, el deporte seleccionado.
     */
    public static final String BUNDLE_SPORT = "BUNDLE_SPORT";
    /**
     * Identificador del deporte seleccionado.
     */
    String mSportIdSelected = "";

    /**
     * Referencia al botón de la interfaz que se utiliza para establecer el deporte del filtro
     */
    @BindView(R.id.search_events_button)
    Button searchEventsButton;
    /**
     * Referencia a la imagen de la interfaz que se usa para mostrar el icono del deporte
     * seleccionado
     */
    @BindView(R.id.search_events_icon)
    ImageView searchEventsIcon;
    /**
     * Referencia al texto en la interfaz donde se escribe el nombre del deporte seleccionado.
     */
    @BindView(R.id.search_events_sport)
    TextView searchEventsSportName;
    /**
     * Referencia al elemento de la interfaz donde se establece el limite inferior del periodo de
     * fechas
     */
    @BindView(R.id.search_events_date_from)
    EditText searchEventsDateFrom;
    /**
     * Referencia al elemento de la interfaz donde se establece el limite superior del periodo de
     * fechas
     */
    @BindView(R.id.search_events_date_to)
    EditText searchEventsDateTo;
    /**
     * Referencia al elemento de la interfaz donde se establece el limite inferior del rango de
     * puestos totales
     */
    @BindView(R.id.search_events_total_from)
    EditText searchEventsTotalFrom;
    /**
     * Referencia al elemento de la interfaz donde se establece el limite superior del rango de
     * puestos totales
     */
    @BindView(R.id.search_events_total_to)
    EditText searchEventsTotalTo;
    /**
     * Referencia al elemento de la interfaz donde se establece el limite inferior del rango de
     * puestos vacantes
     */
    @BindView(R.id.search_events_empty_from)
    EditText searchEventsEmptyFrom;
    /**
     * Referencia al elemento de la interfaz donde se establece el limite superior del rango de
     * puestos vacantes
     */
    @BindView(R.id.search_events_empty_to)
    EditText searchEventsEmptyTo;

    /**
     * Objeto para establecer las fechas preseleccionadas y los límites de los calendarios mostrados
     * en los diálogos que se utilizan en la selección del rango de fechas del filtro
     */
    Calendar myCalendar;
    /**
     * Diálogo de selección de fecha utilizado en la selección del rango límite inferior
     */
    DatePickerDialog datePickerDialogFrom;
    /**
     * Diálogo de selección de fecha utilizado en la selección del rango límite superior
     */
    DatePickerDialog datePickerDialogTo;

    /**
     * Constructor sin argumentos
     */
    public SearchEventsFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @return una nueva instancia de este Fragmento
     */
    public static Fragment newInstance() {
        return new SearchEventsFragment();
    }

    /**
     * En este método se inicializan el Presentador correspondiente a esta Vista.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mSearchEventsPresenter = new SearchEventsPresenter(this);
    }

    /**
     * Inicializa el contenido del menú de opciones de la esquina superior derecha de la pantalla
     *
     * @param menu menú de opciones donde se van a emplazar los elementos.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_ok, menu);
    }

    /**
     * Invocado cuando un elemento del menú es pulsado. En este caso se encarga validar y enviar a
     * la Actividad contenedora, los filtros introducidos, por medio de {@link #actionOkPressed()}.
     *
     * @param item elemento del menú pulsado
     * @return true si se aceptó la pulsación, false en otro caso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_ok) {
            actionOkPressed();
            return true;
        }
        return false;
    }

    /**
     * Transforma el texto de los elementos de la interfaz a los parámetros aceptados por el
     * método de validación del Presentador. Los comprueba y, si son válidos, los envía a la
     * {@link SearchEventsActivity} por medio de
     * {@link OnSearchEventFilter#onFilterSet(String, Long, Long, int, int, int, int)}
     */
    private void actionOkPressed() {
        Long dateFrom = -1L;
        Long dateTo = -1L;
        if (!TextUtils.isEmpty(searchEventsDateFrom.getText().toString()))
            dateFrom = UtilesTime.stringDateToMillis(searchEventsDateFrom.getText().toString());
        if (!TextUtils.isEmpty(searchEventsDateTo.getText().toString()))
            dateTo = UtilesTime.stringDateToMillis(searchEventsDateTo.getText().toString());

        int totalFrom = -1;
        int totalTo = -1;
        if (!TextUtils.isEmpty(searchEventsTotalFrom.getText().toString()))
            totalFrom = Integer.parseInt(searchEventsTotalFrom.getText().toString());
        if (!TextUtils.isEmpty(searchEventsTotalTo.getText().toString()))
            totalTo = Integer.parseInt(searchEventsTotalTo.getText().toString());

        int emptyFrom = -1;
        int emptyTo = -1;
        if (!TextUtils.isEmpty(searchEventsEmptyFrom.getText().toString()))
            emptyFrom = Integer.parseInt(searchEventsEmptyFrom.getText().toString());
        if (!TextUtils.isEmpty(searchEventsEmptyTo.getText().toString()))
            emptyTo = Integer.parseInt(searchEventsEmptyTo.getText().toString());

        //validate data
        if (mSearchEventsPresenter.validateData(dateFrom, dateTo, totalFrom, totalTo, emptyFrom, emptyTo))
            if (getActivity() instanceof OnSearchEventFilter) {
                ((OnSearchEventFilter) getActivity()).onFilterSet(mSportIdSelected, dateFrom, dateTo, totalFrom, totalTo, emptyFrom, emptyTo);
            } else {
                Log.e(TAG, "onOptionsItemSelected: Activity does not implement OnSearchEventFilter");
            }
    }

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz con la ayuda de
     * ButterKnife. Además, recupera el deporte del estado anterior del Fragmento, establece
     * Listeners para las pulsaciones sobre los elementos de la interfaz y establece los limites de
     * fechas en los {@link DatePickerDialog}
     *
     * @param inflater           utilizado para inflar el archivo de layout
     * @param container          contenedor donde se va a incluir la interfaz o null
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     * @return la vista de la interfaz inicializada
     * @see <a href= "http://jakewharton.github.io/butterknife/">ButterKnife</a>
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search_events, container, false);
        ButterKnife.bind(this, root);

        searchEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPickSportDialog();
            }
        });

        myCalendar = Calendar.getInstance();
        searchEventsDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogFrom = new DatePickerDialog(
                        getActivityContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                searchEventsDateFrom.setText(UtilesTime.millisToDateString(myCalendar.getTimeInMillis()));
                                searchEventsDateTo.setText("");
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialogFrom.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialogFrom.setCanceledOnTouchOutside(true);
                datePickerDialogFrom.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myCalendar.setTimeInMillis(System.currentTimeMillis());
                        searchEventsDateFrom.setText("");
                        searchEventsDateTo.setText("");
                    }
                });
                datePickerDialogFrom.show();
            }
        });

        searchEventsDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogTo = new DatePickerDialog(
                        getActivityContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, monthOfYear);
                                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                searchEventsDateTo.setText(UtilesTime.millisToDateString(c.getTimeInMillis()));
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialogTo.getDatePicker().setMinDate(myCalendar.getTimeInMillis() + 1000 * 60 * 60 * 24);
                datePickerDialogTo.setCanceledOnTouchOutside(true);
                datePickerDialogTo.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(searchEventsDateTo.getText())) // myCalendar has been set
                            myCalendar.setTimeInMillis(datePickerDialogTo.getDatePicker().getMinDate() - 1000 * 60 * 60 * 24);
                        searchEventsDateTo.setText("");
                    }
                });
                datePickerDialogTo.show();
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SPORT))
            setSportSearched(savedInstanceState.getString(BUNDLE_SPORT));

        return root;
    }

    /**
     * Muestra en la interfaz los filtros establecidos en una posible ejecución anterior.
     *
     * @see #displayPreviousFilters()
     */
    @Override
    public void onStart() {
        super.onStart();

        displayPreviousFilters();
    }

    /**
     * Comprueba si había filtros establecidos de una ejecución anterior, y coloca sus valores en
     * sus respectivos elementos de la interfaz.
     */
    private void displayPreviousFilters() {
        if (getActivity() instanceof SearchEventsActivity) {
            SearchEventsActivity searchEventsActivity = (SearchEventsActivity) getActivity();
            if (searchEventsActivity.mSportId != null && !TextUtils.isEmpty(searchEventsActivity.mSportId))
                setSportSearched(searchEventsActivity.mSportId);

            if (searchEventsActivity.mDateFrom != null && searchEventsActivity.mDateFrom > -1)
                searchEventsDateFrom.setText(UtilesTime.millisToDateString(searchEventsActivity.mDateFrom));
            if (searchEventsActivity.mDateTo != null && searchEventsActivity.mDateTo > -1)
                searchEventsDateTo.setText(UtilesTime.millisToDateString(searchEventsActivity.mDateTo));

            if (searchEventsActivity.mTotalFrom != null && searchEventsActivity.mTotalFrom > -1)
                searchEventsTotalFrom.setText(String.format(Locale.getDefault(),
                        "%d", searchEventsActivity.mTotalFrom));
            if (searchEventsActivity.mTotalTo != null && searchEventsActivity.mTotalTo > -1)
                searchEventsTotalTo.setText(String.format(Locale.getDefault(),
                        "%d", searchEventsActivity.mTotalTo));

            if (searchEventsActivity.mEmptyFrom != null && searchEventsActivity.mEmptyFrom > -1)
                searchEventsEmptyFrom.setText(String.format(Locale.getDefault(),
                        "%d", searchEventsActivity.mEmptyFrom));
            if (searchEventsActivity.mEmptyTo != null && searchEventsActivity.mEmptyTo > -1)
                searchEventsEmptyTo.setText(String.format(Locale.getDefault(),
                        "%d", searchEventsActivity.mEmptyTo));
        }
    }

    /**
     * Crea y muestra un cuadro de diálogo con la lista de deportes. Si el usuario seleccionad uno,
     * se establece como filtro con ayuda de {@link #setSportSearched(String)}. También puede borrar
     * su selección con {@link #unsetSportSearched()}
     */
    private void createPickSportDialog() {
        ArrayList<String> sportsResources = new ArrayList<>(
                Arrays.asList(getResources().getStringArray(R.array.sport_id_values)));
        final SportSpinnerAdapter listAdapter = new SportSpinnerAdapter(getActivityContext(),
                R.layout.sport_spinner_item, sportsResources);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_sport)
                .setAdapter(listAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sportId = (String) listAdapter.getItem(i);
                        setSportSearched(sportId);
                    }
                })
                .setCancelable(true)
                .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unsetSportSearched();
                    }
                });
        builder.create().show();
    }

    /**
     * Establece el deporte indicado como deporte seleccionado para el filtro. Muestra en la
     * interfaz su icono y su nombre.
     *
     * @param sportId identificador del deporte seleccionado
     */
    private void setSportSearched(String sportId) {
        mSportIdSelected = sportId;

        int sportStringResource = getResources()
                .getIdentifier(sportId, "string", getActivityContext().getPackageName());
        searchEventsSportName.setText(getString(sportStringResource));

        int sportDrawableResource = Utiles.getSportIconFromResource(sportId);
        searchEventsIcon.setVisibility(View.VISIBLE);
        Glide.with(this).load(sportDrawableResource).into(searchEventsIcon);
    }

    /**
     * Borra cualquier deporte seleccionado previamente para el filtro. Borra de la interfaz su
     * icono y su nombre.
     */
    private void unsetSportSearched() {
        mSportIdSelected = "";

        searchEventsSportName.setText(null);
        searchEventsIcon.setVisibility(View.INVISIBLE);
    }

    /**
     * Al finalizar el proceso de creación de la Actividad contenedora, se invoca este método que
     * establece un título para la barra superior y la acción que debe realizar: navegar hacia
     * atrás.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.search_events), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
        mFragmentManagementListener.showContent();
    }

    /**
     * Guarda el deporte seleccionado en el estado del Fragmento
     *
     * @param outState donde se guarda estado del Fragmento en una posible rotación de la pantalla.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mSportIdSelected))
            outState.putString(BUNDLE_SPORT, mSportIdSelected);
    }

    /**
     * Interfaz que debe implementar la Actividad contenedora para recibir los parámetros del
     * filtro y así poder aplicarlos en un Fragmento diferente
     */
    public interface OnSearchEventFilter {
        /**
         * Se invoca para enviar los filtros escogidos para la búsqueda y finalizar el Fragmento.
         *
         * @param sportId   deporte
         * @param dateFrom  limite inferior del periodo de fechas
         * @param dateTo    limite superior del periodo de fechas
         * @param totalFrom limite inferior de jugadores totales
         * @param totalTo   limite superior de jugadores totales
         * @param emptyFrom limite inferior de puestos vacantes
         * @param emptyTo   limite superior de puestos vacantes
         */
        void onFilterSet(String sportId, Long dateFrom, Long dateTo, int totalFrom, int totalTo,
                         int emptyFrom, int emptyTo);
    }
}