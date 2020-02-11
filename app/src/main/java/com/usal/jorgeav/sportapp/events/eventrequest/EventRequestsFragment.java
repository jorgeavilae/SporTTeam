package com.usal.jorgeav.sportapp.events.eventrequest;


import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.EventsAdapter;
import com.usal.jorgeav.sportapp.eventdetail.DetailEventFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar la colección de peticiones de participación sin respuesta
 * enviadas por el usuario actual.
 * <p>
 * Se encarga de inicializar los componentes de la interfaz para mostrar la colección con la ayuda
 * de {@link EventsAdapter} ya que cada petición será representada por el partido al que fue
 * destinada.
 * <p>
 * Implementa la interfaz {@link EventRequestsContract.View} para la comunicación con esta clase
 * y la interfaz {@link EventsAdapter.OnEventItemClickListener} para manejar la pulsación sobre cada
 * uno de los partidos objetivos de la petición.
 */
public class EventRequestsFragment extends BaseFragment implements
        EventRequestsContract.View,
        EventsAdapter.OnEventItemClickListener {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = EventRequestsFragment.class.getSimpleName();

    /**
     * Presentador correspondiente a esta Vista
     */
    EventRequestsContract.Presenter mEventRequestsPresenter;

    /**
     * Adaptador para manejar y emplazar los datos de los partidos destinatarios de las peticiones
     * en cada una de las celdas de la lista
     */
    EventsAdapter mEventsRecyclerAdapter;
    /**
     * Referencia al elemento de la interfaz donde se listan los partidos destinatarios de las
     * peticiones
     */
    @BindView(R.id.recycler_list)
    RecyclerView eventRequestsList;
    /**
     * Referencia al elemento de la interfaz que debe mostrarse en caso de que la consulta de
     * peticiones de participación no arroje ningún resultado.
     */
    @BindView(R.id.list_placeholder)
    ConstraintLayout eventRequestsPlaceholder;

    /**
     * Constructor sin argumentos
     */
    public EventRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @return una nueva instancia de EventRequestsFragment
     */
    public static EventRequestsFragment newInstance() {
        return new EventRequestsFragment();
    }

    /**
     * Inicializa el Presentador correspondiente a esta Vista, y el Adaptador para la colección de
     * partidos.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mEventRequestsPresenter = new EventRequestsPresenter(this);
        mEventsRecyclerAdapter = new EventsAdapter(null, this, Glide.with(this));
    }

    /**
     * Inicializa el contenido del menú de opciones de la esquina superior derecha de la pantalla:
     * lo limpia para no mostrar ninguna opción.
     *
     * @param menu menú de opciones donde se van a emplazar los elementos.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    /**
     * Inicializa y obtiene una referencia a los elementos de la interfaz con la ayuda de
     * ButterKnife. Establece el adaptador creado como adaptador de la lista de la interfaz recién
     * inflada.
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
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, root);

        eventRequestsList.setAdapter(mEventsRecyclerAdapter);
        eventRequestsList.setHasFixedSize(true);
        eventRequestsList.setLayoutManager(new LinearLayoutManager
                (getContext(), LinearLayoutManager.VERTICAL, false));

        return root;
    }

    /**
     * Al finalizar el proceso de creación de la Actividad contenedora, se invoca este método que
     * establece un título para la barra superior y la acción que debe realizar: navegar hacia atrás.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManagementListener.setCurrentDisplayedFragment(
                getString(R.string.action_event_requests), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Ordena al Presentador que inicie el proceso de carga de los partidos destinatarios de
     * peticiones de participación que se encuentren en la base de datos.
     */
    @Override
    public void onStart() {
        super.onStart();
        mEventRequestsPresenter.loadEventRequests(getLoaderManager(), getArguments());
    }

    /**
     * Borra los partidos almacenados en el Adaptador para que no se guarden en el estado del
     * Fragmento. Son recuperados inmediatamente al volver a mostrar el Fragmento por estar
     * usando el mismo Loader.
     */
    @Override
    public void onPause() {
        super.onPause();
        mEventsRecyclerAdapter.replaceData(null);
    }

    /**
     * Establece en el Adaptador los partidos contenidos en el {@link Cursor} y, si no está vacío,
     * muestra la lista; si está vacío, muestra una imagen que lo indica
     *
     * @param cursor peticiones de participación obtenidas en la consulta
     */
    @Override
    public void showEventRequests(Cursor cursor) {
        mEventsRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            eventRequestsList.setVisibility(View.VISIBLE);
            eventRequestsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            eventRequestsList.setVisibility(View.INVISIBLE);
            eventRequestsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Inicia la transición hacia la vista de detalles del partido seleccionado. Desde esa pantalla,
     * el usuario puede cancelar la petición de participación.
     *
     * @param eventId Identificador del partido seleccionado
     */
    @Override
    public void onEventClick(String eventId) {
        Fragment newFragment = DetailEventFragment.newInstance(eventId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
