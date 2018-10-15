package com.usal.jorgeav.sportapp.eventdetail.userrequests;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar la colección de usuarios que mandaron peticiones de participación
 * al partido. En una lista muestra los usuarios que tiene la petición pendiente de contestar y en
 * otra lista se muestran los usuarios cuyas peticiones fueron rechazadas.
 * <p>
 * Este Fragmento se encarga de inicializar los componentes de la interfaz para mostrar esa
 * colección en las dos listas, con la ayuda de dos {@link UsersAdapter}.
 * <p>
 * Desde este Fragmento, el creador del partido puede aceptar o rechazar peticiones de
 * participación y desbloquear usuarios cuya petición fue rechazada.
 * <p>
 * Implementa la interfaz {@link UsersRequestsContract.View} para la comunicación con esta clase.
 * También crea dos {@link UsersAdapter.OnUserItemClickListener} separados para mostrar un cuadro de
 * diálogo diferente según se pulse sobre un usuario con una petición pendiente o sobre un usuario
 * con una petición rechazada.
 */
public class UsersRequestsFragment extends BaseFragment implements
        UsersRequestsContract.View {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = UsersRequestsFragment.class.getSimpleName();
    /**
     * Etiqueta para establecer, en la instanciación del Fragmento, el identificador del partido en
     * al que van dirigidas las peticiones de participación.
     */
    public static final String BUNDLE_EVENT_ID = "BUNDLE_EVENT_ID";

    /**
     * Presentador correspondiente a esta Vista
     */
    UsersRequestsContract.Presenter mUsersRequestsPresenter;

    /**
     * Identificador del partido
     */
    private static String mEventId = "";

    /**
     * Adaptador para manejar y emplazar los datos de los usuarios que tienen petición de
     * participación pendiente en cada una de las celdas de la lista
     */
    UsersAdapter mUsersRequestRecyclerAdapter;
    /**
     * Referencia al elemento de la interfaz donde se listan los usuarios que tienen petición de
     * participación pendiente
     */
    @BindView(R.id.user_requests_list)
    RecyclerView usersRequestsList;
    /**
     * Referencia al elemento de la interfaz que debe mostrarse en caso de que la consulta de
     * los usuarios que tienen petición de participación pendiente no arroje ningún resultado.
     */
    @BindView(R.id.user_requests_placeholder)
    ConstraintLayout userRequestPlaceholder;

    /**
     * Adaptador para manejar y emplazar los datos de los usuarios que tienen la petición de
     * participación rechazada en cada una de las celdas de la lista
     */
    UsersAdapter mUsersRejectedRecyclerAdapter;
    /**
     * Referencia al elemento de la interfaz donde se listan los usuarios que tienen la petición de
     * participación rechazada
     */
    @BindView(R.id.user_rejected_list)
    RecyclerView usersRejectedList;
    /**
     * Referencia al elemento de la interfaz que debe mostrarse en caso de que la consulta de
     * los usuarios que tienen la petición de participación rechazada no arroje ningún resultado.
     */
    @BindView(R.id.user_rejected_placeholder)
    ConstraintLayout usersRejectedPlaceholder;

    /**
     * Constructor sin argumentos
     */
    public UsersRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @param eventID identificador del partido al que van dirigidas las peticiones de participación
     * @return una nueva instancia de UsersRequestsFragment
     */
    public static UsersRequestsFragment newInstance(@NonNull String eventID) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_EVENT_ID, eventID);
        UsersRequestsFragment fragment = new UsersRequestsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inicializa el Presentador correspondiente a esta Vista y los Adaptadores para las
     * colecciones de usuarios con peticiones pendientes y con peticiones rechazadas.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mUsersRequestsPresenter = new UsersRequestsPresenter(this);

        mUsersRequestRecyclerAdapter = new UsersAdapter(null, new UsersAdapter.OnUserItemClickListener() {
            @Override
            public void onUserClick(final String uid) {
                clickOnUserRequest(uid);
            }

            @Override
            public boolean onUserLongClick(String uid) {
                return false;
            }
        }, Glide.with(this));

        mUsersRejectedRecyclerAdapter = new UsersAdapter(null, new UsersAdapter.OnUserItemClickListener() {
            @Override
            public void onUserClick(final String uid) {
                clickOnUserRejected(uid);
            }

            @Override
            public boolean onUserLongClick(String uid) {
                return false;
            }
        }, Glide.with(this));
    }

    /**
     * Establece el comportamiento que debe producirse cuando se pulsa sobre un usuario que tiene
     * una petición de participación pendiente de respuesta. Se muestra un cuadro de diálogo con
     * las opciones de aceptar o rechazar la petición, y se realizan dichas acciones con la ayuda
     * del Presentador de esta Vista. También se da la opción de viajar hasta el Fragmento donde se
     * muestran los detalles del usuario pulsado.
     *
     * @param uid identificador del usuario pulsado
     */
    private void clickOnUserRequest(final String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                .setMessage(R.string.dialog_msg_accept_participant)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mUsersRequestsPresenter.acceptUserRequestToThisEvent(mEventId, uid);
                        mUsersRequestsPresenter.loadUsersRequests(getLoaderManager(), getArguments());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUsersRequestsPresenter.declineUserRequestToThisEvent(mEventId, uid);
                        mUsersRequestsPresenter.loadUsersRequests(getLoaderManager(), getArguments());
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
     * Establece el comportamiento que debe producirse cuando se pulsa sobre un usuario que tiene
     * una petición de participación rechazada por el creador del partido. Se muestra un cuadro de
     * diálogo con la opción de desbloquear al usuario, y se realiza dicha acción con la ayuda del
     * Presentador de esta Vista. También se da la opción de viajar hasta el Fragmento donde se
     * muestran los detalles del usuario pulsado.
     *
     * @param uid identificador del usuario pulsado
     */
    private void clickOnUserRejected(final String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext())
                .setMessage(R.string.dialog_msg_unblock_participant)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mUsersRequestsPresenter.unblockUserParticipationRejectedToThisEvent(mEventId, uid);
                        mUsersRequestsPresenter.loadUsersRejected(getLoaderManager(), getArguments());
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
     * ButterKnife. Establece los adaptadores creados como adaptador de cada lista. Extrae el
     * identificador del partido incluido en el Fragmento en su instanciación.
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
        View root = inflater.inflate(R.layout.fragment_user_request, container, false);
        ButterKnife.bind(this, root);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_EVENT_ID))
            mEventId = getArguments().getString(BUNDLE_EVENT_ID);

        usersRequestsList.setAdapter(mUsersRequestRecyclerAdapter);
        usersRequestsList.setHasFixedSize(true);
        usersRequestsList.setLayoutManager(new GridLayoutManager(getActivityContext(),
                Utiles.calculateNoOfColumns(getActivityContext())));

        usersRejectedList.setAdapter(mUsersRejectedRecyclerAdapter);
        usersRejectedList.setHasFixedSize(true);
        usersRejectedList.setLayoutManager(new GridLayoutManager(getActivityContext(),
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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.user_request), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Ordena al Presentador que inicie el proceso de carga de los usuarios con peticiones
     * pendientes y con peticiones rechazadas que se encuentren en la base de datos.
     */
    @Override
    public void onStart() {
        super.onStart();
        mUsersRequestsPresenter.loadUsersRequests(getLoaderManager(), getArguments());
        mUsersRequestsPresenter.loadUsersRejected(getLoaderManager(), getArguments());
    }

    /**
     * Borra los usuarios almacenados en los Adaptadores para que no se guarden en el estado del
     * Fragmento. Son recuperados inmediatamente al volver a mostrar el Fragmento por estar
     * usando el mismo Loader.
     */
    @Override
    public void onPause() {
        super.onPause();
        mUsersRequestRecyclerAdapter.replaceData(null);
        mUsersRejectedRecyclerAdapter.replaceData(null);
    }

    /**
     * Establece en el Adaptador de la lista de usuarios con peticiones de participación pendientes
     * contenidos en el {@link Cursor} y, si no está vacío, muestra la lista; si está vacío, muestra
     * una imagen que lo indica
     *
     * @param cursor usuarios con peticiones de participación pendientes obtenidos en la consulta
     */
    @Override
    public void showUsersRequests(Cursor cursor) {
        mUsersRequestRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            usersRequestsList.setVisibility(View.VISIBLE);
            userRequestPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            usersRequestsList.setVisibility(View.INVISIBLE);
            userRequestPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Establece en el Adaptador de la lista de usuarios con la petición de participación rechazada
     * contenidos en el {@link Cursor} y, si no está vacío, muestra la lista; si está vacío, muestra
     * una imagen que lo indica
     *
     * @param cursor usuarios con la petición de participación rechazada obtenidos en la consulta
     */
    @Override
    public void showRejectedUsers(Cursor cursor) {
        mUsersRejectedRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            usersRejectedList.setVisibility(View.VISIBLE);
            usersRejectedPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            usersRejectedList.setVisibility(View.INVISIBLE);
            usersRejectedPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Muestra el mensaje especificado en la interfaz mediante un {@link Toast}. Aunque la llamada
     * se produzca desde otro hilo, la operación sobre la interfaz para mostrar el mensaje debe
     * ejecutarse desde el hilo principal.
     *
     * @param msgResource identificador del recurso de texto correspondiente al mensaje que se
     *                    quiere mostrar
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#runOnUiThread(java.lang.Runnable)">
     * runOnUiThread(java.lang.Runnable)</a>
     */
    @Override
    public void showMsgFromBackgroundThread(final int msgResource) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msgResource, Toast.LENGTH_SHORT).show();
                mUsersRequestsPresenter.loadUsersRequests(getLoaderManager(), getArguments());
                mUsersRequestsPresenter.loadUsersRejected(getLoaderManager(), getArguments());
            }
        });
    }
}
