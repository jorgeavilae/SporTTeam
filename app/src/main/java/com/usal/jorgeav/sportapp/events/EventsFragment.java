package com.usal.jorgeav.sportapp.events;

import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.calendarevent.MyCalendarEvent;
import com.usal.jorgeav.sportapp.data.calendarevent.MyCalendarEventList;
import com.usal.jorgeav.sportapp.data.calendarevent.MyEventRenderer;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;
import com.usal.jorgeav.sportapp.events.eventrequest.EventRequestsFragment;
import com.usal.jorgeav.sportapp.sportselection.SelectSportFragment;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar la colección de partidos en los que participa el usuario.
 * <p>
 * Se encarga de inicializar los componentes de la interfaz para mostrar la colección en un
 * {@link AgendaCalendarView}. También permite viajar al Fragmento en el que se muestran los
 * detalles de los partidos, al Fragmentos donde se listan las peticiones de participación enviadas
 * por el usuario actual, y al Fragmento utilizado para crear un partido nuevo.
 * <p>
 * En el {@link AgendaCalendarView} se emplazan los partidos contenidos en una variable de tipo
 * {@link MyCalendarEventList}. Estos partidos vienen en un {@link Cursor} del Proveedor de
 * Contenido y deben ser transformados a {@link MyCalendarEvent} para que puedan ser mostrados en
 * la interfaz con la ayuda de {@link MyEventRenderer}.
 * <p>
 * Implementa la interfaz {@link EventsContract.View} para la comunicación con esta clase y la
 * interfaz {@link CalendarPickerController} para manejar las pulsaciones sobre el calendario y los
 * partidos contenidos en él.
 *
 * @see <a href= "https://github.com/Tibolte/AgendaCalendarView/tree/master/agendacalendarview/src/main/java/com/github/tibolte/agendacalendarview">
 * AgendaCalendarView implementation (Github)</a>
 */
