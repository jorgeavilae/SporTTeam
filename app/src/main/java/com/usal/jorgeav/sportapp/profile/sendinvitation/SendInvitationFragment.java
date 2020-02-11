package com.usal.jorgeav.sportapp.profile.sendinvitation;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
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
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar la colección de partidos para los que el usuario actual puede
 * enviar una invitación al usuario mostrado. Se encarga de inicializar los componentes de la
 * interfaz para mostrar la colección con la ayuda de {@link EventsAdapter}.
 * <p>
 * Implementa la interfaz {@link SendInvitationContract.View} para la comunicación con esta
 * clase y la interfaz {@link EventsAdapter.OnEventItemClickListener} para manejar la pulsación
 * sobre un partido de la colección.
 */
public class SendInvitationFragment extends BaseFragment implements
        SendInvitationContract.View,
        EventsAdapter.OnEventItemClickListener {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = SendInvitationFragment.class.getSimpleName();
    /**
     * Etiqueta para establecer, en la instanciación del Fragmento, el identificador del usuario
     * al que estarán destinadas las invitaciones que van a enviarse para los partidos.
     */
    public static final String BUNDLE_INSTANCE_UID = "BUNDLE_INSTANCE_UID";

    /**
     * Identificador del usuario al que van destinadas las invitaciones que pretenden enviarse desde
     * este Fragmento
     */
    private String mUserId;

    /**
     * Presentador correspondiente a esta Vista
     */
    SendInvitationContract.Presenter mSendInvitationPresenter;

    /**
     * Referencia al elemento de la interfaz donde se listan los posibles partidos a los que referir
     * las invitaciones
     */
    @BindView(R.id.recycler_list)
    RecyclerView sendInvitationList;
    /**
     * Adaptador para manejar y emplazar, en cada una de las celdas de la lista, los datos de los
     * partidos a los que referir las invitaciones
     */
    EventsAdapter mEventsRecyclerAdapter;
    /**
     * Referencia al elemento de la interfaz que debe mostrarse en caso de que la consulta no
     * arroje ningún resultado.
     */
    @BindView(R.id.list_placeholder)
    ConstraintLayout sendInvitationPlaceholder;

    /**
     * Constructor sin argumentos
     */
    public SendInvitationFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @param uid identificador del usuario al que van a ir destinadas las invitaciones
     * @return una nueva instancia de SendInvitationFragment
     */
    public static SendInvitationFragment newInstance(@NonNull String uid) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_INSTANCE_UID, uid);
        SendInvitationFragment fragment = new SendInvitationFragment();
        fragment.setArguments(args);
        return fragment;
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

        mSendInvitationPresenter = new SendInvitationPresenter(this);
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

        if (getArguments() != null && getArguments().containsKey(BUNDLE_INSTANCE_UID))
            mUserId = getArguments().getString(BUNDLE_INSTANCE_UID);

        sendInvitationList.setAdapter(mEventsRecyclerAdapter);
        sendInvitationList.setHasFixedSize(true);
        sendInvitationList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.pick_event), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Ordena al Presentador que inicie el proceso de consulta a la base de datos sobre
     * los partidos para los que el usuario actual puede enviar una invitación y que se encuentren
     * en la base de datos.
     */
    @Override
    public void onStart() {
        super.onStart();
        mSendInvitationPresenter.loadEventsForInvitation(getLoaderManager(), getArguments());
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
     * Muestra los partidos para los que el usuario actual puede enviar invitaciones,
     * contenidos en el {@link Cursor}. Serán todos los partidos en los que el usuario actual
     * participe menos los que ya tengan alguna relación con el usuario mostrado. Establece en el
     * Adaptador los usuarios contenidos en el {@link Cursor} y, si no está vacío, muestra la lista;
     * si está vacío, muestra una imagen que lo indica.
     *
     * @param cursor partidos obtenidos en la consulta
     */
    @Override
    public void showEventsForInvitation(Cursor cursor) {
        mEventsRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            sendInvitationList.setVisibility(View.VISIBLE);
            sendInvitationPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            sendInvitationList.setVisibility(View.INVISIBLE);
            sendInvitationPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Crea y muestra un cuadro de diálogo pregunta si se desea invitar al usuario al partido
     * seleccionado. Si acepta, se inicia el proceso de envío de invitación a través del Presentador.
     * También puede seleccionar la opción de ver detalles, que instancia e inicia la transición
     * hacia el Fragmento que muestra los detalles del partido seleccionado.
     *
     * @param eventId Identificador del Partido seleccionado
     */
    @Override
    public void onEventClick(final String eventId) {
        String userName = UtilesContentProvider.getUserNameFromContentProvider(mUserId);
        if (userName != null && !TextUtils.isEmpty(userName)) {
            String msg = getString(R.string.dialog_msg_send_invitation_to_user);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                    .setMessage(String.format(msg, userName))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mSendInvitationPresenter.sendInvitationToThisUser(eventId, mUserId);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setNeutralButton(R.string.see_details, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Fragment newFragment = DetailEventFragment.newInstance(eventId);
                            mFragmentManagementListener.initFragment(newFragment, true);
                        }
                    });
            builder.create().show();
        }
    }
}
