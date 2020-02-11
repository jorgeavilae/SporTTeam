package com.usal.jorgeav.sportapp.profile.friendrequests;

import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
 * Fragmento utilizado para mostrar la colección de usuarios que han enviado una petición de amistad
 * al usuario actual que está pendiente de respuesta. Se encarga de inicializar los componentes de
 * la interfaz para mostrar la colección con la ayuda de {@link UsersAdapter}.
 * <p>
 * Implementa la interfaz {@link FriendRequestsContract.View} para la comunicación con esta clase
 * y la interfaz {@link UsersAdapter.OnUserItemClickListener} para manejar la pulsación sobre un
 * usuario de la colección.
 */
public class FriendRequestsFragment extends BaseFragment implements
        FriendRequestsContract.View,
        UsersAdapter.OnUserItemClickListener {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = FriendRequestsFragment.class.getSimpleName();

    /**
     * Presentador correspondiente a esta Vista
     */
    FriendRequestsContract.Presenter mFriendRequestsPresenter;

    /**
     * Adaptador para la colección de usuarios que se muestra
     */
    UsersAdapter mUsersRecyclerAdapter;
    /**
     * Referencia a la lista de la interfaz donde se muestran los usuarios que mandaron peticiones
     * de amistad
     */
    @BindView(R.id.recycler_list)
    RecyclerView friendRequestsList;
    /**
     * Referencia al contenedor de la interfaz mostrado en caso de que no exista ninguna petición de
     * amistad
     */
    @BindView(R.id.list_placeholder)
    ConstraintLayout friendRequestsPlaceholder;

    /**
     * Constructor sin argumentos
     */
    public FriendRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @return una nueva instancia de FriendRequestsFragment
     */
    public static FriendRequestsFragment newInstance() {
        return new FriendRequestsFragment();
    }

    /**
     * Inicializa el Presentador correspondiente a esta Vista, y el Adaptador para la colección de
     * usuarios.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mFriendRequestsPresenter = new FriendRequestsPresenter(this);
        mUsersRecyclerAdapter = new UsersAdapter(null, this, Glide.with(this));
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

        friendRequestsList.setAdapter(mUsersRecyclerAdapter);
        friendRequestsList.setHasFixedSize(true);
        friendRequestsList.setLayoutManager(new GridLayoutManager(getActivityContext(),
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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.friend_requests_received), this);
        mNavigationDrawerManagementListener.setToolbarAsUp();
    }

    /**
     * Ordena al Presentador que inicie el proceso de carga de los usuarios que mandaron una
     * petición de amistad que se encuentren en la base de datos.
     */
    @Override
    public void onStart() {
        super.onStart();
        mFriendRequestsPresenter.loadFriendRequests(getLoaderManager(), getArguments());
    }

    /**
     * Borra los usuarios almacenados en el Adaptador para que no se guarden en el estado del
     * Fragmento. Son recuperados inmediatamente al volver a mostrar el Fragmento por estar
     * usando el mismo Loader.
     */
    @Override
    public void onPause() {
        super.onPause();
        mUsersRecyclerAdapter.replaceData(null);
    }

    /**
     * Establece en el Adaptador los usuarios que mandaron una petición de amistad, contenidos en
     * el {@link Cursor}, y, si no está vacío, muestra la lista; si está vacío, muestra una imagen
     * que lo indica
     *
     * @param cursor usuarios obtenidos en la consulta
     */
    @Override
    public void showFriendRequests(Cursor cursor) {
        mUsersRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            friendRequestsList.setVisibility(View.VISIBLE);
            friendRequestsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            friendRequestsList.setVisibility(View.INVISIBLE);
            friendRequestsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Instancia e inicia la transición hacia el Fragmento que muestra los detalles del usuario
     * seleccionado, {@link ProfileFragment}.
     *
     * @param uid Identificador del usuario pulsado
     */
    @Override
    public void onUserClick(String uid) {
        Fragment newFragment = ProfileFragment.newInstance(uid);
        mFragmentManagementListener.initFragment(newFragment, true);
    }

    /**
     * Innecesario. No implementado.
     *
     * @param uid Identificador del usuario pulsado
     * @return false
     */
    @Override
    public boolean onUserLongClick(String uid) {
        return false;
    }
}
