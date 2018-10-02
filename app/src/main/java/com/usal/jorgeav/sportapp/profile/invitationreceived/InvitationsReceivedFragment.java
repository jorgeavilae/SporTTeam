package com.usal.jorgeav.sportapp.profile.invitationreceived;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
 * Fragmento utilizado para mostrar la colección de partidos para los que el usuario actual ha
 * recibido una invitación que está pendiente de respuesta. Se encarga de inicializar los
 * componentes de la interfaz para mostrar la colección con la ayuda de {@link EventsAdapter}.
 * <p>
 * Implementa la interfaz {@link InvitationsReceivedContract.View} para la comunicación con esta
 * clase y la interfaz {@link EventsAdapter.OnEventItemClickListener} para manejar la pulsación
 * sobre un partido de la colección.
 */
public class InvitationsReceivedFragment extends BaseFragment implements
        InvitationsReceivedContract.View,
        EventsAdapter.OnEventItemClickListener {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = InvitationsReceivedFragment.class.getSimpleName();

    /**
     * Presentador correspondiente a esta Vista
     */
    InvitationsReceivedContract.Presenter mInvitationsReceivedPresenter;

    /**
     * Adaptador para la colección de partidos que se muestra
     */
    EventsAdapter mEventsRecyclerAdapter;
    /**
     * Referencia a la lista de la interfaz donde se muestran los partidos para los que se
     * recibieron invitaciones
     */
    @BindView(R.id.recycler_list)
    RecyclerView eventInvitationsList;
    /**
     * Referencia al contenedor de la interfaz mostrado en caso de que no exista ninguna invitación
     */
    @BindView(R.id.list_placeholder)
    ConstraintLayout eventInvitationsPlaceholder;

    /**
     * Constructor sin argumentos
     */
    public InvitationsReceivedFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @return una nueva instancia de InvitationsReceivedFragment
     */
    public static InvitationsReceivedFragment newInstance() {
        return new InvitationsReceivedFragment();
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

        mInvitationsReceivedPresenter = new InvitationsReceivedPresenter(this);
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

        eventInvitationsList.setAdapter(mEventsRecyclerAdapter);
        eventInvitationsList.setHasFixedSize(true);
        eventInvitationsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.event_invitations_received), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Ordena al Presentador que inicie el proceso de consulta de los partidos para los que el
     * usuario ha recibido una invitación que se encuentren en la base de datos.
     */
    @Override
    public void onStart() {
        super.onStart();
        mInvitationsReceivedPresenter.loadEventInvitations(getLoaderManager(), getArguments());
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
     * Establece en el Adaptador los partidos para los que se recibió una invitación, contenidos en
     * el {@link Cursor}, y, si no está vacío, muestra la lista; si está vacío, muestra una imagen
     * que lo indica
     *
     * @param cursor partidos obtenidos en la consulta
     */
    @Override
    public void showEventInvitations(Cursor cursor) {
        mEventsRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            eventInvitationsList.setVisibility(View.VISIBLE);
            eventInvitationsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            eventInvitationsList.setVisibility(View.INVISIBLE);
            eventInvitationsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Instancia e inicia la transición hacia el Fragmento que muestra los detalles del partido
     * seleccionado, {@link DetailEventFragment}.
     *
     * @param eventId Identificador del Partido seleccionado
     */
    @Override
    public void onEventClick(String eventId) {
        Fragment newFragment = DetailEventFragment.newInstance(eventId);
        mFragmentManagementListener.initFragment(newFragment, true);
    }
}