public class EventsFragment extends BaseFragment implements
        EventsContract.View,
        CalendarPickerController {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = EventsFragment.class.getSimpleName();

    /**
     * Presentador correspondiente a esta Vista
     */
    EventsContract.Presenter mEventsPresenter;

    /**
     * Referencia al calendario de la interfaz donde se van a emplazar los partidos
     *
     * @see <a href= "https://github.com/Tibolte/AgendaCalendarView/tree/master/agendacalendarview/src/main/java/com/github/tibolte/agendacalendarview">
     * AgendaCalendarView implementation (Github)</a>
     */
    @BindView(R.id.agenda_calendar_view)
    AgendaCalendarView eventsAgendaCalendarView;
    /**
     * Mantiene una lista con el conjunto de partidos en formato {@link MyCalendarEvent}
     */
    MyCalendarEventList mEventList;

    /**
     * Constructor sin argumentos
     */
    public EventsFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @return una nueva instancia de EventsFragment
     */
    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    /**
     * Inicializa el Presentador correspondiente a esta vista y la variable que mantendrá la lista
     * de partidos.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mEventsPresenter = new EventsPresenter(this);
        mEventList = new MyCalendarEventList(null);
    }

    /**
     * Inicializa el contenido del menú de opciones de la esquina superior derecha de la pantalla
     *
     * @param menu menú de opciones donde se van a emplazar los elementos.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_events_calendar, menu);
    }

    /**
     * Invocado cuando un elemento del menú es pulsado. En este caso se encarga de iniciar el
     * proceso de creación de un nuevo partido o de mostrar el Fragmento con las peticiones de
     * participación enviadas.
     *
     * @param item elemento del menú pulsado
     * @return true si se aceptó la pulsación, false en otro caso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        hideSoftKeyboard();
        if (item.getItemId() == R.id.action_new_event) {
            Fragment fragment = SelectSportFragment.newInstance();
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        } else if (item.getItemId() == R.id.action_event_requests) {
            Fragment fragment = EventRequestsFragment.newInstance();
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        }
        return false;
    }

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz con la ayuda de
     * ButterKnife. Establece la configuración inicial del calendario.
     *
     * @param inflater           utilizado para inflar el archivo de layout
     * @param container          contenedor donde se va a incluir la interfaz o null
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     * @return la vista de la interfaz inicializada
     * @see <a href= "http://jakewharton.github.io/butterknife/">ButterKnife</a>
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this, root);

        initCalendar();
        return root;
    }

    /**
     * Establece la configuración del calendario con los partidos contenidos en la variable global
     * {@link #mEventList}. Busca los partidos cuyas fechas sean más baja y más alta para establecer
     * las fechas límite que mostrará el calendario, teniendo en cuanta la fecha actual. Luego
     * inicia el calendario con la lista de partidos {@link MyCalendarEvent}s y establece la clase
     * que los emplazará los datos en cada una de las celdas {@link MyEventRenderer}.
     */
    private void initCalendar() {
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        Long minDateInMillis = mEventList.getMinDateInMillis();
        Long maxDateInMillis = mEventList.getMaxDateInMillis();
        Long currentDateInMillis = System.currentTimeMillis();

        if (minDateInMillis != null && minDateInMillis < currentDateInMillis)
            minDate.setTimeInMillis(minDateInMillis);
        minDate.add(Calendar.MONTH, -2);

        if (maxDateInMillis != null && maxDateInMillis > currentDateInMillis)
            maxDate.setTimeInMillis(maxDateInMillis);
        maxDate.add(Calendar.MONTH, 2);

        // Init is the only way to pass events to eventsCalendar
        eventsAgendaCalendarView.init(mEventList.getAsCalendarEventList(),
                minDate, maxDate, Locale.getDefault(), this);
        eventsAgendaCalendarView.addEventRenderer(new MyEventRenderer());
    }

    /**
     * Al finalizar el proceso de creación de la Actividad contenedora, se invoca este método que
     * establece un título para la barra superior y la acción que debe realizar: mostrar el menú
     * lateral de navegación.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.events), this);
        mNavigationDrawerManagementListener.setToolbarAsNav();
    }

    /**
     * Ordena al Presentador que inicie el proceso de carga de los partidos en la base de datos.
     */
    @Override
    public void onStart() {
        super.onStart();
        mEventsPresenter.loadEvents(getLoaderManager(), getArguments());
    }

    /**
     * Borra los partidos almacenados en la lista de {@link #mEventList} para que no se guarden en
     * el estado del Fragmento. Son recuperados inmediatamente al volver a mostrar el Fragmento por
     * estar usando el mismo Loader.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mEventList != null)
            mEventList.clear();
    }

    /**
     * Transforma los partidos contenidos en el {@link Cursor} en partidos con el formato
     * {@link MyCalendarEvent} con la ayuda de
     * {@link UtilesContentProvider#cursorToMultipleCalendarEvent(Cursor, int)}. A continuación,
     * los guardar en la lista de {@link #mEventList}. Luego invoca {@link #initCalendar()} para
     * reiniciar el calendario con la nueva lista de partidos.
     *
     * @param cursor partidos obtenidos en la consulta a la base de datos
     */
    @Override
    public void showCalendarEvents(Cursor cursor) {
        mEventList.replaceEvents(UtilesContentProvider.cursorToMultipleCalendarEvent(cursor,
                ContextCompat.getColor(getActivity(), R.color.colorLighter)));

        initCalendar();
        showContent();
    }

    /**
     * No implementado, no es necesario
     *
     * @param dayItem día pulsado
     */
    @Override
    public void onDaySelected(DayItem dayItem) {
    }

    /**
     * Invocado cuando se pulsa sobre uno de los partidos del calendario. Extrae el identificador
     * del partido pulsado y lo utiliza para crear y mostrar el Fragmento en el que se especifican
     * los detalles del partido.
     *
     * @param event partido del calendario pulsado
     */
    @Override
    public void onEventSelected(CalendarEvent event) {
        if (event instanceof MyCalendarEvent) {
            MyCalendarEvent myCalendarEvent = mEventList.getItemAtPosition((int) event.getId());
            Fragment newFragment = DetailEventFragment.newInstance(myCalendarEvent.getEvent_id());
            mFragmentManagementListener.initFragment(newFragment, true);
        }
    }

    /**
     * No implementado, no es necesario
     *
     * @param calendar fecha a la que se ha desplazado
     */
    @Override
    public void onScrollToDate(Calendar calendar) {
    }
}
