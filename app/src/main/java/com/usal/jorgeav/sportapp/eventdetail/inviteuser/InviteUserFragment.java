package com.usal.jorgeav.sportapp.eventdetail.inviteuser;

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
 * Fragmento utilizado para mostrar la colección de usuarios a los que enviar una invitación al
 * partido referido. Se encarga de inicializar los componentes de la interfaz para mostrar la
 * colección con la ayuda de {@link UsersAdapter}.
 * Implementa la interfaz {@link InviteUserContract.View} para la comunicación con esta clase
 * y la interfaz {@link UsersAdapter.OnUserItemClickListener} para manejar la pulsación sobre cada
 * uno de los usuarios destinatarios de la invitación.
 */
public class InviteUserFragment extends BaseFragment implements
        InviteUserContract.View,
        UsersAdapter.OnUserItemClickListener {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = InviteUserFragment.class.getSimpleName();
    /**
     * Etiqueta para establecer, en la instanciación del Fragmento, el identificador del evento
     * al que estarán referidas las invitaciones que van a enviarse a los usuarios en la
     * instanciación del Fragmento
     */
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";

    /**
     * Presentador correspondiente a esta Vista
     */
    InviteUserContract.Presenter mSendInvitationPresenter;

    /**
     * Identificador del evento al que van referidas las invitaciones que pretenden enviarse desde
     * este Fragmento
     */
    private static String mEvent = "";

    /**
     * Referencia al elemento de la interfaz donde se listan los posibles usuarios destinatarios
     * de las invitaciones
     */
    @BindView(R.id.recycler_list)
    RecyclerView sendInvitationRecyclerList;
    /**
     * Adaptador para manejar y emplazar los datos de los posibles usuarios destinatarios
     * de las invitaciones en cada una de las celdas de la lista
     */
    UsersAdapter mSendInvitationRecyclerAdapter;
    /**
     * Referencia al elemento de la interfaz que debe mostrarse en caso de que la consulta de
     * usuarios no arroje ningún resultado.
     */
    @BindView(R.id.list_placeholder)
    ConstraintLayout sendInvitationPlaceholder;

    /**
     * Constructor sin argumentos
     */
    public InviteUserFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @param eventId identificador del partido al que hacen referencia las invitaciones
     *
     * @return una nueva instancia de InviteUserFragment
     */
    public static InviteUserFragment newInstance(@NonNull String eventId) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_EVENT_ID, eventId);
        InviteUserFragment fragment = new InviteUserFragment();
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

        mSendInvitationPresenter = new InviteUserPresenter(this);
        mSendInvitationRecyclerAdapter = new UsersAdapter(null, this, Glide.with(this));
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
            mEvent = getArguments().getString(BUNDLE_EVENT_ID);

        sendInvitationRecyclerList.setAdapter(mSendInvitationRecyclerAdapter);
        sendInvitationRecyclerList.setHasFixedSize(true);
        sendInvitationRecyclerList.setLayoutManager(new GridLayoutManager(getActivityContext(),
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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.invite_user), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Ordena al Presentador que inicie el proceso de carga de los posibles usuarios destinatarios
     * de invitaciones que se encuentren en la base de datos.
     */
    @Override
    public void onStart() {
        super.onStart();
        mSendInvitationPresenter.loadFriends(getLoaderManager(), getArguments());
    }

    /**
     * Borra los usuarios almacenados en el Adaptador para que no se guarden en el estado del
     * Fragmento. Son recuperados inmediatamente al volver a mostrar el Fragmento por estar
     * usando el mismo Loader.
     */
    @Override
    public void onPause() {
        super.onPause();
        mSendInvitationRecyclerAdapter.replaceData(null);
    }

    /**
     * Establece en el Adaptador los usuarios contenidos en el {@link Cursor} y, si no está vacío,
     * muestra la lista; si está vacío, muestra una imagen que lo indica
     *
     * @param cursor usuarios obtenidos en la consulta
     */
    @Override
    public void showFriends(Cursor cursor) {
        mSendInvitationRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            sendInvitationRecyclerList.setVisibility(View.VISIBLE);
            sendInvitationPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            sendInvitationRecyclerList.setVisibility(View.INVISIBLE);
            sendInvitationPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Crea y muestra un cuadro de diálogo preguntando si se quiere enviar la invitación al usuario
     * seleccionado. También ofrece la opción de mostrar el perfil del usuario pulsado.
     *
     * @param uid Identificador del usuario pulsado
     */
    @Override
    public void onUserClick(final String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                .setTitle(R.string.dialog_msg_are_you_sure)
                .setPositiveButton(R.string.send_invitation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mSendInvitationPresenter.sendInvitationToThisEvent(mEvent, uid);
                    }
                })
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
