package com.usal.jorgeav.sportapp.friends;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.adapters.UsersAdapter;
import com.usal.jorgeav.sportapp.friends.searchuser.SearchUsersFragment;
import com.usal.jorgeav.sportapp.profile.ProfileFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragmento utilizado para mostrar la colección de amigos del usuario actual. Se encarga de
 * inicializar los componentes de la interfaz para mostrar la colección con la ayuda de
 * {@link UsersAdapter}.
 * <p>
 * Implementa la interfaz {@link FriendsContract.View} para la comunicación con esta clase y la
 * interfaz {@link UsersAdapter.OnUserItemClickListener} para manejar la pulsación sobre un
 * usuario de la colección.
 */
public class FriendsFragment extends BaseFragment implements
        FriendsContract.View,
        UsersAdapter.OnUserItemClickListener {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = FriendsFragment.class.getSimpleName();

    /**
     * Presentador correspondiente a esta Vista
     */
    FriendsContract.Presenter mFriendsPresenter;

    /**
     * Adaptador para la colección de usuarios que se muestra
     */
    UsersAdapter mFriendsRecyclerAdapter;

    /**
     * Referencia a la lista de la interfaz donde se muestran los amigos
     */
    @BindView(R.id.friends_list)
    RecyclerView friendsRecyclerList;
    /**
     * Referencia al contenedor de la interfaz mostrado en caso de que no exista ningún amigo
     */
    @BindView(R.id.friends_placeholder)
    ConstraintLayout friendsPlaceholder;

    /**
     * Constructor sin argumentos
     */
    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Método de instanciación del Fragmento
     *
     * @return una nueva instancia de FriendsFragment
     */
    public static FriendsFragment newInstance() {
        return new FriendsFragment();
    }

    /**
     * Inicializa el Presentador correspondiente a esta vista, y el Adaptador para la colección de
     * usuarios.
     *
     * @param savedInstanceState estado del Fragmento guardado en una posible rotación de
     *                           la pantalla, o null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mFriendsPresenter = new FriendsPresenter(this);
        mFriendsRecyclerAdapter = new UsersAdapter(null, this, Glide.with(this));
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
        inflater.inflate(R.menu.menu_search_users, menu);
    }

    /**
     * Invocado cuando un elemento del menú es pulsado. En este caso se encarga de iniciar la
     * transición hacia el Fragmento de búsqueda de usuarios.
     *
     * @param item elemento del menú pulsado
     * @return true si se aceptó la pulsación, false en otro caso
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_search_users) {
            Fragment fragment = SearchUsersFragment.newInstance();
            mFragmentManagementListener.initFragment(fragment, true);
            return true;
        }
        return false;
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
        View root = inflater.inflate(R.layout.fragment_users, container, false);
        ButterKnife.bind(this, root);

        friendsRecyclerList.setAdapter(mFriendsRecyclerAdapter);
        friendsRecyclerList.setHasFixedSize(true);
        friendsRecyclerList.setLayoutManager(new GridLayoutManager(getActivityContext(),
                Utiles.calculateNoOfColumns(getActivityContext())));

        return root;
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
        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.friends), this);
        mNavigationDrawerManagementListener.setToolbarAsNav();
    }

    /**
     * Ordena al Presentador que inicie el proceso de consulta a la base de datos de los amigos del
     * usuario actual.
     */
    @Override
    public void onStart() {
        super.onStart();
        mFriendsPresenter.loadFriend(getLoaderManager(), getArguments());
    }

    /**
     * Borra los usuarios almacenados en el Adaptador para que no se guarden en el estado del
     * Fragmento. Son recuperadas inmediatamente al volver a mostrar el Fragmento por estar
     * usando el mismo Loader.
     */
    @Override
    public void onPause() {
        super.onPause();
        mFriendsRecyclerAdapter.replaceData(null);
    }

    /**
     * Establece en el Adaptador los usuarios contenidos en el {@link Cursor} y, si no está vacío,
     * muestra la lista; si está vacío, muestra una imagen indicándolo
     *
     * @param cursor usuarios obtenidos en la consulta
     */
    @Override
    public void showFriends(Cursor cursor) {
        mFriendsRecyclerAdapter.replaceData(cursor);
        if (cursor != null && cursor.getCount() > 0) {
            friendsRecyclerList.setVisibility(View.VISIBLE);
            friendsPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            friendsRecyclerList.setVisibility(View.INVISIBLE);
            friendsPlaceholder.setVisibility(View.VISIBLE);
        }
        mFragmentManagementListener.showContent();
    }

    /**
     * Inicia la transición hacia el Fragmento utilizado para mostrar los detalles del usuario
     * pulsado, {@link ProfileFragment}.
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
