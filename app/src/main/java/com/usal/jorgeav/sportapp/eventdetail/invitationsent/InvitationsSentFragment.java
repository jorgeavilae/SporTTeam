package com.usal.jorgeav.sportapp.eventdetail.invitationsent;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar la colección de invitaciones enviadas por el usuario actual.
 * Se encarga de inicializar los componentes de la interfaz para mostrar la colección con la ayuda
 * de {@link UsersAdapter} ya que cada invitación será representada por el usuario al que fue
 * destinada.
 * Implementa la interfaz {@link InvitationsSentContract.View} para la comunicación con esta clase
 * y la interfaz {@link UsersAdapter.OnUserItemClickListener} para manejar la pulsación sobre cada
 * uno de los usuarios destinatarios de la invitación.
 */
public class InvitationsSentFragment extends BaseFragment implements
        InvitationsSentContract.View,
        UsersAdapter.OnUserItemClickListener {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = InvitationsSentFragment.class.getSimpleName();
    /**
     * Etiqueta para establecer el identificador del evento al que están referidas las invitaciones
     * que deben mostrarse en la instanciación del Fragmento
     */
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";

    /**
     * Presentador correspondiente a esta Vista
     */
    InvitationsSentContract.Presenter mEventInvitationsPresenter;

    /**
     * Identificador del evento al que van referidas las invitaciones mostradas en este Fragmento
     */
    private static String mEventId = "";

    /**
     * Adaptador para manejar y emplazar los datos de los usuario destinatarios de las invitaciones
     * en cada una de las celdas de la lista
     */
    UsersAdapter mUsersAdapter;
    /**
     * Referencia al elemento de la interfaz donde se lista los usuario destinatarios de las
     * invitaciones
     */
    @BindView(R.id.recycler_list)
    RecyclerView userInvitationsSentList;
    /**
     * Referencia al elemento de la interfaz que debe mostrarse en caso de que la consulta de
     * invitaciones no arroje ningún resultado.
     */
    @BindView(R.id.list_placeholder)
    ConstraintLayout userInvitationsSentPlaceholder;

    /**
     * Constructor sin argumentos
     */
    public InvitationsSentFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @param eventId identificador del partido al que hacen referencia las invitaciones
     *
     * @return una nueva instancia de InvitationsSentFragment
     */
    public static InvitationsSentFragment newInstance(@NonNull String eventId) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_EVENT_ID, eventId);
        InvitationsSentFragment fragment = new InvitationsSentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inicializa el Presentador correspondiente a esta Vista, y el Adaptador para la colección de
     * usuarios.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mEventInvitationsPresenter = new InvitationsSentPresenter(this);
        mUsersAdapter = new UsersAdapter(null, this, Glide.with(this));
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
     * Inicializa y obtiene una referencia a los elementos de la interfaz. Establece el adaptador
     * creado como adaptador de la lista de la interfaz recién inflada.
     *
     * @param inflater utilizado para inflar el archivo de layout
     * @param container contenedor donde se va a incluir la interfaz o null
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     *
     * @return la vista de la interfaz inicializada
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, root);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_EVENT_ID))
            mEventId = getArguments().getString(BUNDLE_EVENT_ID);

        userInvitationsSentList.setAdapter(mUsersAdapter);
        userInvitationsSentList.setHasFixedSize(true);
        userInvitationsSentList.setLayoutManager(new GridLayoutManager(getActivityContext(),
                Utiles.calculateNoOfColumns(getActivityContext())));

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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.action_unanswered_invitations), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Ordena al Presentador que inicie el proceso de carga de los usuarios destinatarios de
     * invitaciones que se encuentren en la base de datos.
     */
    @Override
    public void onStart() {
        super.onStart();
        mEventInvitationsPresenter.loadEventInvitationsSent(getLoaderManager(), getArguments());
    }

    /**
     * Borra los usuarios almacenados en el Adaptador para que no se guarden en el estado del
     * Fragmento. Son recuperados inmediatamente al volver a mostrar el Fragmento por estar
     * usando el mismo Loader.
     */
    @Override
    public void onPause() {
        super.onPause();
        mUsersAdapter.replaceData(null);
    }

    /**
     * Establece en el Adaptador los usuarios contenidos en el {@link Cursor} y, si no está vacío,
     * muestra la lista; si está vacío, muestra una imagen que lo indica
     *
     * @param cursor usuarios obtenidos en la consulta
     */
    @Override
    public void showEventInvitationsSent(Cursor cursor) {
        mUsersAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            userInvitationsSentList.setVisibility(View.VISIBLE);
            userInvitationsSentPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            userInvitationsSentList.setVisibility(View.INVISIBLE);
            userInvitationsSentPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Crea y muestra un cuadro de diálogo preguntando si se quiere borrar la invitación que se
     * envió al usuario seleccionado. También ofrece la opción de mostrar el perfil del usuario
     * pulsado.
     *
     * @param uid Identificador del usuario pulsado
     */
    @Override
    public void onUserClick(final String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                .setMessage(R.string.dialog_msg_cancel_invitation)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mEventInvitationsPresenter.deleteInvitationToThisEvent(mEventId, uid);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setNeutralButton(R.string.see_details, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Fragment newFragment = ProfileFragment.newInstance(uid);
                        mFragmentManagementListener.initFragment(newFragment, true);
                    }
                });
        builder.create().show();
    }

    /**
     * No realiza ninguna acción
     *
     * @param uid Identificador del usuario pulsado
     *
     * @return false
     */
    @Override
    public boolean onUserLongClick(String uid) {
        return false;
    }
}
